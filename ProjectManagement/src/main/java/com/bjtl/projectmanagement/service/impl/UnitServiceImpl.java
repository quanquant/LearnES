package com.bjtl.projectmanagement.service.impl;

import com.bjtl.projectmanagement.common.ElasticSearchInfo;
import com.bjtl.projectmanagement.config.ElasticSearchConfig;
import com.bjtl.projectmanagement.mapper.UnitMapper;
import com.bjtl.projectmanagement.model.TreeNodes;
import com.bjtl.projectmanagement.service.UnitService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 单位业务层接口实现类
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
@Service
public class UnitServiceImpl implements UnitService {
    @Autowired
    private UnitMapper unitMapper;

    @Override
    public List<TreeNodes> listTreeNode(Integer id) {
        List<TreeNodes> treeNodesList = unitMapper.listTreeNode(id);
        return treeNodesList;
    }

    /**
     * 获取单位树形下拉框的数据
     *
     * @param id 初始查询编号
     * @return 单位树形下拉框的数据
     */
    @Override
    @Cacheable(cacheNames = "unit", key = "1")
    public List<TreeNodes> listTreeNodeByES(Integer id) {
        List<TreeNodes> list = null;
        TransportClient instance = ElasticSearchConfig.getInstance();
        SearchResponse response = instance.prepareSearch(ElasticSearchInfo.INDEX_UNIT_NAME).setTypes(ElasticSearchInfo.TYPE_UNIT_NAME)
                .setQuery(QueryBuilders.termQuery("parentId", id)).get();
        SearchHit[] hits = response.getHits().getHits();

        if (hits != null && hits.length > 0) {
            list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                TreeNodes tn = new TreeNodes(Integer.valueOf(map.get("id").toString()), map.get("text").toString());
                List<TreeNodes> children = listTreeNodeByES(Integer.valueOf(map.get("id").toString()));
                tn.setChildren(children);
                list.add(tn);
            }
        }
        return list;
    }
}
