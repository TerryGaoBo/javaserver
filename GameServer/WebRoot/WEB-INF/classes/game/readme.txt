
1. http/socket java 服务器
2. 框架（模块搭建）
3. 链接MySql
4. EXCEL都保存到数据库中（导表的一种方式）或者把EXCEL导出JSON文件格式

java 手游服务器
1.数据库：mysql
2.开发语言:java
3.第三方库：mina/Netty 目前首选：netty,spring
4.数据统计：php/或者第三方分析平台（这个目前暂时不考虑）
5.部署配置：阿里云服务器(windows)
6.目前分游戏逻辑服务器，游戏数据服务器（暂时制作游戏逻辑服务器）
7.目前通信是HTTP，socket暂时不用
8.Hibernate 链接数据库
9.服务器收到客户端的消息的时候，先在数据库中删除在处理
那些不足或者不准确，欢迎补充

登陆服务器
游戏服务器
日志服务器
支付服务器

本地服务器：需要安装：
Mac：tomcat(类似Apache）

Servlet:
客户端发送请求至服务器
服务器启动并调用 Servlet，Servlet 根据客户端请求生成响应内容并将其传给服务器
服务器将响应返回客户端

javaBean:
JavaBean 是一种JAVA语言写成的可重用组件。
为写成JavaBean，类必须是具体的和公共的，并且具有无参数的构造器。
JavaBean 通过提供符合一致性设计模式的公共方法将内部域暴露成员属性。
众所周知，属性名称符合这种模式，其他Java 类可以通过自身机制发现和操作这些JavaBean 的属性。

J2EE
J2SE

netty:	http://netty.io/wiki/


json:http://www.json.org/java/index.html



/////////////////////////
命令 lsof -i tcp:port  （port替换成端口号，比如6379）可以查看该端口被什么程序占用，并显示PID，方便KILL


http: 消息定义：
json 格式：
消息类型+消息id+消息内容
消息id+消息内容

模块id 找到对应的模块
每个模块又分许多的消息
模块id 消息id（针对的时每个模块）

一点一点的完善

