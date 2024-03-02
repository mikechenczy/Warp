package com.mj.warp.http.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike_Chen
 * @date 2023/9/8
 * @apiNote
 */

@SpringBootApplication
public class Server {
    public static String ip = "127.0.0.1";
    public static int port = 3389;
    public static int serverPort;
    public static void main(String[] args) {
        if(args.length>=1)
            ip = args[0];
        try {
            if(args.length>=2)
                port = Integer.parseInt(args[1]);
            if(args.length>=3)
                serverPort = Integer.parseInt(args[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        List<String> modifiedArgs = new ArrayList<>();
        modifiedArgs.add("--server.port="+serverPort);
        SpringApplication.run(Server.class, modifiedArgs.toArray(new String[]{}));
    }
}
