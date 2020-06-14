package com.bjtl.repository;

import com.bjtl.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.util.List;

public interface BookRepo extends ElasticsearchRepository<Book, String> {

    List<Book> findByName(String name);

    List<Book> findByNameAfterOrIntroduce(String name,String introduce);

    List<Book> findByNameAfterOrIntroduce(String name, String introduce, Pageable pageable);
}