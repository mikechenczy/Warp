package com.mj.warp.http.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Mike_Chen
 * @date 2023/9/8
 * @apiNote
 */
public class HttpClient extends Thread {
    public ChannelHandlerContext ctxOrigin;
    public boolean closed;
    public HttpClient(ChannelHandlerContext ctx) {
        this.ctxOrigin = ctx;
        uniqueId = new Random().nextLong();
    }

    public HttpClient connect() {
        start();
        return this;
    }

    public List<byte[]> byteBufList = new ArrayList<>();

    private boolean connected;

    private final long uniqueId;

    private Long msgSendIndex = 0L;

    public void send(byte[] msg) {
        flush();
        if(connected) {
            writeAndFlush(msg);
        } else {
            byteBufList.add(msg);
        }
    }
    public void flush() {
        if(!connected)
            return;
        if(byteBufList.size()>0){
            for(byte[] bytes : byteBufList) {
                writeAndFlush(bytes);
            }
            byteBufList.clear();
        }
    }
    public void sendBack(byte[] msg) {
        if(ClientHandler.ctxList.containsKey(ctxOrigin)) {
            ByteBuf buffer = ctxOrigin.alloc().buffer();
            buffer.writeBytes(msg);
            ctxOrigin.writeAndFlush(buffer);
            //ctxOrigin.writeAndFlush(Utils.bytes2ByteBuf(msg));
        }
    }

    @Override
    public void run() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(Client.url+"get?uniqueId="+ uniqueId).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setReadTimeout(0);
            urlConnection.connect();
            connected = true;
            flush();
            while (connected) {
                try {
                    Thread.sleep(0);
                    if(urlConnection.getInputStream().available()>0) {
                        byte[] b = new byte[urlConnection.getInputStream().available()];
                        urlConnection.getInputStream().read(b, 0, b.length);
                        sendBack(b);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    connected = false;
                }
            }
            urlConnection.getInputStream().close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void writeAndFlush(byte[] bytes) {
        try {
            CloseableHttpClient client = new DefaultHttpClient();
            synchronized (msgSendIndex) {
                HttpPost httpPost = new HttpPost(Client.url + "post?uniqueId=" + uniqueId + "&index="+(msgSendIndex++));
                httpPost.setEntity(new ByteArrayEntity(bytes));
                new Thread(() -> {
                    try {
                        client.execute(httpPost);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close() {
        if(closed) {
            return;
        }
        connected = false;
        closed = true;
        byteBufList.clear();
        ClientHandler.ctxList.remove(ctxOrigin);
        ctxOrigin.close();
    }
}
