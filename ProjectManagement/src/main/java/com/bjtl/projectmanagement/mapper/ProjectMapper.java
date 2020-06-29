package com.bjtl.projectmanagement.mapper;

import com.bjtl.projectmanagement.model.ProjectDO;
import com.bjtl.projectmanagement.model.ProjectVO;

import java.util.List;
import java.util.Map;

/**
 *  @Description: 项目接口
 *  @Author: leitianquan
 *  @Date: 2020/06/20
 */
public interface ProjectMapper {
    int deleteByPrimaryKey(Integer projectId);

    int insert(ProjectDO record);

    int insertSelective(ProjectDO record);

    ProjectDO selectByPrimaryKey(Integer projectId);

    int updateByPrimaryKeySelective(ProjectDO record);

    int updateByPrimaryKey(ProjectDO record);

    List<ProjectVO> listProject(Map<String,Object> map);

    ProjectVO selectByKeyForVO(Integer projectId);

    ProjectDO selectByProjectName(String projectName);

    int deleteBatchProjects(Integer[] ids);
}