package com.colin.sprintbootassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.colin.sprintbootassessment.model.Book;
import com.colin.sprintbootassessment.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) // disable security to perform unit testing
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_ENDPOINT = "/api/books";

    private Book book1, book2;
    private List<Book> bookList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // Delete all records in the database before starting
        bookRepository.deleteAll();

        // arrange
        book1 = Book.builder()
                .title("Book 1")
                .author("Author 1")
                .build();

        book2 = Book.builder()
                .title("Book 2")
                .author("Author 2")
                .build();

        bookList.add(book1);
        bookList.add(book2);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void allBooks() throws Exception{

        // arrange
        bookRepository.saveAll(bookList);

        // act
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT));

        // assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(bookList.size())));

    }

    @Test
    void getBookById() throws Exception{

        // arrange
        bookRepository.save(book1);

        // act
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT.concat("/{id}"), book1.getId()));

        // assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));

    }

    @Test
    void createBook() throws Exception{

        // arrange
        String requestBody = objectMapper.writeValueAsString(book1);

        // act
        ResultActions resultActions = mockMvc.perform(post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // assert
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));

    }

    @Test
    void updateBook() throws Exception{

        // arrange
        bookRepository.save(book1);

        Book updatedBook1 = bookRepository.findById(book1.getId()).get();

        updatedBook1.setAuthor("updated author 1");
        updatedBook1.setTitle("updated book 1");

        String requestBody = objectMapper.writeValueAsString(updatedBook1);

        // act
        ResultActions resultActions = mockMvc.perform(put(API_ENDPOINT.concat("/{id}"), updatedBook1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updatedBook1.getTitle()))
                .andExpect(jsonPath("$.author").value(updatedBook1.getAuthor()));

    }

    @Test
    void deleteBook() throws Exception{

        // arrange
        bookRepository.save(book1);

        Book deleteBook1 = bookRepository.findById(book1.getId()).get();

        String expectedResponse = String.format("%s deleted successfully", deleteBook1.getTitle());

        // act
        ResultActions resultActions = mockMvc.perform(delete(API_ENDPOINT.concat("/{id}"), deleteBook1.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
    }
}