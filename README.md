## 网易云音乐爬虫

### 启动
   ```
   1.自行更换讯代理的订单号及密钥，位置：\src\main\java\com\sag\util\Constants.java
   2.指定初始爬取地址，位置：\src\main\java\com\sag\SpiderApplication.java
   3.配置数据库地址并创建数据库music，位置：\src\main\resources\application.yml
   4.运行SpiderApplication.java
   ```

### 需求
   ```
   1.爬取一首歌的所有评论
   2.爬取一个歌单中所有歌曲的评论
   
   //以下待实现
   3.从一个人创建的歌单里筛选Ta的所有评论
   4.断点续爬
   5.爬取新增评论
   6.自动去重
   ```
### 实现
   ```
   Java框架：springboot
   爬虫框架：webmagic
   代理服务方：讯代理
   数据库：mysql
   ```
### 项目暂停维护说明
   ```
   网易云策咯更改：经测试发现评论在爬取到5000条之后便不再返回数据，网页亲测，一首歌的评论只能点到前后251页，APP没有翻页只能往下拉不知道是不是也这样，懒得测了。
   ```
   