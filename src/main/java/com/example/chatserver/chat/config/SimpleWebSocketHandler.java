package com.example.chatserver.chat.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// connect로 웹소켓 연결초요청이 들어왔을 때 이를 처리할 클래스
@Component
@Slf4j
public class SimpleWebSocketHandler extends TextWebSocketHandler {

    // 연결된 세션 관리 : 스레드 safe한 자료구조
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // session의 아이피 브라우저 등등 정보가 들어있다
        sessions.add(session);
        log.info("Connected : {}", session.getId());

    }


    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received Message : {}", payload);
        for(WebSocketSession s : sessions){
            if(s.isOpen()){ // Set에 담겨있는 연결된 모두에게 메시지를 보내겠다 (일반 웹소켓가지고는 방이나 이런 웹소켓코드는 짜기 힘들다)
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("Disconnected");
    }




}
