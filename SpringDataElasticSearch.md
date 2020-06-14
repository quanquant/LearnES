# SpringDataElasticSearch

1. 工程搭建

   - 创建一个Java工程

   - Maven添加相应的坐标

     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
         <modelVersion>4.0.0</modelVersion>
     
         <groupId>com.bjtl</groupId>
         <artifactId>springdata-elasticsearch</artifactId>
         <version>1.0-SNAPSHOT</version>
         <parent>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-parent</artifactId>
             <version>2.0.4.RELEASE</version>
             <relativePath/>
         </parent>
     
         <properties>
             <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
             <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
             <java.version>1.8</java.version>
             <!-- 自己定义es 版本依赖，保证和本地一致 -->
             <elasticsearch.version>5.5.3</elasticsearch.version>
         </properties>
     
         <dependencies>
     
             <!-- Spring Boot Start -->
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-test</artifactId>
                 <scope>test</scope>
             </dependency>
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
             </dependency>
     
             <!-- Tool Start -->
             <dependency>
                 <groupId>com.alibaba</groupId>
                 <artifactId>fastjson</artifactId>
                 <version>1.2.62</version>
             </dependency>
             <dependency>
                 <groupId>junit</groupId>
                 <artifactId>junit</artifactId>
                 <version>4.12</version>
                 <scope>test</scope>
             </dependency>
         </dependencies>
     </project>
     ```

   - 创建一个spring的配置文件

     1. 配置elasticsearch:transport-client 客户端配置
     2. 配置elasticsearch:repositories 包扫描器，扫描dao接口
     3. 配置 elasticsearchTemplate 对象，就是一个bean

     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:context="http://www.springframework.org/schema/context"
            xmlns:elasticsearch="http://www.springframework.org/schema/data/elasticsearch"
            xsi:schemaLocation="
             http://www.springframework.org/schema/beans
             http://www.springframework.org/schema/beans/spring-beans.xsd
             http://www.springframework.org/schema/context
             http://www.springframework.org/schema/context/spring-context.xsd
             http://www.springframework.org/schema/data/elasticsearch
             http://www.springframework.org/schema/data/elasticsearch/spring-elasticsearch-1.0.xsd ">
         <!--ES客户端的配置-->
         <elasticsearch:transport-client id="esClient" cluster-name="ceshi-elasticsearch"
                                         cluster-nodes="127.0.0.1:9301,127.0.0.1:9302，127.0.0.1:9303"/>
         <!--配置包扫描器，扫描dao接口-->
         <elasticsearch:repositories base-package="com.bjtl.es.repositories"/>
     
         <bean id="elasticsearchTemplate" class="org.springframework.data.elasticsearch.core.ElasticsearchTemplate">
             <constructor-arg name="client" ref="esClient"/>
         </bean>
     </beans>
     ```

2. 管理索引库

   - 创建一个Entity类，映射到一个Document，需要添加一些注解进行标注
- 创建一个Dao，是一个接口，需要集成ElasticSearchRepositoriesepositories接口
   - 编写测试代码

3. 创建索引

   - 直接使用ElasticSearchTemplate对象的createIndex方法创建索引，并配置映射关系

4. 添加、更新文档

   - 创建一个对象
   - 使用接口对象的deleteById方法直接删除

5. 查询索引库

   - 直接使用接口对象的查询方法

6. 自定义查询方法

   - 需要根据SpringDataES命名规则来命名

   - 如果不设置分页，默认带分页，每页显示10条

   - 如果设置分页，应在方法中添加一个参数pageable(默认从第0页开始)

     ```
      Pageable pageable = PageRequest.of(0,25);
     ```

   - 可以对搜索内容先分词后进行查询，每个词之间都是and关系

     > 例如：我爱学习  我 爱 学习 之间都是and的关系，必须全部包含这些才能查询到，写成我也爱学习的话，多了个也，就查询不到了

7. 使用原生的条件查询(满足其中一个或多个分子都可以查询出结果)

   - NativeSearchQuery对象

   - 使用方法：

     > 1. 创建一个NativeSearchQuery对象（设置查询条件，QueryBuilder对象）
     > 2. 使用ElasticSearchTemplate对象执行查询
     > 3. 取查询结果

     ```java
     NativeSearchQuery query = new NativeSearchQueryBuilder()
         .withQuery(QueryBuilders.queryStringQuery("阿雷喜欢西游记").defaultField("name"))
         .withPageable(PageRequest.of(0, 15))
         .build();
     List<Book> list = template.queryForList(query, Book.class);
     list.forEach(a -> System.out.println(a));
     ```

     



