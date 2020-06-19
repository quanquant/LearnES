package com.bjtl.learnes;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
public class LearnEsApplicationTests {

    // 创建索引
    @Test
    public void contextLoads() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // 创建索引
        // 创建一个称为alei的索引
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("lei");
        CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
        // 查看索引是否创建成功
        System.out.println(response.isAcknowledged());

      /* // 数据查询
        GetResponse response =client.prepareGet("books","library","1").execute().actionGet();

        // 得到查询的数据
        System.out.println(response.getSourceAsString());
*/
        client.close();

    }

    // 给创建好的索引 添加mappings
    @Test
    public void testAddIndexMapping() throws IOException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        PutMappingRequest mappingRequest = Requests.putMappingRequest("learn").type("quan").source(getCnMapping("quan"));
        PutMappingResponse putMappingResponse = client.admin().indices().putMapping(mappingRequest).actionGet();
        client.close();
    }

    // 删除索引
    @Test
    public void testDeleteIndex() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        IndicesAdminClient admin = client.admin().indices();
        admin.prepareDelete("alei").execute().actionGet().isAcknowledged();
    }

    // 添加文档
    @Test
    public void testAdd() throws IOException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        XContentBuilder doc = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", "1")
                .field("title", "java设计模式")
                .field("content", "好好学习，天天向上")
                .field("postdate", "2020-05-20")
                .field("url", "www.baidu.com")
                .endObject();
        // 添加文档  这里的id如果设置为null  则id由ES自动生成
        IndexResponse response = client.prepareIndex("learn", "quan", "1")
                .setSource(doc).get();
        // 查看创建是否成功 created说明成功
        System.out.println(response.status());
        client.close();
    }

    // 删除文档
    @Test
    public void testDeleteDocument() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        //DeleteResponse response = client.prepareDelete("lei", "quan", "10").get();
        DeleteResponse response = client.prepareDelete("lei", "quan", "AXLLnbGOeS6OPcKj95kt").get();
        // 输出ok说明删除成功
        System.out.println(response.status());
    }

    // 更新文档
    @Test
    public void testUpdateDocument() throws IOException, ExecutionException, InterruptedException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        UpdateRequest request = new UpdateRequest();
        // 相当于post方式的更新
        request.index("lei")
                .type("quan")
                .id("10")
                .doc(
                        XContentFactory.jsonBuilder().startObject()
                                .field("title", "单例设计模式")
                                .endObject()
                );
        UpdateResponse response = client.update(request).get();

        System.out.println(response.status());
    }

    //************************************************
    // upsert方式更新，如果文档存在执行更新，如果文档不存在执行添加  *
    @Test
    public void testUpdateDocument2() throws IOException, ExecutionException, InterruptedException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // 添加文档
        IndexRequest request1 = new IndexRequest("lei", "quan", "6")
                .source(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("id", "2")
                                .field("title", "工厂设计模式")
                                .field("content", "good good study，day day up")
                                .field("postdate", "2020-05-20")
                                .field("url", "www.baidu.com")
                                .endObject()
                );
        // 如果更新查不到对应id的文档，则执行添加操作 upsert(request1)
        UpdateRequest request2 = new UpdateRequest("lei", "quan", "6")
                .doc(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "嗯哼")
                                .endObject()
                ).upsert(request1);
        UpdateResponse response = client.update(request2).get();

        System.out.println(response.status());
    }

    //mget批量查询
    @Test
    public void testBatchQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        MultiGetResponse responses = client.prepareMultiGet()
                .add("lei", "quan", "10", "11")
                .add("books", "library", "14", "15", "16").get();
        for (MultiGetItemResponse respons : responses) {
            GetResponse getResponse = respons.getResponse();
            if (getResponse != null && getResponse.isExists()) {
                System.out.println(getResponse.getSourceAsString());
            }
        }
    }

    // bulk批量操作
    @Test
    public void testBatchAdd() throws IOException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // 批量添加
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        bulkRequestBuilder.add(client.prepareIndex("lei", "quan", "8")
                .setSource(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "python设计")
                                .field("price", 99)
                                .endObject()
                )
        );
        bulkRequestBuilder.add(client.prepareIndex("lei", "quan", "9")
                .setSource(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "c++设计")
                                .field("price", 59)
                                .endObject()
                )
        );
        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());

        if (responses.hasFailures()) {
            System.out.println("失败了");
        }
    }

    //*****************************************
    // 查询删除 * 删除
    // 查询出满足条件的文档之后删除
    @Test
    public void testDelete() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("name", "西游记"))  //testMatchQuery
                .source("books")
                .get();
        // 删除文档的个数
        long counts = response.getDeleted();
        System.out.println(counts);
    }

    //match_all
    @Test
    public void testMatchAll() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        // 输出前3个
        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(queryBuilder)
                .setSize(3).get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    //match_query
    @Test
    public void testMatchQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));


        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "设计");

        // 输出前3个
        SearchResponse searchResponse = client.prepareSearch("lei")
                .setQuery(queryBuilder)
                .setSize(3).get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

           /* Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }*/
        }
    }


    //multi_match_query 匹配多个字段查询
    @Test
    public void testMultiMatchQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("罗贯中","name","introduce");

        // 输出前3个
        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(queryBuilder)
                .setSize(20).get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    //term
    @Test
    public void testTermQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));


        QueryBuilder queryBuilder = QueryBuilders.termQuery("introduce","富强");


        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(queryBuilder)
                .setSize(5).get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    //terms  一个字段匹配多个条件查询
    @Test
    public void testTermsQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        //
        QueryBuilder queryBuilder = QueryBuilders.termsQuery("introduce","富强","罗贯中");


        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(queryBuilder)
                .setSize(10).get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    // range查询  限定范围的查询
    // prefix前缀查询
    // wildcard查询  模糊查询 通配符的
    // fuzzy查询 introduce中含有文明的
    // type查询  类型查询
    // ids查询  根据id进行查询
    @Test
    public void testRangeQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // range查询 90年到2000年出生的
        //QueryBuilder queryBuilder = QueryBuilders.rangeQuery("birthday").from("1990-01-01").to("2000-12-20").format("yyyy-MM-dd");

        // prefix前缀查询 name中以5开头的
        //QueryBuilder queryBuilder = QueryBuilders.prefixQuery("name","5");

        // wildcard查询  模糊查询
        //QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("introduce","22*");

        // fuzzy查询 introduce中含有文明的
        //QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("introduce","文明");

        // type查询  类型查询
        //QueryBuilder queryBuilder = QueryBuilders.typeQuery("library");

        // ids查询  根据id进行查询
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1","3","4");

        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(queryBuilder)
                .get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    // 聚合查询 Aggregation query
    @Test
    public void testAggregationQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // 查询年龄中的最大值  最大值调用min  平均值用 avg  总和用sum  基数cardinality 年龄这个字段上有多少个互不相同的年龄
        AggregationBuilder aggregationBuilder = AggregationBuilders.max("aggMax").field("age");

        SearchResponse searchResponse = client.prepareSearch("books")
                .addAggregation(aggregationBuilder)
                .setSize(10).get();
        Max max = searchResponse.getAggregations().get("aggMax");
        System.out.println(max.getValue());
    }

    // query_string 全文查询
    @Test
    public void testQueryString() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        //QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("title","设计");

        // +代表满足  -代表不满足  queryStringQuery为全部匹配
        //QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("+设计 -python");

        // 满足其中一个即可查出
        QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery("+设计 -python");


        SearchResponse searchResponse = client.prepareSearch("lei")
                .setQuery(queryBuilder)
                .get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }
    // 组合查询
    @Test
    public void testGroupQuery() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));


        // must  必须满足
        // mustNot 必须不满足
        // should  或者   可以有
        // filter 过滤
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("title","设计"))
                .mustNot(QueryBuilders.matchQuery("content","学习"))
                .should(QueryBuilders.matchQuery("url","baidu"))
                .filter(QueryBuilders.rangeQuery("postdate").gte("2000-02-02").format("yyyy-MM-dd"));

        SearchResponse searchResponse = client.prepareSearch("lei")
                .setQuery(queryBuilder)
                .get();
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());

            Map<String, Object> map = hit.getSourceAsMap();
            for (String s : map.keySet()) {
                System.out.println(s + "=" + map.get(s));
            }
        }
    }

    // 设置索引库结构
    public static XContentBuilder getCnMapping(String indexType) throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                .startObject(indexType)
                .startObject("properties")

                .startObject("id").field("type", "long")
                .endObject()

                .startObject("title").field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()

                .startObject("content").field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()

                .startObject("postdate").field("type", "date")
                .endObject()

                .startObject("url").field("type", "text")
                .endObject()

                .endObject() // properties
                .endObject() // indexType
                .endObject();
        return mapping;
    }
}
