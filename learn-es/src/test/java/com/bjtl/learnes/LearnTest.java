package com.bjtl.learnes;


import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@SpringBootTest
public class LearnTest {
    //分组聚合  使用terms
    @Test
    public void test1() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        // 按照年龄分组
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("terms").field("age");

        SearchResponse response = client.prepareSearch("lei").addAggregation(aggregationBuilder).execute().actionGet();

        Terms terms = response.getAggregations().get("terms");

        for (Terms.Bucket entry : terms.getBuckets()) {
            // 输出年龄和对应年龄的数据的数量
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    // filter聚合 指定一个过滤条件
    @Test
    public void test2() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        QueryBuilder queryBuilder = QueryBuilders.termQuery("age", 20);

        // 按照年龄分组
        AggregationBuilder aggregationBuilder = AggregationBuilders.filter("filter", queryBuilder);

        SearchResponse response = client.prepareSearch("lei").addAggregation(aggregationBuilder).execute().actionGet();

        Terms terms = response.getAggregations().get("terms");

        for (Terms.Bucket entry : terms.getBuckets()) {
            // 输出年龄和对应年龄的数据的数量
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    // filter聚合 指定多个过滤条件
    @Test
    public void test3() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));


        // 对interests进行分组查询
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters("filter",
                new FiltersAggregator.KeyedFilter("changge", QueryBuilders.termQuery("interests", "changge")),
                new FiltersAggregator.KeyedFilter("youyong", QueryBuilders.termQuery("interests", "youyong")));
        SearchResponse response = client.prepareSearch("lei").addAggregation(aggregationBuilder).execute().actionGet();

        Terms terms = response.getAggregations().get("terms");

        for (Terms.Bucket entry : terms.getBuckets()) {
            // 输出年龄和对应年龄的数据的数量
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    // range聚合
    @Test
    public void test4() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        // 对interests进行分组查询
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .range("range")
                .field("age")
                .addUnboundedTo(50) //小于50
                .addRange(25, 50) // 25-50之间
                .addUnboundedFrom(25); //大于25
        SearchResponse response = client.prepareSearch("lei").addAggregation(aggregationBuilder).execute().actionGet();

        Range range = response.getAggregations().get("terms");

        for (Range.Bucket entry : range.getBuckets()) {
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    //missing 聚合  某个字段上为空或为null的
    @Test
    public void test5() throws UnknownHostException {
        // 指定ES集群的名称
        Settings settings = Settings.builder().put("cluster.name", "ceshi-elasticsearch").build();
        // 创建访问ES服务器的客户端
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));

        //价格为null
        AggregationBuilder aggregationBuilder = AggregationBuilders.missing("missing").field("price");
        SearchResponse response = client.prepareSearch("lei").addAggregation(aggregationBuilder).execute().actionGet();
        Aggregation aggregation = response.getAggregations().get("missing");
        System.out.println(aggregation.toString());
    }

}
