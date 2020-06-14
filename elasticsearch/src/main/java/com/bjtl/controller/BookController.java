package com.bjtl.controller;

import com.bjtl.entity.Book;
import com.bjtl.repository.BookRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

public class BookController {
    @Resource
    private BookRepo bookRepo;

    /*@GetMapping("/book/get/{id}")
    public Book getBook(@PathVariable String id) {
        return bookRepo.findById(id).get();
    }*/

    @PostMapping("/book/put")
    public Book putBook(@RequestBody Book book) {
        if (book != null) {
            return bookRepo.save(book);
        }
        return new Book();
    }

   /* @GetMapping("/book/all")
    public List<Book> getAll() {
        return List.newArrayList(bookRepo.findAll());
    }*/
}
