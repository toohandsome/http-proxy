# http-proxy

## agent 模式
一款支持在运行时无侵入的获取 java 程序的 所有发出(已完成)与接收(开发中)的 http 请求的 agent .

目前支持的 http 客户端 
1. HttpURLConnection
2. HttpClient
3. Okhttp
4. 待添加


### 特性:
1. 无视 https 加密, 因为是注入到 jvm 内部,所以无需关心 https,也可以解决 proxy 模式 ssl 证书问题
2. 支持白名单,指定不拦截的 url 地址
3. 支持输出请求发起时的堆栈信息
4. 提供 web 页面进行操作与查看,无需在 console 操作

## 代理模式

类似 nginx, 可以对请求进行转发

在转发过程中可以对 请求头,请求体,响应头,响应体进行输出或修改

不支持 长连接