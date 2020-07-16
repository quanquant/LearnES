package com.bjtl.webscoket;

import com.bjtl.controller.LogController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: f
 * @Author: leitianquan
 * @Date: 2020/07/11
 **/
@ServerEndpoint(value = "/home")
@Component
public class WebSocketServer {

    @Autowired
    LogController logController;
    //用来记录当前在线连接数，线程安全。
    private static int onlineCount = 0;

    //线程安全Set，存放每个客户端的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getAsyncRemote());
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        sendMessage("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        sendMessage("有用户退出！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        //群发消息
        sendInfo("服务端返回:" + session.getId() + "-" + message);
    }

    /**
     * 发生错误时调用
     *
     * @OnError
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 向客户端发送消息
     *
     * @param message 消息内容
     */
    private void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    private void sendMessage2(Map<String,Object> map) {
        this.session.getAsyncRemote().sendObject(map);
    }


    /**
     * 群发自定义消息
     */
    public void sendInfo(String message) {
        try {
            for (WebSocketServer item : webSocketSet) {
                item.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendInfo2(Map<String,Object> map) {
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage2(map);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
