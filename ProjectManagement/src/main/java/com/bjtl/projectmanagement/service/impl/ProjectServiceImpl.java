package com.bjtl.projectmanagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.bjtl.projectmanagement.common.ElasticSearchInfo;
import com.bjtl.projectmanagement.config.ElasticSearchConfig;
import com.bjtl.projectmanagement.mapper.ProjectMapper;
import com.bjtl.projectmanagement.mapper.UnitMapper;
import com.bjtl.projectmanagement.model.ProjectDO;
import com.bjtl.projectmanagement.model.ProjectVO;
import com.bjtl.projectmanagement.model.Statistic;
import com.bjtl.projectmanagement.model.UnitDO;
import com.bjtl.projectmanagement.service.ProjectService;
import com.bjtl.projectmanagement.utils.DateUtil;
import com.bjtl.projectmanagement.utils.ElasticSearchUtil;
import com.bjtl.projectmanagement.utils.RedisUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

/**
 * @Description: 项目管理业务层实现类
 * @Author: leitianquan
 * @Date: 2020/06/20
 **/
@Service
public class ProjectServiceImpl implements ProjectService {
    private String cacheName = "project";

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UnitMapper unitMapper;

    @Override
    public ProjectDO getProjectById(Integer projectId) {
        return projectMapper.selectByPrimaryKey(projectId);
    }

    /**
     * 根据项目名称查询项目
     * 使用自定义的RedisTemplate对项目对象进行缓存
     *
     * @param projectName 项目名称
     * @return 查询到的项目对象
     */
    @Override
    public ProjectDO getProjectByName(String projectName) throws InvocationTargetException, IllegalAccessException {
        // 1. 判断redis缓存中是否存在 存在则直接返回数据
        if (redisUtil.hHasKey(cacheName, projectName)) {
            return JSON.parseObject(String.valueOf(redisUtil.hget(cacheName, projectName)), ProjectDO.class);
        }
        // 2. 在ES中查询 查询成功加入redis中并返回 实现精准查询
        TransportClient client = ElasticSearchConfig.getInstance();
        BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
        mustQuery.must(QueryBuilders.matchPhraseQuery("projectName", projectName));
        SearchResponse searchResponse = client.prepareSearch(ElasticSearchInfo.INDEX_PROJECT_NAME)
                .setQuery(mustQuery)
                .setSize(1).get();
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits != null && hits.length > 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(hits[0].getSourceAsMap());
            Map<String, Object> map = list.get(0);
            ProjectDO projectDO = new ProjectDO();
            BeanUtils.populate(projectDO, map);
            System.out.println("转化后" + projectDO);
            // 查询成功加入redis
            redisUtil.hset(cacheName, projectName, JSON.toJSONString(projectDO));
            return projectDO;
        }
        ProjectDO projectDO1 = projectMapper.selectByProjectName(projectName);
        if (null != projectDO1) {
            redisUtil.hset(cacheName, projectName, JSON.toJSONString(projectDO1));
            return projectDO1;
        }
        return null;
    }

    /**
     * 多条件查询项目列表
     *
     * @param map 条件
     * @return 返回值 rows 为项目列表 total为项目总数
     */
    @Override
    public Map<String, Object> listProjects(Map<String, Object> map) {
        TransportClient client = ElasticSearchConfig.getInstance();
        Integer rows = Integer.parseInt(map.get("rows").toString());
        Integer pageCode = Integer.parseInt(map.get("page").toString());

        map.remove("page");
        map.remove("rows");
        Map<String, Object> outMap = new HashMap<>();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        int year = 0;
        // 判断是否有年份条件，若有，专门对日期设置查询
        if (map.get("projectDate") != null && !"".equals(map.get("projectDate").toString())) {
            year = Integer.parseInt(map.get("projectDate").toString());
            String time1 = null;
            String time2 = null;
            try {
                // 获取查询年的第一天和最后一天，转化成时间戳
                time1 = DateUtil.dateToStamp(DateUtil.getYearFirst(year));
                time2 = DateUtil.dateToStamp(DateUtil.getYearLast(year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("projectDate")
                    .from(time1).to(time2);
            boolQuery.must(rangequerybuilder);
            map.remove("projectDate");
        }

        // 判断是否有单位条件，若有 获取该单位名称加入查询条件，实现分词查询
        String unitIdString = map.get("unitId").toString();
        if (null != unitIdString && !"".equals(unitIdString)) {
            int unitId = Integer.parseInt(unitIdString);
            UnitDO unitDO = unitMapper.selectByPrimaryKey(unitId);
            String unitName = unitDO.getUnitName();
            map.remove("unitId");
            map.put("unitName", unitName);
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "") {
                boolQuery.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
            }
        }
        // addSort("projectId", SortOrder.DESC) 排序
        SearchResponse searchResponse = client.prepareSearch(ElasticSearchInfo.INDEX_PROJECT_NAME).setTypes(ElasticSearchInfo.TYPE_PROJECT_NAME).setQuery(boolQuery)
                .setFrom((pageCode - 1) * rows).setSize(rows).addSort("projectId", SortOrder.DESC).get();
        SearchHit[] hits = searchResponse.getHits().getHits();

        if (hits != null && hits.length > 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                list.add(searchHit.getSourceAsMap());
            }
            long totalHits = client.prepareSearch(ElasticSearchInfo.INDEX_PROJECT_NAME).setTypes(ElasticSearchInfo.TYPE_PROJECT_NAME).setQuery(boolQuery)
                    .setSize(0).get().getHits().getTotalHits();
            outMap.put("total", totalHits);
            outMap.put("rows", list);
            System.out.println("来查了");
            return outMap;
        }
        return outMap;
    }

    @Override
    public ProjectDO saveProject(ProjectDO projectDO) {
        projectDO.setProjectDate(new Date());
        projectDO.setPlanValue(0.0);
        int flag = projectMapper.insert(projectDO);
        // 添加数据库成功，将数据存入Redis ES
        if (flag != 0) {
            // 1. 将数据存入Redis中
            System.out.println("添加成功：" + projectDO);
            ProjectVO projectVO = projectMapper.selectByKeyForVO(projectDO.getProjectId());
            String unitParentName = setUnitName(projectVO.getUnitId());
            if (null != unitParentName){
                projectVO.setUnitName(setUnitName(projectVO.getUnitId())+projectVO.getUnitName());
            }
            redisUtil.hset(cacheName, (projectDO.getProjectId()).toString(), JSON.toJSONString(projectVO));
            // 2. 将数据存入ES
            ElasticSearchUtil.insertData(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME, String.valueOf(projectDO.getProjectId()), JSON.toJSONString(projectVO));
            return projectDO;
        }
        return null;
    }

    @Override
    public ProjectDO updateProject(ProjectDO projectDO) {
        int flag = projectMapper.updateByPrimaryKeySelective(projectDO);
        // 修改数据库成功 将数据存入Redis ES
        if (flag != 0) {
            // 1. 直接写入redis 不需要判断是否存在 存在则直接覆盖
            ProjectVO projectVO = projectMapper.selectByKeyForVO(projectDO.getProjectId());
            String unitParentName = setUnitName(projectVO.getUnitId());
            if (null != unitParentName){
                projectVO.setUnitName(setUnitName(projectVO.getUnitId())+projectVO.getUnitName());
            }
            redisUtil.hset(cacheName, (projectDO.getProjectId()).toString(), JSON.toJSONString(projectVO));
            // 2. 写入ES
            ElasticSearchUtil.updateData(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME, String.valueOf(projectDO.getProjectId()), JSON.toJSONString(projectVO));
            return projectDO;
        }
        return null;
    }

    @Override
    public int deleteBatchProject(Integer[] ids) {
        int flag = projectMapper.deleteBatchProjects(ids);
        // 批量删除成功，删除Redis中的和Es中的
        if (flag != 0) {
            // 1. 批量删除Redis中的数据 需要是String数组不能是Integer数组
            String[] ids1 = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                ids1[i] = String.valueOf(ids[i]);
            }
            redisUtil.hdel2(cacheName, ids1);
            // 2. 批量删除ES中的数据
            for (int i = 0; i < ids.length; i++) {
                ElasticSearchUtil.deleteData(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME, ids[i].toString());
            }
        }
        return flag;
    }

    @Override
    public int updatePlanValue(List<ProjectDO> list) {
        int flag = 0;
        for (ProjectDO projectDO : list) {
            flag = projectMapper.updateByPrimaryKeySelective(projectDO);
            // 修改数据库计划值成功  修改Redis和ES
            if (flag != 0) {
                System.out.println("修改成功");
                // 1. 修改Redis中的, 从数据库中根据项目编号查询该条数据，存入Redis中
                ProjectVO projectVO = projectMapper.selectByKeyForVO(projectDO.getProjectId());
                String unitParentName = setUnitName(projectVO.getUnitId());
                if (null != unitParentName){
                    projectVO.setUnitName(setUnitName(projectVO.getUnitId())+projectVO.getUnitName());
                }
                redisUtil.hset(cacheName, (projectVO.getProjectId()).toString(), JSON.toJSONString(projectDO));
                // 2. 修改ES中的数据
                ElasticSearchUtil.updateData(ElasticSearchInfo.INDEX_PROJECT_NAME, ElasticSearchInfo.TYPE_PROJECT_NAME, String.valueOf(projectDO.getProjectId()), JSON.toJSONString(projectVO));
            }
        }
        return flag;
    }

    @Override
    public List<ProjectVO> listAllProject() {
        List<ProjectVO> list = projectMapper.listProject(null);
        for (ProjectVO projectVO : list) {
            Integer unitId = projectVO.getUnitId();
            if (null != setUnitName(unitId)){
                projectVO.setUnitName( setUnitName(unitId)+ projectVO.getUnitName());
            }
        }
        return list;
    }

    @Override
    public List<Statistic> getStatisticsData(int year) {
        List<Statistic> list = new ArrayList();
        TransportClient client = ElasticSearchConfig.getInstance();
        // 若为0，则查询所有数据
        if(year ==0){

        }else {
            // 根据省份分组，求省份内计划组总数
            TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("unitList").field("unitName");
            AggregationBuilder aggregationBuilder1 = AggregationBuilders.avg("planValueSum").field("planValue");
            AggregationBuilder aggregationBuilder = AggregationBuilders.filters("filter",
                    new FiltersAggregator.KeyedFilter("year", QueryBuilders.termQuery("interests", "changge")),
                    new FiltersAggregator.KeyedFilter("youyong", QueryBuilders.termQuery("interests", "youyong")));
        }
        return null;
    }

    public String setUnitName(int unitId) {
        String unitParentName = null;
        String unitName = null;
        if (unitId != 0) {
            UnitDO unitDO = unitMapper.selectByPrimaryKey(unitId);
            if (unitDO.getUnitParentId() != 0) {
                UnitDO unitParent = unitMapper.selectByPrimaryKey(unitDO.getUnitParentId());
                unitParentName = unitParent.getUnitName();
                unitName = unitParentName + " ";
            }
        }
        return unitName;
    }

}
