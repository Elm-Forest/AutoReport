## CTGU自动安全上报系统
springboot定时任务实现每日8，9，10，11，12，13点自动触发上报服务\
在上报服务启动中，对于登录至安全上报系统失败的用户，系统会自动删除账号，并邮件提示更新信息\
\
安全上报服务器限制了IP地址的访问，在湖北省外的请求均无法访问安全上报平台\
\
目前的想法是在校内搭建一台微服务器，连接至校内网，然后进行内网穿透，将内网设备的端口映射到公网服务器的端口上，访问公网服务器，进行请求转发。


#### 项目技术栈
| 框架  | 说明 |
|------------|---------------|
|  SpringBoot | 容器+MVC框架  |
|  MyBatis-Plus | ORM框架  |
|  Hutool  | 工具类  |
|  Redis |  缓存数据库 |
|  Druid |  数据库连接池 |
|log4j2|日志收集工具|
|Maven|项目构建工具|
|Docker|服务端部署工具|
|Ngnix|反向代理服务器|
