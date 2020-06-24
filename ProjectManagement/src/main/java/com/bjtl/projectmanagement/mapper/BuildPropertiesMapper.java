package com.bjtl.projectmanagement.mapper;

import com.bjtl.projectmanagement.model.BuildProperties;
import com.bjtl.projectmanagement.model.BuildPropertiesVO;

import java.util.List;

/**
 * @Description: 建设性质接口
 * @Author: leitianquan
 * @Date: 2020/06/20
 */
public interface BuildPropertiesMapper {
    int deleteByPrimaryKey(Integer buildId);

    int insert(BuildProperties record);

    int insertSelective(BuildProperties record);

    BuildProperties selectByPrimaryKey(Integer buildId);

    int updateByPrimaryKeySelective(BuildProperties record);

    int updateByPrimaryKey(BuildProperties record);

    List<BuildPropertiesVO> listBuildProperties();
}