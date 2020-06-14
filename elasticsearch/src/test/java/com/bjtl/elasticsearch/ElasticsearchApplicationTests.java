package com.bjtl.elasticsearch;

import com.bjtl.entity.Book;
import com.bjtl.repository.BookRepo;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchApplicationTests {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    public void createIndex() throws Exception {
        // 创建索引，并配置映射关系
        //template.createIndex(Book.class);
        // 单独配置映射关系
        template.putMapping(Book.class);
    }

    @Test
    public void addDocument() throws Exception {
        for (int i = 1; i < 10; i++) {
            bookRepo.save(new Book(i + "", i + "西游记", i + "罗贯中"));
        }
        for (int i = 10; i < 20; i++) {
            bookRepo.save(new Book(i + "", i + "中国梦", i + "富强民主文明和谐"));
        }
        for (int i = 20; i < 30; i++) {
            bookRepo.save(new Book(i + "", i + "阿雷", i + "好好学习，天天向上"));
        }
    }

    @Test
    public void deleteDocumentById() throws Exception {
        bookRepo.deleteById("14");
    }

    @Test
    public void deleteAllDocument() throws Exception {
        bookRepo.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        Iterable<Book> books = bookRepo.findAll();
        books.forEach(a -> System.out.println(a));
    }

    @Test
    public void findById() throws Exception {
        Optional<Book> optional = bookRepo.findById("5");
        Book book = optional.get();
        System.out.println(book);
    }

    @Test
    public void findByName() throws Exception {
        List<Book> books = bookRepo.findByName("西游记");
        books.stream().forEach(a -> System.out.println(a));
    }

    @Test
    public void findByNameOrIntroduce() throws Exception {
        Pageable pageable = PageRequest.of(0, 25);
        bookRepo.findByNameAfterOrIntroduce("雷", "和谐", pageable).forEach(a -> System.out.println(a));
    }

    @Test
    public void nativeSearchQuery() throws Exception {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("阿雷喜欢西游记").defaultField("name"))
                .withPageable(PageRequest.of(0, 15))
                .build();
        List<Book> list = template.queryForList(query, Book.class);
        list.forEach(a -> System.out.println(a));
    }

}
