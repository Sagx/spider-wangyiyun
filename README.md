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
   3.从一个人创建的歌单里筛选Ta的所有评论（待实现）
   ```
### 实现
   ```
   Java框架：springboot
   爬虫框架：webmagic
   代理服务方：讯代理
   数据库：mysql
   ```
   