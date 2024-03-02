package com.mj.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mike_Chen
 * @date 2023/9/8
 * @apiNote
 */
public class Main {
    //SAMPLE:
    //java -jar x.jar http server 127.0.0.1 3389 8080
    //java -jar x.jar http client "http://server:8080/" 8081
    public static void main(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        if(argList.size()==0)
            return;
        if(argList.get(0).equalsIgnoreCase("ws")) {
            argList.remove(0);
            com.mj.warp.ws.Main.main(argList.toArray(new String[]{}));
            return;
        }
        argList.remove(0);
        com.mj.warp.http.Main.main(argList.toArray(new String[]{}));
    }
}
