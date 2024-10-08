package com.colin.sprintbootassessment.controller;

import com.colin.sprintbootassessment.exception.ResourceNotFoundException;
import com.colin.sprintbootassessment.model.Book;
import com.colin.sprintbootassessment.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Function to get all books in bookdb
    @GetMapping
    public ResponseEntity<Object> AllBooks() {

        List<Book> bookList = bookService.findAllBooks();

        if (bookList.isEmpty()) throw new ResourceNotFoundException();

        return new ResponseEntity<>(bookService.findAllBooks(), HttpStatus.OK);

    }

    // Function to get specified book by id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {

        Book book = bookService.findBookById(id).orElseThrow(()-> new ResourceNotFoundException());

        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    // Function to create a book
    @PostMapping
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {

        return new ResponseEntity<>(bookService.saveBook(book), HttpStatus.CREATED);

    }

    // Function to update a book by id
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {

        Book checkBook = bookService.findBookById(id).map(_book ->{
            _book.setTitle(bookDetails.getTitle());
            _book.setAuthor(bookDetails.getAuthor());
            return bookService.saveBook(_book);
        }).orElseThrow(()-> new ResourceNotFoundException());

        return new ResponseEntity<>(checkBook, HttpStatus.OK);

    }

    // Function to delete book by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {

        Book checkBook = bookService.findBookById(id).map(_book ->{
            bookService.deleteBookById(_book.getId());
            return _book;
        }).orElseThrow(()-> new ResourceNotFoundException());

        String response = String.format("%s deleted successfully", checkBook.getTitle());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
