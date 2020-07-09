package com.bjtl.utils;

import com.alibaba.fastjson.JSON;
import com.bjtl.config.ElasticSearchConfig;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @Description: ElasticSearch工具类
 * @Author: leitianquan
 * @Date: 2020/06/27
 **/
public class ElasticSearchUtil {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchUtil.class);

    private static TransportClient client = ElasticSearchConfig.getInstance();

    /**
     * 创建索引
     *
     * @param indexName 索引名
     */
    public static void createIndex(String indexName) {
        try {
            if (indexExist(indexName)) {
                logger.info("索引：" + indexName + " 已存在！");
            } else {
                CreateIndexResponse createIndexResponse = client.admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
                if (createIndexResponse.isAcknowledged()) {
                    logger.info("索引 " + indexName + "创建成功！");
                } else {
                    logger.info("索引 " + indexName + "创建失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引
     *
     * @param index 索引名
     */
    public static void deleteIndex(String index) {
        try {
            if (indexExist(index)) {
                DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index)
                        .execute().actionGet();
                if (!dResponse.isAcknowledged()) {
                    logger.info("failed to delete index " + index + "!");
                } else {
                    logger.info("delete index " + index + " successfully!");
                }
            } else {
                logger.error("the index " + index + " not exists!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证索引是否存在
     *
     * @param index 索引名
     */
    public static boolean indexExist(String index) {
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse inExistsResponse = client.admin().indices()
                .exists(inExistsRequest).actionGet();
        return inExistsResponse.isExists();
    }

    /**
     * 给索引添加mappings
     *
     * @param indexName 索引名称
     * @param typeName  类型名称
     * @throws IOException
     */
    public static void addIndexMapping(String indexName, String typeName, XContentBuilder mapping) throws IOException {
        PutMappingRequest mappingRequest = Requests.putMappingRequest(indexName).type(typeName).source(mapping);
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }

    /**
     * 插入数据
     *
     * @param index 索引名
     * @param type  类型
     * @param json  数据
     */
    public static void insertData(String index, String type, String json) {
        IndexResponse response = client.prepareIndex(index, type)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .setSource(json, XContentType.JSON)
                .get();
        System.out.println(response.getVersion());
    }

    /**
     * 通过prepareIndex增加文档，参数为json字符串
     *
     * @param index 索引名
     * @param type  类型
     * @param id    数据id
     * @param json  数据
     */
    public static void insertData(String index, String type, String id, String json) {
        IndexResponse indexResponse = client.prepareIndex()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                // 设置文档索引
                .setIndex(index)
                // 设置文档Type
                .setType(type)
                // 设置文档ID
                .setId(id)
                // 设置文档信息
                .setSource(json, XContentType.JSON)
                // 执行方法
                .get();
        System.out.println(indexResponse.getVersion());
        logger.info("数据插入ES成功！");
    }

    /**
     * 批量添加到数据库中
     *
     * @param indexName 索引
     * @param typeName  类型
     * @param list      需要添加的数据
     */
    public static void insertBatchData(String indexName, String typeName, List list) {
        // 批量添加ES
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < list.size(); i++) {
            //执行添加操作 提前设置ID
          /*  bulkRequestBuilder.add(client.prepareIndex(indexName, typeName, UUID.randomUUID().toString().replaceAll("-", ""))
                    .setSource(JSON.toJSONString(list.get(i)), XContentType.JSON)*/
            bulkRequestBuilder.add(client.prepareIndex(indexName, typeName)
                    .setSource(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());
        if (responses.hasFailures()) {
            logger.info("批量导入数据到ES失败");
        } else {
            System.out.println(responses.getItems().length + "条数据插入完成！");
        }
    }

    /**
     * 更新数据
     *
     * @param index 索引名
     * @param type  类型
     * @param id    数据id
     * @param json  数据
     */
    public static void updateData(String index, String type, String id, String json) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(json, XContentType.JSON);
            // 修改后立刻更新
            updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            client.update(updateRequest).get();
        } catch (Exception e) {
            logger.error("update data failed." + e.getMessage());
        }
    }

    /**
     * 删除指定数据
     *
     * @param index 索引名
     * @param type  类型
     * @param id    数据id
     */
    public static void deleteData(String index, String type, String id) {
        try {
            DeleteResponse response = client.prepareDelete(index, type, id).get();
            System.out.println(response.isFragment());
            logger.info("删除指定数据成功！");
        } catch (Exception e) {
            logger.error("删除指定数据失败！" + e);
        }
    }

    /**
     * 删除索引类型表所有数据，批量删除
     *
     * @param index
     * @param type
     */
    public static void deleteIndexTypeAllData(String index, String type) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.matchAllQuery()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000)).setSize(10000).setExplain(false).execute().actionGet();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        while (true) {
            SearchHit[] hitArray = response.getHits().getHits();
            SearchHit hit = null;
            for (int i = 0, len = hitArray.length; i < len; i++) {
                hit = hitArray[i];
                DeleteRequestBuilder request = client.prepareDelete(index, type, hit.getId());
                bulkRequest.add(request);
            }
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                logger.error(bulkResponse.buildFailureMessage());
            }
            if (hitArray.length == 0) break;
            response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
        }
    }
}
