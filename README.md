本项目适用场景：利用netlify的http redirect功能，再加上家里宽带下一台ipv6服务器，实现零成本搭建tcp服务。\
可执行文件使用graalvm native-image编译而成。由于不支持交叉编译，发布列表里没有的就安装java来使用吧\
原理是将tcp连接包装为http短连接：\
real tcp server port<---warp server--->http server port<-----http get/post------>
http server port<---warp client--->tcp server port<----->real client\
使用方法：\
``Http-Server 127.0.0.1 3389 8080``\
``Http-Client "http://serverhost:8080/" 8081``\
同时也有websocket协议的类似的代码和使用方法，由于netlify只能http短连接，因此不适用，但适用于cloudflare的cdn代理（注意cf cdn仅支持部分端口）。有需要可以自行编译。\

TODO List：\
添加客户端的代理支持\
编译osx下的可执行文件
