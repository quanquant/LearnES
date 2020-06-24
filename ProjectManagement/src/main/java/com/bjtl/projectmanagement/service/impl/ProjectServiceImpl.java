package com.bjtl.projectmanagement.service.impl;

import com.bjtl.projectmanagement.mapper.ProjectMapper;
import com.bjtl.projectmanagement.model.ProjectDO;
import com.bjtl.projectmanagement.model.ProjectVO;
import com.bjtl.projectmanagement.service.ProjectService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 项目管理业务层实现类
 * @Author: leitianquan
 * @Date: 2020/06/20
 **/
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public ProjectDO getProjectById(Integer prjectId) {
        return null;
    }

    @Override
    public ProjectDO getProjectByName(String projectName) {
        return projectMapper.selectByProjectName(projectName);
    }

    @Override
    public Map<String,Object> listProjects(Map<String,Object> map) {
        Integer rows= Integer.parseInt(map.get("rows").toString());
        Integer pageCode = Integer.parseInt(map.get("page").toString());
        Page<Object> page = PageHelper.startPage(pageCode, rows);
        List<ProjectVO> list =projectMapper.listProject(map);
        Map<String,Object> outMap = new HashMap<>();
        outMap.put("rows", list);
        outMap.put("total",page.getTotal());
        return outMap;
    }

    @Override
    @CachePut(cacheNames = "project", key = "#result.projectId")
    public ProjectDO saveProject(ProjectDO projectDO) {
        projectDO.setProjectDate(new Date());
        projectDO.setPlanValue(0.0);
        int flag =  projectMapper.insert(projectDO);
        if (flag !=0){
            return projectDO;
        }
        return null;
    }

    @Override
    @CachePut(cacheNames = "project", key = "#projectDO.projectId")
    public ProjectDO updateProject(ProjectDO projectDO) {
        int flag = projectMapper.updateByPrimaryKeySelective(projectDO);
        if (flag !=0){
            System.out.println("===========x修改======="+projectDO);
            return projectDO;
        }
        return null;
    }

    @Override
    public int deleteBatchProject(Integer[] ids) {
        return projectMapper.deleteBatchProjects(ids);
    }

    @Override
    public int updatePlanValue(List<ProjectDO> list) {
        int flag = 0;
        for (ProjectDO projectDO : list) {
            System.out.println("单个数据："+projectDO);
            flag = projectMapper.updateByPrimaryKeySelective(projectDO);
        }
        return flag;
    }

}
