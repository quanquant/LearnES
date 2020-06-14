package com.bjtl.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "books",type = "library")
public class Book implements Serializable {
    /**
     *     index：是否设置分词
     *     analyzer：存储时使用的分词器
     *     searchAnalyze：搜索时使用的分词器
     *     store：是否存储
     *     type: 数据类型
     */

    @Id
    @Field(type = FieldType.Text,store = true,index=false)
    private String id;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String name;
    @Field(type = FieldType.Text,store = true,analyzer = "ik_smart")
    private String introduce;
}
