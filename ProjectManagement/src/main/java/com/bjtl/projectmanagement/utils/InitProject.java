package com.bjtl.projectmanagement.utils;

import com.alibaba.fastjson.JSON;
import com.bjtl.projectmanagement.common.ElasticSearchInfo;
import com.bjtl.projectmanagement.config.ElasticSearchConfig;
import com.bjtl.projectmanagement.mapper.BuildPropertiesMapper;
import com.bjtl.projectmanagement.mapper.UnitMapper;
import com.bjtl.projectmanagement.model.BuildPropertiesVO;
import com.bjtl.projectmanagement.model.ProjectVO;
import com.bjtl.projectmanagement.model.UnitVO;
import com.bjtl.projectmanagement.service.ProjectService;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description: 启动类实现方法
 * @Author: leitianquan
 * @Date: 2020/06/27
 **/
@Configuration
public class InitProject implements ApplicationRunner {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BuildPropertiesMapper buildPropertiesMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 删除索引库，该方法中已判断是否存在，对异常已处理
        ElasticSearchUtil.deleteIndex(ElasticSearchInfo.INDEX_BUILD_NAME);
        ElasticSearchUtil.deleteIndex(ElasticSearchInfo.INDEX_PROJECT_NAME);
        ElasticSearchUtil.deleteIndex(ElasticSearchInfo.INDEX_UNIT_NAME);
        // 创建索引
        ElasticSearchUtil.createIndex(ElasticSearchInfo.INDEX_BUILD_NAME);
        ElasticSearchUtil.createIndex(ElasticSearchInfo.INDEX_PROJECT_NAME);
        ElasticSearchUtil.createIndex(ElasticSearchInfo.INDEX_UNIT_NAME);
        // 设置mappings
        ElasticSearchUtil.addIndexMapping(ElasticSearchInfo.INDEX_BUILD_NAME, ElasticSearchInfo.TYPE_BUILD_NAME, ElasticSearchUtil.getBuildMapping(ElasticSearchInfo.TYPE_BUILD_NAME));
        ElasticSearchUtil.addIndexMapping(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME, ElasticSearchUtil.getProjectMapping(ElasticSearchInfo.TYPE_PROJECT_NAME));
        ElasticSearchUtil.addIndexMapping(ElasticSearchInfo.INDEX_UNIT_NAME, ElasticSearchInfo.TYPE_UNIT_NAME, ElasticSearchUtil.getProjectMapping(ElasticSearchInfo.TYPE_UNIT_NAME));
        // 导入数据到ES
        importBuildToElasticSearch(ElasticSearchInfo.INDEX_BUILD_NAME, ElasticSearchInfo.TYPE_BUILD_NAME);
        importProjectToElasticSearch(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME);
        importUnitToElasticSearch(ElasticSearchInfo.INDEX_UNIT_NAME, ElasticSearchInfo.TYPE_UNIT_NAME);
    }

    /**
     * 将Project所需数据导入ES
     *
     * @param indexName 索引
     * @param typeName  类型
     */
    public void importProjectToElasticSearch(String indexName, String typeName) {
        List<ProjectVO> list = projectService.listAllProject();
        TransportClient client = ElasticSearchConfig.getInstance();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < list.size(); i++) {
            //执行添加操作 提前设置ID
            bulkRequestBuilder.add(client.prepareIndex(indexName, typeName, String.valueOf(list.get(i).getProjectId()))
                    .setSource(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());
        if (responses.hasFailures()) {
            System.out.println("批量导入数据到ES失败");
        } else {
            System.out.println(responses.getItems().length + "条数据插入完成！");
        }
    }

    /**
     * 将BuildProperties所需数据导入ES
     *
     * @param indexName 索引
     * @param typeName  类型
     */
    public void importBuildToElasticSearch(String indexName, String typeName) {
        List<BuildPropertiesVO> list = buildPropertiesMapper.listBuildProperties();
        TransportClient client = ElasticSearchConfig.getInstance();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < list.size(); i++) {
            //执行添加操作 提前设置ID
            bulkRequestBuilder.add(client.prepareIndex(indexName, typeName, String.valueOf(list.get(i).getId()))
                    .setSource(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());
        if (responses.hasFailures()) {
            System.out.println("批量导入数据到ES失败");
        } else {
            System.out.println(responses.getItems().length + "条数据插入完成！");
        }
    }

    /**
     * 将Unit所需数据导入ES
     *
     * @param indexName 索引
     * @param typeName  类型
     */
    public void importUnitToElasticSearch(String indexName, String typeName) {
        List<UnitVO> list = unitMapper.listAllTreeNode();
        TransportClient client = ElasticSearchConfig.getInstance();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (int i = 0; i < list.size(); i++) {
            //执行添加操作 提前设置ID
            bulkRequestBuilder.add(client.prepareIndex(indexName, typeName, String.valueOf(list.get(i).getId()))
                    .setSource(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());
        if (responses.hasFailures()) {
            System.out.println("批量导入数据到ES失败");
        } else {
            System.out.println(responses.getItems().length + "条数据插入完成！");
        }
    }
}
