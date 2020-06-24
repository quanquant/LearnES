package com.bjtl.projectmanagement.service;

import com.bjtl.projectmanagement.model.BuildPropertiesVO;

import java.util.List;

/**
 * @Description: 建设性质业务层接口
 * @Author: leitianquan
 * @Date: 2020/06/22
 **/
public interface BuildPropertiesService {
    List<BuildPropertiesVO> listBuildProperties();
}
