# Rico AIGC平台
前端项目地址：https://github.com/ricoguo0228/ricobi-frontend-master

项目背景：学校工作室项目，从 0 开始自主搭建的 AIGC 平台，该平台开发旨在解决不同专业学生对 AI 的需求，核心业务为 AI Chat 和 数据分析功能，平台将结合 AI 能力为用户回答问题，或提供详尽的数据分析与图表展示。

前端技术选型：React（使用Ant Design Pro进行快捷开发），SwiftUI

后端技术选型：SpringBoot，MySQL，MyBatisPlus，Redis，RabbitMQ

主要工作：
1.自定义业务异常、通用返回类和返回码，实现了统一的前后端消息交互，方便错误的排查，提高系统可维护性和健壮性。

2.使用 Spring AOP 实现用户鉴权，对管理员、普通用户、VIP 用户进行区分

3.使用 Redis 的 List 数据结构将图表代码、AI 回答内容进行缓存，查询时先查缓存，如果命中直接返回，实测响应速度提升了30%。

4.基于 Redisson 的 RateLimiter 实现分布式限流，使用令牌桶算法控制单用户访问的频率，防止某用户恶意占用系统资源。

5.使用 Easy Excel 解析用户上传的 XLSX 表格数据文件并压缩为 CSV ，解决了对于数据分析 AI Model 输入 Token 长度的限制问题

6.基于 RabbitMQ 的发布-订阅模式实现了 AI 调用的并发执行和异步化，解决了本地任务队列重启丢失数据的问题。

7.前端使用 ECharts 展示用户要求生成的图表，后端调用 AI 接口只需返回 ECharts 代码即可，提升了 AI 响应速度。

8.可选性接入自训练 AI 模型代替第三方 API 实现核心业务，实现了业务功能拓展（AI 绘画）。

