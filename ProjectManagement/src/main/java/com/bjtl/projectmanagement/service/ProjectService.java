package com.bjtl.projectmanagement.service;

import com.bjtl.projectmanagement.model.ProjectDO;
import com.bjtl.projectmanagement.model.ProjectVO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 项目管理业务层接口
 * @Author: leitianquan
 * @Date: 2020/06/20
 **/
public interface ProjectService {
    ProjectDO getProjectById(Integer prjectId);

    ProjectDO getProjectByName(String projectName);

    Map<String,Object> listProjects(Map<String,Object> map);

    ProjectDO saveProject(ProjectDO projectDO);

    ProjectDO updateProject(ProjectDO projectDO);

    int deleteBatchProject(Integer[] ids);

    int updatePlanValue(List<ProjectDO> list);
}
