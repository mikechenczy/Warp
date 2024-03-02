package com.mj.warp.ws;

import com.mj.warp.ws.client.Client;
import com.mj.warp.ws.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mike_Chen
 * @date 2023/9/8
 * @apiNote
 */
public class Main {
    public static void main(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        if(argList.size()==0)
            return;
        if(argList.get(0).startsWith("s")) {
            argList.remove(0);
            Server.main(argList.toArray(new String[]{}));
            return;
        }
        argList.remove(0);
        Client.main(argList.toArray(new String[]{}));
    }
}
