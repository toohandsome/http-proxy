# http-proxy

## agent 模式
一款支持在运行时无侵入的获取java程序的 所有发出(已完成)与接收(开发中)的 http 请求的 agent .

目前支持的http客户端 
1. HttpURLConnection
2. HttpClient
3. Okhttp
4. 待添加


特性:
1. 无视https加密, 因为是注入到jvm内部,所以无需关心https,也可以解决proxy 模式 ssl证书问题
2. 支持白名单,指定不拦截的url地址
3. 支持输出请求发起时的堆栈信息

## 代理模式

类似nginx, 可以对请求进行转发

在转发过程中可以对 请求头,请求体,响应头,响应体进行输出或修改

不支持 长连接