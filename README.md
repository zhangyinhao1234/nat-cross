# nat-cross
nat-cross是基于Netty实现的内网穿透工具,目前支持TCP和HTTP协议。

# 什么是内网穿透
简单的说内网穿透是一种技术手段，它允许用户从外部网络（如互联网）访问位于内网（如家庭或企业局域网）中的设备或服务。

# 内网穿透常见的应用场景
* 远程办公：员工可以在家中通过内网穿透技术访问公司内网的文件服务器、数据库等资源，实现远程办公。
* 智能家居控制：用户可以通过互联网远程控制家中的智能设备，如智能摄像头、智能插座等。
* 开发调试：开发者可以使用内网穿透工具将本地开发环境映射到外网，方便进行远程调试和测试，
* * 例如在开发微信公众号服务时，通过内网穿透让微信服务器能够访问本地开发接口。
* * 例如和供应商进行开发调试时候,可以将内网服务暴露到外网,让供应商通过内网穿透工具访问本地服务。


# 部署自己的内网穿透服务的好处
* 使用免费的内网穿透工具存在流量限制问题，需要付费。
* 使用免费的内网穿透工具偶尔会映射失败,自己之前遇到过,导致调试中断了半天。

# 启动
### 服务端启动
租一台带有公网 IP 的服务器,在服务器上执行 Java 命令:
```
java -jar nat-cross-server.jar
```
启动成功后,会看到如下输出:
```
Server Start Success Port Is : 10060
```
服务器默认的端口号是10060,你可以通过修改[server.properties](conf%2Fserver.properties)调整端口号
客户端连接服务端需要通过Token连接,你可以在[token.properties](conf%2Ftoken.properties)进行配置

### 客户端启动
```
java -jar nat-cross-client.jar
```
启动成功后,会看到如下输出: 然后使用 {server.addr}:{server.proxy.port} 访问你的服务
```
Register Success, ServerProxyPort : 38080 
```

其他启动错误:
```
Token Cons Is Gone : [连接已经被占满]
```

# 文件下载
* https://github.com/zhangyinhao1234/nat-cross/releases/tag/v1.0.0

# 免费使用
* 个人服务器2025年2月10号到期,到时候服务会停用
* 使用下载的客户端,修改 conf 中的配置文件,在本地启动 nat-cross-client.jar ;使用 124.222.52.154:{server.proxy.port} 访问你的服务
* 免费的服务设置了最大连接数200个,服务器代理服务开放的端口是[30000~40000],如果发现线路繁忙无法连接请联系我,提供单独的Token zhangyinhao1234@163.com

