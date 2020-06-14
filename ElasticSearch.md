# 学习ElasticSearch

Elasticsearch，简称es，es是一个开源的==高扩展==的==分布式全文搜索引擎==，可以近乎==实时的存储、检索数据==；本身的扩展性很好，可以扩展到上百台服务器，处理PB级别（大数据时代）的数据，es也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能，但是它的目的是通过简单的==RESTful API==来隐藏Lucene的复杂性，从而让全文搜索变得简单。

## Elasticsearch和Solr比较

- 当单纯的对已有的数据进行搜索时，Solr更快。
- 当实时建立索引时，Solr会产生io阻塞，查询性能较差，Elasticsearch具有明显的优势。
- 转变我们的搜索基础设施后，从Solr 到 Elasticsearch，可提高50的搜索性能

## ElasticSearch 和 Solr总结

1. es基本时开箱即用（解压就可以使用！），非常简单，Solr安装略微复杂些。
2. Solr利用Zookeeper进行分布式管理，而Elasticsearch自身带有分布式协调管理功能。
3. Solr支持更多格式的数据，比如JSON、XML、CSV,而ElasticSearch仅支持JSON文件格式。
4. Solr官方提供的功能更多，而ElasticSearch本身更注重于核心功能，高级共嗯那个多由第三方插件提供，例如图形化界面需要kibana友好支撑。
5. Solr查询快，但更新索引时慢（即插入删除慢），用于电商等查询多的应用。
   - ES建立索引块（即查询慢），即实时性查询快，用于facebook 新浪等搜索。
   - Solr时传统搜索应用的有力解决方案，但ElasticSearch更适用于新兴的实时搜索应用。
6. Solr比较成熟，有一个更大、更成熟的用户，开发和贡献者社区，而ElasticSearch相对开发维护者较少，更新太快，学习使用成本较高。

## 熟悉目录

![image-20200610095750791](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610095750791.png)

```
bin 启动文件
config 配置文件
	log4j2 日志配置文件
	jvm.options Java虚拟机相关的配置 将-xms设置为256m
	elasticsearch.yml elasticsearch 的配置文件！ 默认 9200端口！跨域！
lib      相关jar包
logs     日志
modules  功能模块
plugins  插件	
```

> **启动**

1. 双击bin目录下的 elasticsearch.bat，默认访问9200端口

   ![image-20200610101005300](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610101005300.png)

2. 访问测试

   ![image-20200610101140531](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610101140531.png)

> 安装可视化界面 ES head的插件

1. Github下载

2. 安装node.js 官网下载 安装完成后再cmd窗口中输入**node --version**，查看版本号

   ![image-20200610102916606](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610102916606.png)

3. 启动

   ```bash
   npm install
   npm run start
   ```

4. 连接测试发现端口跨域,配置ES 的yum文件

   ```bash
   http.cors.enabled: true
   http.cors.allow-origin: "*"
   ```

5. 重启ES服务器，然后再次连接

   ![image-20200610134909037](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610134909037.png)

## 安装Kibana

1. Kibana是一个针对ElasticSearch的开源分析及可视化平台，用来搜索，查看交互存储在ElasticSearch索引中的数据
2. 使用Kibana，可以通过各种图表进行高级数据分析及展示
3. Kibana让海量数据更容易理解
4. 操作简单，基于浏览器的用户界面可以快速创建仪表板（dashbard）实时显示EalsticSearch查询动态
5. 设置Kibana非常简单，无需编码或者额外的基础架构，几分钟内就可以完成Kibana安装并启动ElasticSearch索引监测

官网：https://www.elastic.co/cn/kibana

找到与ElasticSearch对应的版本下载即可

> 启动测试

1. 解压后的目录

   ![image-20200610151152051](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610151152051.png)

2. 启动 bat文件

   ![image-20200610152303304](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610152303304.png)

3. 访问测试

   ![image-20200610152344858](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610152344858.png)

4. 开发工具

   ![image-20200610152513578](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200610152513578.png)

5. 汉化

   7.x 修改kibana.yum文件 最后加上 **i18n.defaultLocale: "zh-CN"**

## IK分词器

- 分词：即把一段中文或者别的划分成一个个关键字，我们在搜素的时候会把自己的信息进行分词，会把数据库中或者索引库中的数据进行分词，然后进行一个匹配操作，默认的中文分词时将每个字看成一个词
- 如果使用中文，建议使用IK分词器
- IK提供了两个分词算法：ik_smart 和 ik_max_word，其中ik_smart为最少切分，ik_max_word为最细粒度划分

> 安装IK分词器

1. GitHub下载：https://github.com/medcl/elasticsearch-analysis-ik

   ik分词器各个版本下载地址：
   https://github.com/medcl/elasticsearch-analysis-ik/releases

2. 下载完毕后，放入到我们的elasticsearch插件中即可

> 启动Kibana 使用ik分词器

![image-20200611165430658](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200611165430658.png)

## 基本测试

1. 创建一个索引

   ```
   PUT /索引名/~类型名~/文档id
   {请求体}
   ```

   ![image-20200611170052872](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200611170052872.png)

   ![image-20200611170306665](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200611170306665.png)

2. 使用postman 创建一个索引

   ![image-20200611184554114](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200611184554114.png)

   ```json
   {
       "mappings":{
           "article":{
               "properties":{
                   "id":{
                       "type":"long",
                       "store":true
                   },
                   "title":{
                       "type":"text",
                       "store":true,
                       "index":true,
                       "analyzer":"standard"
                   },
                   "content":{
                       "type":"text",
                       "store":true,
                       "index":true,
                       "analyzer":"standard"
                   }
                }
           }
       }
   }
   ```

3. 创建索引之后添加mappings

   - 使用post请求方式

   - http://127.0.0.1:9200/blog/hello/_mappings

   - 内容为：

     ```json
     {
     	"hello":{
     		"properties":{
     			"id":{
                         "type":"long",
                         "store":true
                     },
                 "title":{
                     "type":"text",
                     "store":true,
                     "index":true,
                     "analyzer":"standard"
                 },
                 "content":{
                     "type":"text",
                     "store":true,
                     "index":true,
                     "analyzer":"standard"
                 }
     		}
     	}
     }
     ```

   4. 向索引库中添加文档
   
      **url中末尾的id对应 ==_id==**
   
      ![image-20200612090649884](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612090649884.png)
   
      ![image-20200612090726789](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612090726789.png)
   
      **若url中不写末尾的Id,则会默认生成一个字符串，类似于UUID**
   
      ![image-20200612090939466](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612090939466.png)
   
   5. 删除文档
   
      删除文档根据 ==_id== 进行删除。
   
      ![image-20200612091411362](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612091411362.png)
   
   6. 修改文档
   
      修改文档相当于再次添加的覆盖，以新数据覆盖旧数据
   
      使用post请求方式，url中设置好需要修改的 ==_id== ，json中写入新内容提交即可
   
      ![image-20200612092151404](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612092151404.png)
   
   7. 根据id查看文档
   
      ![image-20200612093131669](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612093131669.png)
   
   8. 根据关键词进行查询
   
      (==这里使用的是标准分词器 一个汉字一个关键词 若使用大于一个汉字关键词查不到==)
   
      ![image-20200612093743017](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612093743017.png)
   
      ![image-20200612093854153](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612093854153.png)
   
   9. queryString查询
   
      ==先对字符串进行分词，然后进行查询==
   
      ![image-20200612094617061](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612094617061.png)
   
      查询结果：
   
      ```json
      {
          "took": 63,
          "timed_out": false,
          "_shards": {
              "total": 5,
              "successful": 5,
              "failed": 0
          },
          "hits": {
              "total": 3,
              "max_score": 4.9065104,
              "hits": [
                  {
                      "_index": "blog",
                      "_type": "hello",
                      "_id": "4",
                      "_score": 4.9065104,
                      "_source": {
                          "id": 4,
                          "title": "实时更新：新型冠状肺炎全国疫情地图 ",
                          "content": "实时更新：新型冠状肺炎全国疫情地图"
                      }
                  },
                  {
                      "_index": "blog",
                      "_type": "hello",
                      "_id": "2",
                      "_score": 0.5640044,
                      "_source": {
                          "id": 2,
                          "title": "新添加的文档",
                          "content": "新添加的文档的内容"
                      }
                  },
                  {
                      "_index": "blog",
                      "_type": "hello",
                      "_id": "1",
                      "_score": 0.2824934,
                      "_source": {
                          "id": 1,
                          "title": "修改新标题",
                          "content": "修改新内容"
                      }
                  }
              ]
          }
      }
      ```
   
   10. ES集成IK分词器
   
       ![image-20200612100139990](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612100139990.png)
   
       ![image-20200612100310424](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612100310424.png)

## ES搭建集群

1. 复制三份ElasticSearch文件

2. 修改elasticsearch.yml

   ```yml
   #解决跨域
   http.cors.enabled: true
   http.cors.allow-origin: "*"
   
   #节点2的配置信息：
   #集群名称，保证唯一
   cluster.name: ceshi-elasticsearch
   #节点名称，必须不一样
   node.name: node-2
   #必须为本机的ip地址
   network.host: 127.0.0.1
   #服务端口号，在同一机器下必须不一样  使用 9201 9201 9203即可
   http.port: 9202
   #集群间通信端口号，在同一机器下必须不一样  使用 9201 9201 9203即可
   transport.tcp.port: 9302
   #设置集群自动发现机器ip集合
   discovery.zen.ping.unicast.hosts: ["127.0.0.1:9301","127.0.0.1:9302","127.0.0.1:9303"]
   ```

3. 三个ES都启动起来

   ![image-20200612103232062](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612103232062.png)

4. 直接新建索引 （5个分片 一个副本 相当于10个分片）

   ![image-20200612103408279](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612103408279.png)

   ![image-20200612103630529](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612103630529.png)

5. 使用postman创建索引

   ![image-20200612104443610](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612104443610.png)

   ![image-20200612104453914](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200612104453914.png)

   