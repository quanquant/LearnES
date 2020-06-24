package com.bjtl.projectmanagement.service;

import com.bjtl.projectmanagement.model.TreeNodes;

import java.util.List;
import java.util.Map;

/**
 * @Description: 单位业务层接口
 * @Author: leitianquan
 * @Date: 2020/06/21
 **/
public interface UnitService {
    List<TreeNodes> listTreeNode(Integer id);
}
