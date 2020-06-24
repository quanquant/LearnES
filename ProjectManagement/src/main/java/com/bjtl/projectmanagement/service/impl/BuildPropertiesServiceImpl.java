package com.bjtl.projectmanagement.service.impl;

import com.bjtl.projectmanagement.mapper.BuildPropertiesMapper;
import com.bjtl.projectmanagement.model.BuildPropertiesVO;
import com.bjtl.projectmanagement.service.BuildPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 建设性质业务层实现类
 * @Author: leitianquan
 * @Date: 2020/06/22
 **/
@Service
public class BuildPropertiesServiceImpl implements BuildPropertiesService {

    @Autowired
    private BuildPropertiesMapper buildPropertiesMapper;

    @Override
    public List<BuildPropertiesVO> listBuildProperties() {
        return buildPropertiesMapper.listBuildProperties();
    }
}
