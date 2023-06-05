package com.example.myapplication.utile;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.net.ssl.SSLParameters;

public class JWebSocketClient extends WebSocketClient {
    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
        //super.onSetSSLParameters(sslParameters);
    }

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtils.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        LogUtils.e("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.e("JWebSocketClient", "onClose()" + reason + "-****-" + remote);
    }

    @Override
    public void onError(Exception ex) {
        LogUtils.e("JWebSocketClient", "onError()" + ex);
    }
}
