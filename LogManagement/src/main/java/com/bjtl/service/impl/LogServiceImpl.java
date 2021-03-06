package com.bjtl.service.impl;

import com.bjtl.config.ElasticSearchConfig;
import com.bjtl.controller.LogController;
import com.bjtl.service.LogService;
import com.bjtl.utils.TimeChange;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    /**
     * 从ES中获取日志列表
     *
     * @param inMap 条件
     * @return 日志列表，列表信息
     */
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

    /**
     * 从ES中获取系统首页所需数据
     *
     * @param indexName 索引名称
     * @return 所需数据
     */
    @Override
    public Map<String, Object> getLogStatistic(String indexName) {
        Map<String, Object> outMap = new HashMap<>();
        // 获取本月根据日志级别分组聚合，对应数量
        SearchResponse response = queryAggregation(indexName, TimeChange.getMinMouthTime(), TimeChange.getMaxMouthTime());
        Terms terms = response.getAggregations().get("levelTerms");
        for (Terms.Bucket entry : terms.getBuckets()) {
            outMap.put(entry.getKey().toString(), entry.getDocCount());
        }
        // 获取今日根据日志级别分组聚合，对应数量
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = simpleDateFormat.format(currentDate);
        SearchResponse searchResponse = queryAggregation(indexName, TimeChange.getMinTodayTime(todayDate), TimeChange.getMaxTodayTime(todayDate));
        Terms termsCurrent = searchResponse.getAggregations().get("levelTerms");
        if (termsCurrent.getBuckets().size() == 0) {
            outMap.put("ERRORToday", 0);
            outMap.put("INFOToday", 0);
            outMap.put("DEBUGToday", 0);
            outMap.put("WARNToday", 0);
        }
        for (Terms.Bucket entry : termsCurrent.getBuckets()) {
            outMap.put(entry.getKey().toString() + "Today", entry.getDocCount());
        }
        // 获取本周7天根据日志界别分组聚合，对应数量
        List<Long> errorList = new ArrayList<>();
        List<Long> infoList = new ArrayList<>();
        List<Long> debugList = new ArrayList<>();
        List<Long> warnList = new ArrayList<>();
        List<String> weekList = dateToWeek();
        for (String time : weekList) {
            SearchResponse responseWeek = queryAggregation(indexName, TimeChange.getMinTodayTime(time), TimeChange.getMaxTodayTime(time));
            Terms termsWeek = responseWeek.getAggregations().get("levelTerms");
            if (termsWeek.getBuckets().size() == 0) {
                errorList.add(0L);
                infoList.add(0L);
                debugList.add(0L);
                warnList.add(0L);
            }
            for (Terms.Bucket entry : termsWeek.getBuckets()) {
                if ("ERROR".equals(entry.getKey().toString())) {
                    errorList.add(entry.getDocCount());
                }
                if ("INFO".equals(entry.getKey().toString())) {
                    infoList.add(entry.getDocCount());
                }
                if ("DEBUG".equals(entry.getKey().toString())) {
                    debugList.add(entry.getDocCount());
                }
                if ("WARN".equals(entry.getKey().toString())) {
                    warnList.add(entry.getDocCount());
                }
            }
        }
        outMap.put("errorList", errorList);
        outMap.put("infoList", infoList);
        outMap.put("debugList", debugList);
        outMap.put("warnList", warnList);
        return outMap;
    }

    /**
     * 提取出公用的方法，从ES中根据日志时间范围和日志级别联合查询
     *
     * @param indexName 索引名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return ES查询返回响应
     */
    public SearchResponse queryAggregation(String indexName, String startTime, String endTime) {
        AggregationBuilder groupByLevel = AggregationBuilders.terms("levelTerms").field("level");
        RangeQueryBuilder rangequery = QueryBuilders.rangeQuery("logTime").from(startTime).to(endTime);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(rangequery);
        SearchResponse response = client.prepareSearch(indexName).setQuery(boolQueryBuilder).addAggregation(groupByLevel).get();
        return response;
    }

    /**
     * 获取当前周一周的日期
     *
     * @return 一周的日期
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static List<String> dateToWeek() {
        Date mdate = new Date();
        // 一周的第几天
        int b = mdate.getDay();
        Date fdate;
        List<String> list = new ArrayList();
        Long fTime = mdate.getTime() - b * 24 * 3600000;
        for (int a = 0; a < 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + ((a + 1) * 24 * 3600000));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            list.add(a, simpleDateFormat.format(fdate));
        }
        return list;
    }

    @Override
    public Map<String, Object> getDataByCeShi(String indexName) {
        Map<String, Object> outMap = new HashMap<>();
        // 获取本周7天根据日志界别分组聚合，对应数量
        List<Long> errorList = new ArrayList<>();
        List<Long> infoList = new ArrayList<>();
        List<Long> debugList = new ArrayList<>();
        List<Long> warnList = new ArrayList<>();
        List<String> weekList = dateToWeek();
        String startTime = TimeChange.getMinTodayTime(weekList.get(0));
        String endTime = TimeChange.getMaxTodayTime(weekList.get(weekList.size() - 1));
       /* String startTime = TimeChange.getMinTodayTime("2020-07-06");
        String endTime = TimeChange.getMaxTodayTime("2020-07-12");*/
        System.out.println(startTime + "  " + endTime);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("logTime").gte(startTime));
        queryBuilder.must(QueryBuilders.rangeQuery("logTime").lte(endTime));
        //根据时间分组统计总数
        DateHistogramAggregationBuilder fieldBuilder = AggregationBuilders.dateHistogram("logTime").field("logTime").dateHistogramInterval(DateHistogramInterval.DAY);
        AggregationBuilder groupByLevel = AggregationBuilders.terms("levelTerms").field("level");
        SearchResponse response = client.prepareSearch(indexName).setQuery(queryBuilder).addAggregation(groupByLevel.subAggregation(fieldBuilder)).get();
        Terms terms = response.getAggregations().get("levelTerms");
        // 按级别划分 bucket.getKey()为日志级别
        for (Terms.Bucket bucket  : terms.getBuckets()) {
            Histogram agg = bucket.getAggregations().get("logTime");
            // 按时间划分 entry.getKey()为时间级别
            for (Histogram.Bucket entry : agg.getBuckets()) {
                logger.info(bucket.getKey().toString()+"--"+entry.getKey().toString()+"--"+entry.getDocCount());
                outMap.put(bucket.getKey().toString()+entry.getKey().toString()+UUID.randomUUID(),entry.getDocCount());
                if ("ERROR".equals(bucket.getKey().toString())) {
                    setListData(errorList,entry);
                }
                if ("INFO".equals(bucket.getKey().toString())) {
                    setListData(infoList,entry);
                }
                if ("DEBUG".equals(bucket.getKey().toString())) {
                    setListData(debugList,entry);
                }
                if ("WARN".equals(bucket.getKey().toString())) {
                    setListData(warnList,entry);
                }
            }
        }
        outMap.put("errorList", errorList);
        outMap.put("infoList", infoList);
        outMap.put("debugList", debugList);
        outMap.put("warnList", warnList);
        return outMap;
    }

    public void setListData(List<Long> list, Histogram.Bucket entry){
        List<String> weekList = dateToWeek();
        for (String s : weekList) {
            logger.info(s+"时间------------------------------："+entry.getKey().toString().substring(0,10));
            if (s.equals(entry.getKey().toString().substring(0,10))){
                list.add(entry.getDocCount());
            }else{
                list.add(0L);
            }
        }
    }
}
