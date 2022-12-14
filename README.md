## 自动安全上报
#### 核心服务实现
- 由于安全上报服务器不稳定，由springboot定时任务实现每日``8,9,10,11,12,13``点自动触发上报服务
- 上报成功后，将在redis存放flag标记今日已上报，再次触发上报服务时，会检查该字段是否存在，若存在则`continue`，该标记字段在每日3点自动清空
- 由于`ttoken`字段嵌入在html页面内，通过正则表达式提取`ttoken`
- 请求过往提交数据表单，筛选过去第三天的数据填充当前安全上报的请求参数表单
- 在`reportService`上报服务执行中，对于登录至安全上报系统失败的用户，系统会自动删除账号，并邮件提示更新信息
- 后台具有拦截已存在用户的逻辑，但对于初次注册的用户重复发送提交请求，为了防止插入重复字段，使用``Redis缓存锁``，在初次请求注册服务的时候进行缓存标记，在结束服务后释放缓存
- 前端响应式布局
- 密码使用AES对称加密存储。为什么不使用非对称或者不可逆加密呢？非对称加密在此场景属实没有必要，密码和私钥一起泄露；不可逆加密正如其名，密码是用来请求的而非用来验证

#### 部署注意
- 安全上报服务器限制了IP地址的访问，在省外的请求均无法访问安全上报平台
- 由于提供华中地区云计算服务的厂商不多(大厂只有百度云提供武汉)，目前的想法是在校内搭建一台微服务器，连接至校内网，然后进行内网穿透，将内网设备的端口映射到公网服务器的端口上，访问公网服务器，进行请求转发


#### 后端
| 技术栈  | 说明 |
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

#### 前端
| 技术栈  | 说明 |
|------------|---------------|
|  Vue2 | JavaScript框架 |
|  ElementUI | UI组件库 |
|  Axios  | promise网络请求库 |
