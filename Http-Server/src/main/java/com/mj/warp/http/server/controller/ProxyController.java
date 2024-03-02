package com.mj.warp.http.server.controller;

import com.mj.warp.http.server.ProxyClient;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProxyController {
    public static Map<Long, Object[]> map = new HashMap<>();

    @GetMapping("/get")
    public void get(@RequestParam("uniqueId") long uniqueId, HttpServletResponse response) throws IOException {
        if(map.containsKey(uniqueId)) {
            ((ProxyClient)map.get(uniqueId)[1]).close();
            map.remove(uniqueId);
        }
        response.setContentType("application/octet-stream");
        ProxyClient proxyClient = new ProxyClient(uniqueId) {
            @Override
            public void sendBack(byte[] bytes) {
                if(ProxyController.map.containsKey(uniqueId)) {
                    try {
                        write(response, bytes);
                    } catch (IOException e) {
                        close();
                    }
                }
            }
        };
        map.put(uniqueId, new Object[]{0L, proxyClient});
        proxyClient.start();
        while (true) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!map.containsKey(uniqueId)) {
                break;
            }
        }
    }

    @PostMapping("/post")
    public String post(@RequestParam("uniqueId") long uniqueId, @RequestParam("index") long index, @RequestBody byte[] bytes) {
        if(!map.containsKey(uniqueId))
            return "";
        new Thread(() -> {
            while ((long)map.get(uniqueId)[0]!=index) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ((ProxyClient)map.get(uniqueId)[1]).send(bytes);
            map.replace(uniqueId, new Object[]{index+1, map.get(uniqueId)[1]});
        }).start();
        return "";
    }

    private void write(HttpServletResponse response, byte[] bytes) throws IOException {
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
