package com.bjtl.service.impl;

import com.bjtl.config.ElasticSearchConfig;
import com.bjtl.controller.LogController;
import com.bjtl.service.LogService;
import com.bjtl.utils.TimeChange;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 日志业务层实现类
 * @Author: leitianquan
 * @Date: 2020/07/08
 **/
@Service
public class LogServiceImpl implements LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private static TransportClient client = ElasticSearchConfig.getInstance();

    @Override
    public Map<String, Object> getLogList(Map<String, Object> inMap) {
        logger.info("service中条件：" + inMap);
        Map<String, Object> outMap = new HashMap<>();
        String indexName = inMap.get("index").toString();
        int page = Integer.parseInt(inMap.get("page").toString());
        int limit = Integer.parseInt(inMap.get("limit").toString());

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (null != inMap.get("logDate")) {
            String logDate = inMap.get("logDate").toString();
            String startTime = TimeChange.timeToUnixTime(logDate.substring(0, 19));
            String endTime = TimeChange.timeToUnixTime(logDate.substring(22));
            logger.info("开始时间" + startTime + "结束时间" + endTime);
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("logTime")
                    .from(startTime).to(endTime);
            boolQuery.must(rangequerybuilder);
        }

        if (null != inMap.get("level")) {
            boolQuery.must(QueryBuilders.matchQuery("level", inMap.get("level")));
        }

        SearchResponse searchResponse = client.prepareSearch(indexName).setTypes("log").setQuery(boolQuery)
                .setFrom((page - 1) * limit).setSize(limit).addSort("logTime", SortOrder.DESC).get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits != null && hits.length > 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                list.add(searchHit.getSourceAsMap());
            }
            long totalHits = client.prepareSearch(indexName).setTypes("log").setQuery(boolQuery)
                    .setSize(0).get().getHits().getTotalHits();
            outMap.put("count", totalHits);
            outMap.put("data", list);
        }
        outMap.put("code", 0);
        outMap.put("msg", "");
        return outMap;
    }

    @Override
    public Map<String, Object> getLogStatistic(String indexName) {
        dateToWeek();
        Map<String, Object> outMap = new HashMap<>();
        // 获取本月根据日志级别分组聚合，对应数量
        SearchResponse response = queryAggregation(indexName, TimeChange.getMinMouthTime(), TimeChange.getMaxMouthTime());
        Terms terms = response.getAggregations().get("levelTerms");
        for (Terms.Bucket entry : terms.getBuckets()) {
            outMap.put(entry.getKey().toString(), entry.getDocCount());
        }
        // 获取今日根据日志级别分组聚合，对应数量
        SearchResponse searchResponse = queryAggregation(indexName, TimeChange.getMinTodayTime(), TimeChange.getMaxTodayTime());
        Terms termsCurrent = searchResponse.getAggregations().get("levelTerms");
        for (Terms.Bucket entry : termsCurrent.getBuckets()) {
            outMap.put(entry.getKey().toString() + "Today", entry.getDocCount());
        }
        return outMap;
    }

    public SearchResponse queryAggregation(String indexName, String startTime, String endTime) {
        AggregationBuilder groupByLevel = AggregationBuilders.terms("levelTerms").field("level");
        RangeQueryBuilder rangequery = QueryBuilders.rangeQuery("logTime").from(startTime).to(endTime);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(rangequery);
        SearchResponse response = client.prepareSearch(indexName).setQuery(boolQueryBuilder).addAggregation(groupByLevel).get();
        return response;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public static List<String> dateToWeek() {
        Date mdate = new Date();
        System.out.println("mdate :" + mdate);
        // 一周的第几天
        int b = mdate.getDay();
        System.out.println("b:  " + b);
        Date fdate;
        List<String> list = new ArrayList();
        Long fTime = mdate.getTime() - b * 24 * 3600000;
        System.out.println("fTime: " + fTime);
        for (int a = 0; a < 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + ((a+1) * 24 * 3600000));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            list.add(a, simpleDateFormat.format(fdate));
        }
        return list;
    }

}
