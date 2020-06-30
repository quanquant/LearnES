package com.bjtl.projectmanagement.controller;

import com.bjtl.projectmanagement.model.ProjectDO;
import com.bjtl.projectmanagement.model.Statistic;
import com.bjtl.projectmanagement.model.TreeNodes;
import com.bjtl.projectmanagement.service.BuildPropertiesService;
import com.bjtl.projectmanagement.service.ProjectService;
import com.bjtl.projectmanagement.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 项目控制层类
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
@RestController
@RequestMapping("project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private BuildPropertiesService buildPropertiesService;

    /**
     * 根据查询条件获取项目列表
     *
     * @param map 前端传来的查询条件，rows 和 page是easyui表格默认传递条件
     * @return 结果返回total和rows
     */
    @RequestMapping("listProject")
    public Map<String, Object> listProject(@RequestParam(required = false) Map<String, Object> map) {
        System.out.println("++++++++++++++" + map);
        return projectService.listProjects(map);
    }

    /**
     * 获取单位树形下拉框数据
     *
     * @return 树形下拉框数据
     */
    @RequestMapping("getTreeNode")
    public List<TreeNodes> getTreeNode() {
        List<TreeNodes> treeNodesList = unitService.listTreeNode(-1);
        // 先给出一个根节点，然后再以跟节点为参数进行递归
        return getChildrenNodeList(treeNodesList);
    }

    /**
     * 获取单位属性下拉框数据
     *
     * @return 树形下拉框数据
     */
    @RequestMapping("getTreeNodeByES")
    public List<TreeNodes> getTreeNodeByES() {
        return unitService.listTreeNodeByES(-1);
    }

    /**
     * 获取建设性质下拉框数据
     *
     * @return 建设性质下拉框数据
     */
    @RequestMapping("getBuildProperties")
    public Object getBuildProperties() {
        Object list = buildPropertiesService.listBuildProperties();
        return list;
    }

    /**
     * 添加项目
     *
     * @param projectDO 需要添加的项目对象
     * @return 返回给前端的状态
     */
    @RequestMapping("addProjects")
    public Map<String, Object> addProjects(ProjectDO projectDO) throws InvocationTargetException, IllegalAccessException {
        System.out.println("添加进来了");
        Map<String, Object> outMap = new HashMap<>();
        ProjectDO project = projectService.getProjectByName(projectDO.getProjectName());
        if (null != project) {
            // 项目名称已存在
            outMap.put("status", 1);
        } else {
            ProjectDO projectDO1 = projectService.saveProject(projectDO);
            if (null != projectDO1) {
                // 添加成功
                System.out.println("添加成功");
                outMap.put("status", 2);
            }
        }
        return outMap;
    }

    /**
     * 修改项目
     *
     * @param projectDO 需要修改的项目对象
     * @return 返回给前端的状态
     */
    @RequestMapping("updateProjects")
    public Map<String, Object> updateProjects(ProjectDO projectDO) throws InvocationTargetException, IllegalAccessException {
        System.out.println(projectDO);
        Map<String, Object> outMap = new HashMap<>();
        ProjectDO project = projectService.getProjectByName(projectDO.getProjectName());

        // 根据项目名称查询，项目不为空，且项目名与当前修改项目不相同
        if (null != project) {
            ProjectDO projectDO1 = projectService.getProjectById(projectDO.getProjectId());
            if (!projectDO.getProjectName().equals(projectDO1.getProjectName())) {
                // 项目名称已存在
                outMap.put("status", 1);
                return outMap;
            }
        }
        ProjectDO projectDO1 = projectService.updateProject(projectDO);
        if (null != projectDO1) {
            // 修改成功
            outMap.put("status", 2);
        }
        return outMap;
    }

    /**
     * 修改计划值
     *
     * @param list 接收前端传来的计划值对象数组
     * @return 返回给前端的状态
     */
    @RequestMapping("updatePlanValue")
    public Map<String, Object> updatePlanValue(@RequestBody List<ProjectDO> list) {
        Map<String, Object> outMap = new HashMap<>();
        //接收对象数组
        System.out.println(list);
        int flag = projectService.updatePlanValue(list);
        if (flag != 0) {
            outMap.put("status", 2);
        } else {
            outMap.put("status", 1);
        }
        return outMap;
    }

    /**
     * 递归方法，获取树形下拉框子节点数据
     *
     * @param treeList 父级节点
     * @return 树形下拉框所有数据
     */
    public List<TreeNodes> getChildrenNodeList(List<TreeNodes> treeList) {
        for (TreeNodes nodes : treeList) {
            List<TreeNodes> childrenNodeList = unitService.listTreeNode(nodes.getId());
            // 递归出口，必须要有，不然就一直循环了
            if (childrenNodeList.isEmpty()) {

            } else {
                getChildrenNodeList(childrenNodeList);
                if (null != childrenNodeList) {
                    nodes.setChildren(childrenNodeList);
                }
            }
        }
        return treeList;
    }

    /**
     * 删除项目
     *
     * @param ids 需要删除的项目编号
     * @return 状态
     */
    @RequestMapping("deleteProjects")
    public Map<String, Object> deleteProjects(Integer[] ids) {
        for (Integer id : ids) {
            System.out.println("需要删除的数据为：" + id);
        }
        Map<String, Object> outMap = new HashMap<>();
        int flag = projectService.deleteBatchProject(ids);
        if (flag != 0) {
            outMap.put("status", 2);
        } else {
            outMap.put("status", 1);
        }
        return outMap;
    }

    /**
     * 获取统计图的数据
     *
     * @param map 查询条件，年份
     * @return 获取状态和结果
     */
    @RequestMapping("getStatisticsData")
    public Map<String, Object> getStatisticsData(@RequestParam(required = false) Map<String, Object> map) {
        int year = 0;
        if (null != map.get("projectDate") && !"".equals(map.get("projectDate"))) {
            year = Integer.parseInt(map.get("projectDate").toString());
        }
        List<Statistic> list = projectService.getStatisticsData(year);
        Map<String, Object> outMap = new HashMap<>();

        if (null != list) {
            // 查到了
            outMap.put("status", 1);
            outMap.put("list", list);
        } else {
            outMap.put("status", 2);
        }
        return outMap;
    }
}
