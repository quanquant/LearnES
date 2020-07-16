package com.bjtl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bjtl.service.LogService;
import com.bjtl.webscoket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: 日志管理控制层
 * @Author: leitianquan
 * @Date: 2020/07/03
 **/
@Slf4j
@RestController
@RequestMapping("log")
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogService logService;
    @Autowired
    WebSocketServer webSocketServer;

    /**
     * 多条件查询日志列表
     *
     * @param inMap 条件map
     * @return layui要求返回的参数map
     */
    @RequestMapping("getLogList")
    public Map<String, Object> getLogList(@RequestParam(required = false) Map<String, Object> inMap) {
        if (null != inMap.get("searchParams")) {
            JSONObject searchParams = JSON.parseObject((String) inMap.get("searchParams"));
            inMap.put("index", searchParams.get("index"));
            if (null != searchParams.get("logDate") && !"".equals(searchParams.get("logDate"))) {
                inMap.put("logDate", searchParams.get("logDate"));
            }
            if (null != searchParams.get("level") && !"".equals(searchParams.get("level"))) {
                inMap.put("level", searchParams.get("level"));
            }
            inMap.remove("searchParams");
        }
        return logService.getLogList(inMap);
    }

    /**
     * 获取首页统计图数据
     *
     * @param inMap 获取需要查询的系统索引
     * @return 统计需要的数据
     */
    @RequestMapping("getLogStatistic")
    public Map<String, Object> getLogStatistic(@RequestParam(required = false) Map<String, Object> inMap) {
        String indexName = inMap.get("index").toString();
        return logService.getLogStatistic(indexName);
    }

    @Scheduled(fixedDelay = 1000 * 2)
    public Map<String, Object> getData() {
        String indexName = "log_log";
        Map<String, Object> map = logService.getLogStatistic(indexName);
        webSocketServer.sendInfo(JSON.toJSONString(map));
        //webSocketServer.sendInfo2(map);
        return map;
    }

    @RequestMapping("get")
    public Map<String,Object> get(){
        String indexName = "log_log";
        return logService.getDataByCeShi(indexName);
    }
}