package com.bjtl.service;

import java.util.Map;

/**
 * @Description: 日志业务层接口
 * @Author: leitianquan
 * @Date: 2020/07/08
 **/
public interface LogService {
    Map<String, Object> getLogList(Map<String, Object> inMap);

    Map<String, Object> getLogStatistic(String indexName);

    Map<String, Object> getDataByCeShi(String indexName);
}
