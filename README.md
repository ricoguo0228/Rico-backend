# RBI AIGC平台
前端项目地址：https://github.com/ricoguo0228/ricobi-frontend-master

项目介绍：从 0 开始自主搭建的数据分析平台，该平台开发旨在解决非专业用户的数据分析问题，用户上传 Excel 表格，平台结合AI能力为用户提供详尽的数据分析与图表展示

技术选型：React（使用Ant Design Pro进行快捷开发），SpringBoot，MySQL，MyBatisPlus，Redis，RabbitMQ

主要工作：

1.自定义业务异常、通用返回类和返回码，实现了统一的前后端消息交互，方便错误的排查，提高系统可维护性和健壮性。

2.使用 Redis 的 List 数据结构将图表代码、AI 回答内容进行缓存，查询时先查缓存，如果命中直接返回，实测响应速度提升了30%。

3.基于 Redisson 的 RateLimiter 实现分布式限流，使用令牌桶算法控制单用户访问的频率，防止某用户恶意占用系统资源。

4.使用 Easy Excel 解析用户上传的 XLSX 表格数据文件并压缩为 CSV ，解决了AIGC 输入 Token 长度的限制问题，实测提高了 50% 的单次输入数据量。

5.基于 RabbitMQ 的发布-订阅模式实现了 AI 调用的并发执行和异步化，解决了本地任务队列重启丢失数据的问题。

6.使用 Knife4j + Swagger 自动生成后端接口文档，并通过编写 ApiOperation 等注解补充接口注释，避免了人工编写维护文档的麻烦。
