# 一、ELK+redis+filebeat配置

> - 使用beat的原因为logstash需要依赖java环境，对cpu和内存消耗都比较大，所以filebeat应运而生，对cpu和内存基本没有消耗。
> - 使用redis 的原因是当多个clinet同时写入到logstash或者elasticsearch 时候，有io瓶颈，所以选择了redis ,当然可以使用kafka，rabbitmq等消息中间件。

1. **Elasticsearch**

    ```
    双击elasticsearch.bat启动

    访问localhost:9200
    ```

2. **Kibana**

   ```
   双击 kibana.bat 启动
   
   访问localhost:5601
   ```

3. **Redis**

   ```
   双击 redis-server.exe
   ```

4. **Filebeat**

   ```
   对应目录下，CMD窗口，输入 
   
   filebeat -e -c filebeat2.yml
   ```

5. **logstash**

   ```
   这里conf文件跟logstash.bat处于同一目录下，对应目录下，CMD窗口
   
   logstash -f logstash-sample.conf
   ```

## 步骤

> 1. 启动redis，启动filebeat，读取日志文件，将日志文件数据写入redis（使用redis是防止大批量日志的时候logstash无法及时处理）
>
> 2. 启动ES、Kibana、logstash，logstash读取redis中存入的日志数据，写入到ES中
>
> 总结：当日志文件发生变化时，filebeat会将数据传送给redis，redis也会再次接收数据，logstash将数据格式化写入ES

## logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--定义日志文件的存储地址-->
    <property name="log_home" value="D:/logs/projectManagement/"/>

    <!-- 控制台输出 -->
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
<!--            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{20} - [%method,%line] - %msg%n</pattern>-->
            <pattern>|- %-12(%d{yyyy-MM-dd HH:mm:ss}) %-5level [%thread] %c : %L %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 按照每天生成日志文件,系统日志输出 -->
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <fileNamePattern>${log_home}%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志最大的历史 30天 -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
<!--            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{80} - %msg%n</pattern>-->
            <pattern>|- %-12(%d{yyyy-MM-dd HH:mm:ss}) %-5level [%thread] %c : %L %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!-- 日志输出级别 -->
    <root level="info">
        <appender-ref ref="stdout"/>
        <appender-ref ref="file"/>
    </root>
</configuration>
```

## application.yml配置日志

```yml
#日志配置
logging:
  level:
    com.tw: debug
```

## filebeat.yml

```yaml
#=========================== Filebeat prospectors =============================
filebeat.prospectors:
- type: log
  #开启监视，不开不采集
  enabled: true
  paths:
    - D:\logs\projectManagement\*.log
  encoding: utf-8
  fields:
    log_source: project_management # logstash判断日志来源
#-------------------------- reids output ------------------------------
output.redis:
  hosts: ["127.0.0.1:6379"]
  key: filebeat
  db: 4
  timeout: 5s
  max_retries: 3
```

## 创建日期格式化文件夹

> 1. 在bin目录下创建patterns文件夹（文件名称自定义）
> 2. 创建配置文件 patterns.conf 内容如下：

```yaml
MYSELFTIMESTAMP 20%{YEAR}-%{MONTHNUM}-%{MONTHDAY} %{HOUR}:?%{MINUTE}(?::?%{SECOND})
JAVACLASS (?:[a-zA-Z$_][a-zA-Z$_0-9]*\.)*[a-zA-Z$_][a-zA-Z$_0-9]*
MATCH_ANY [\s\S]*
JAVA_LOG \|\- %{MYSELFTIMESTAMP:timestamp}(\s*)%{LOGLEVEL:level}(\s*)\[%{MATCH_ANY:thread}\](\s*)%{JAVACLASS:class}([\s\S]*)\: %{NUMBER:line} %{MATCH_ANY:logmessage}
```

## logstash-sample.conf

```yaml
# 从redis中获取数据
input {
  redis {
    host => "127.0.0.1"
    port => "6379"
    data_type => "list"
    key => "filebeat"
	db => 4
    type => "redis-input"
  }
}
filter {
    grok {
        # 正则文件存放目录 文件名叫什么无所谓
        patterns_dir => "./patterns"
        match => {
           "message" => "%{JAVA_LOG}"
        }
        remove_field => ["message"]
    }
}
output {
  elasticsearch {
    action => "index"  
    hosts  => "127.0.0.1:9201"  
    # 索引名称
    index  => "logmanagement"         
  }
}
```

# 二、搜集不同项目的日志文件

> 1. filebeat扫描多个项目的日志文件
>
> 2. 由于日志文件格式相同，以fields--log_source进行区分，直接输出到一个redis中
> 3. logstash接收redis中对应数据
> 4. logstash对日志信息进行拆分，移除不需要的日志，
> 5. logstash将日志信息输出到ES中，根据 [fields] [log_source]的不同，创建不同的索引，将数据对应加入ES

## filebeat.yml

```yaml
#=========================== Filebeat prospectors =============================
filebeat.prospectors:
- type: log
  #开启监视，不开不采集
  enabled: true
  paths:
    - D:\logs\projectManagement\*.log
  encoding: utf-8
  fields:
    log_source: project_management # logstash判断日志来源
    
- type: log
  #开启监视，不开不采集
  enabled: true
  paths:
    - D:\logs\projectManagement\*.log
  encoding: utf-8
  fields:
    log_source: project_management2 # logstash判断日志来源
#-------------------------- reids output ------------------------------
output.redis:
  hosts: ["127.0.0.1:6379"]
  key: project1_2
  db: 4
  timeout: 5s
  max_retries: 3
```

## logstash-sample.conf

```yaml
# For detail structure of this file
# Set: https://www.elastic.co/guide/en/logstash/current/configuration-file-structure.html
input {
  redis {
    host => "127.0.0.1"
    port => "6379"
    data_type => "list"
    key => "project1_2"
	db => 4
    type => "redis-input"
  }
}
filter {
    grok {
		# 正则文件存放目录
		patterns_dir => "./patterns"
		match => {
		   "message" => "%{JAVA_LOG}"
		}	
		remove_field => ["message"]
    }
	if ([level]!= "INFO" and [level]!= "DEBUG" and [level]!= "ERROR" and [level]!= "WARN"){
		drop {}
	}
}
output {
	if [fields][log_source] == 'project_management' {
	  elasticsearch {
		action => "index"  
		hosts  => "127.0.0.1:9201"  
		index  => "pmlog"         
	  }
    }
	if [fields][log_source] == 'project_management2' {
	  elasticsearch {
		action => "index"  
		hosts  => "127.0.0.1:9201"  
		index  => "pmlog2"         
	  }
    }
}
```

## ES日志数据划分信息

| 文档字段   | 文档名称               | 备注                |
| ---------- | ---------------------- | ------------------- |
| level      | 日志级别               | INFO DEBUG          |
| logClass   | 类                     | 该日志对应哪个类    |
| timestamp  | 日志产生时间           | 2020-07-08 14:12:28 |
| thread     | 线程                   | restartedMain       |
| logMessage | 日志信息               |                     |
| line       | 日志信息对应类的哪一行 |                     |

## filebeat从头开始读取log文件

> 删除安装目录下的data文件夹中的数据，registry中记载了最后一次读取数据的offset

## 对text类型分组

设置mapping的时候需要设置fielddata 为true

```
PUT my_index/_mapping/my_type
{
  "properties": {
    "my_field": { 
      "type":     "text",
      "fielddata": true
    }
  }
}
```

# 三、Logstash 自定义模板

## logstash-sample.conf

```yaml
# For detail structure of this file
# Set: https://www.elastic.co/guide/en/logstash/current/configuration-file-structure.html
input {
  redis {
    host => "127.0.0.1"
    port => "6379"
    data_type => "list"
    key => "logs"
	db => 4
    type => "redis-input"
  }
}
filter {
    grok {
		# 正则文件存放目录
		patterns_dir => "./patterns"
		match => {
		   "message" => "%{JAVA_LOG}"
		}	
		remove_field => ["message"]
    }
	if ([level]!= "INFO" and [level]!= "DEBUG" and [level]!= "ERROR" and [level]!= "WARN"){
		drop {}
	}	
	# 解决日期问题 将日期转换成UNIX格式，避免时差
	date {
		match => [ "logTime", "yyyy-MM-dd HH:mm:ss" ]
		target => "logTime"
		timezone =>"+00:00"
	}
}
output {
	if [fields][log_source] == 'project_management' {
	  elasticsearch {
		action => "index"  
		hosts  => "127.0.0.1:9201"  
		index  => "log_project"  
		template => "../config/project.json"
		template_name => "log_project"
		template_overwrite => true		
	  }
    }
	if [fields][log_source] == 'log_management' {
	  elasticsearch {
		action => "index"  
		hosts  => "127.0.0.1:9201"  
		index  => "log_log"    
		template => "../config/log.json"
		template_name => "log_log"
		template_overwrite => true
	  }
    }
}
```

## log.json

```json
{
    "template":"log_log",
    "settings":{
        "index.refresh_interval":"1s"
    },
    "mappings":{
        "log":{
            "properties":{
                "level":{
                    "type":"keyword"
                },
                "line":{
                    "type":"keyword"
                },
                "logMessage":{
                    "type":"text"
                },
				"thread":{
                    "type":"text"
                },
				"logClass":{
                    "type":"text"
                },
				"logTime":{
                    "type":"date"
                }
			
            }
        }
    }
}
```

