package com.bjtl.projectmanagement.service;

import com.bjtl.projectmanagement.model.TreeNodes;

import java.util.List;

/**
 * @Description: 单位业务层接口
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
public interface UnitService {
    List<TreeNodes> listTreeNode(Integer id);

    List<TreeNodes> listTreeNodeByES(Integer id);
}
