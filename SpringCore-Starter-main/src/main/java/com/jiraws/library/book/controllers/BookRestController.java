package com.jiraws.library.book.controllers;

import com.jiraws.library.book.dto.BookDTO;
import com.jiraws.library.book.model.BookEntity;
import com.jiraws.library.book.persistence.BookRepository;
import com.jiraws.library.book.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookRestController {

    private final BookService bookService;
    private final BookRepository bookRepo;

    public BookRestController(BookService bookService, BookRepository bookRepo)
    {
        this.bookService = bookService;
        this.bookRepo = bookRepo;
    }

    // SOLID
    // S --> Single Responsibility
    // O --> Open Closed
    // L --> Substitution de Liskov
    // I --> Interface Segregation
    // D --> Dependency Inversion

    // GET --> Lecture
    // POST --> Création*
    // PUT --> Mise à jour
    // DELETE --> Suppression

    @GetMapping
    public String get(@RequestParam String bookName, @RequestParam Integer bookPages)
    {
//        log.info(bookName); //Déjà un String donc l'afficher direct.
//        log.info(String.valueOf(bookPages)); //Integer en String pour afficher le nombre de pages.
//
//        String rep = bookService.createBook(bookName, bookPages);
//
//        return(rep);
        return("OK GET");
    }

    @GetMapping("/liste")
    public List<BookEntity> get()
    {
        List<BookEntity> maListe = bookService.getAllBooks();
        return(maListe);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BOOK_READ')")
    public ResponseEntity<BookEntity> getBookById(@PathVariable Long id)
    {
        ResponseEntity<BookEntity> rep = bookService.getBookById(id);

        return(rep);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BOOK_CREATE')")
    public String post(@RequestBody BookDTO.PostInput input)
    {
        log.info(input.getBookName()); //Déjà un String donc l'afficher direct.
        log.info(String.valueOf(input.getBookPages())); //Integer en String pour afficher le nombre de pages.

        String rep = bookService.createBook(input.getBookName(), input.getBookPages());

        return(rep);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BOOK_UPDATE')")
    public String update(@PathVariable Long id, @RequestBody BookDTO.PostInput input)
    {
        BookEntity updatedBook = bookService.updateBook(id, input.getBookName(), input.getBookPages());

        return("OK UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('BOOK_DELETE')")
    public String delete(@PathVariable Long id)
    {
        bookService.deleteBook(id);

        return("DELETE OK");
    }
}