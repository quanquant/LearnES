package com.bjtl.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @Description: ES配置类，初始化操作
 * @Author: leitianquan
 * @Date: 2020/06/26
 **/
@Configuration
public class ElasticSearchConfig {
    /**
     * 集群名称
     */
    private static String clusterName;

    /**
     * 集群地址
     */
    private static String host;

    /**
     * 集群服务端口
     */
    private static Integer port;

    @Value("${ES.clustername}")
    public void setClusterName(String clusterName) {
        ElasticSearchConfig.clusterName = clusterName;
    }

    @Value("${ES.host}")
    public void setHost(String host) {
        ElasticSearchConfig.host = host;
    }

    @Value("${ES.port}")
    public void setPort(Integer port) {
        ElasticSearchConfig.port = port;
    }


    /**
     * 静态内部类
     */
    private static class Singleton {
        // 创建客户端对象（TransportClient是接口，使用其子类 PreBuiltTransportClient 进行实例化）
        private static TransportClient client = null;

        static {
            // 配置设置对象，指定集群的名称
            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            try {
                // 创建访问es服务器的客户端实例
                client = new PreBuiltTransportClient(settings);
                // 配置其ES集群节点的IP和端口号
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取ES客户端
     *
     * @return 客户端
     */
    public static TransportClient getInstance() {
        return Singleton.client;
    }

    /**
     * 防止netty的bug
     * java.lang.IllegalStateException: availableProcessors is already set to [4], rejecting [4]
     */
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
