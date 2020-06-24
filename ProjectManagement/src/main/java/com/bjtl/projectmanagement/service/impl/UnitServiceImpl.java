package com.bjtl.projectmanagement.service.impl;

import com.bjtl.projectmanagement.mapper.UnitMapper;
import com.bjtl.projectmanagement.model.TreeNodes;
import com.bjtl.projectmanagement.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return unitMapper.listTreeNode(id);
    }
}
