package com.engine.LibrarySearchEngine.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@RestController
@RequestMapping(path = "api")
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "books")
    public List<BookDTO> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping(path = "search/{word}")
    public List<BookDTO> searchBooks(@PathVariable("word") String word) {
        System.out.println("Getting book and its neighbours...");
        return bookService.searchBooks(word);
    }

    @GetMapping(path = "searchTest/{word}")
    public List<BookDTO> searchBooksTest(@PathVariable("word") String word) {
        System.out.println("Getting book and its neighbours...");
        return bookService.searchBooks_nbBooks(word, 1);
    }

    @GetMapping(path = "book/{id}")
    public List<BookDTO> getBookAndBookNeighbours(@PathVariable("id") Long id) {
        return bookService.getBookAndBookNeighbours(id, 0.2);
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
