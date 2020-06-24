package com.bjtl.projectmanagement.mapper;

import com.bjtl.projectmanagement.model.TreeNodes;
import com.bjtl.projectmanagement.model.UnitDO;

import java.util.List;
import java.util.Map;

/**
 *  @Description: 单位接口
 *  @Author: leitianquan
 *  @Date: 2020/06/20
 */
public interface UnitMapper {
    int deleteByPrimaryKey(Integer unitId);

    int insert(UnitDO record);

    int insertSelective(UnitDO record);

    UnitDO selectByPrimaryKey(Integer unitId);

    int updateByPrimaryKeySelective(UnitDO record);

    int updateByPrimaryKey(UnitDO record);

    List<TreeNodes> listTreeNode(Integer id);
}