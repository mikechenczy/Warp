package com.mj.nat;

import com.mj.nat.client.Client;
import com.mj.nat.server.Server;

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
