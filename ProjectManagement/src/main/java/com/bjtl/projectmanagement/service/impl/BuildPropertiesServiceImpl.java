package com.bjtl.projectmanagement.service.impl;

import com.bjtl.projectmanagement.common.ElasticSearchInfo;
import com.bjtl.projectmanagement.config.ElasticSearchConfig;
import com.bjtl.projectmanagement.mapper.BuildPropertiesMapper;
import com.bjtl.projectmanagement.model.BuildPropertiesVO;
import com.bjtl.projectmanagement.service.BuildPropertiesService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 建设性质业务层实现类
 * @Author: leitianquan
 * @Date: 2020/06/22
 **/
@Service
public class BuildPropertiesServiceImpl implements BuildPropertiesService {

    @Autowired
    private BuildPropertiesMapper buildPropertiesMapper;

    /**
     * 获取建设性质列表
     *
     * @return 建设性质列表
     */
    @Override
    @Cacheable(cacheNames = "build", key = "1")
    public Object listBuildProperties() {
        TransportClient client = ElasticSearchConfig.getInstance();
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(ElasticSearchInfo.INDEX_BUILD_NAME)
                .setQuery(queryBuilder)
                .get();
        SearchHit[] hits = searchResponse.getHits().getHits();

        if (hits != null && hits.length > 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                list.add(searchHit.getSourceAsMap());
            }
            return list;
        }
        System.out.println("ES中没查到数据");
        List<BuildPropertiesVO> builds = buildPropertiesMapper.listBuildProperties();
        return builds;
    }
}
