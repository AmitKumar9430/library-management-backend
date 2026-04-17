package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // GET all books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET search by title
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String author,
                                   @RequestParam(required = false) String genre) {
        if (title != null) return bookRepository.findByTitleContainingIgnoreCase(title);
        if (author != null) return bookRepository.findByAuthorContainingIgnoreCase(author);
        if (genre != null) return bookRepository.findByGenreIgnoreCase(genre);
        return bookRepository.findAll();
    }

    // POST create
    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Book with this ISBN already exists"));
        }
        book.setAvailableQuantity(book.getQuantity());
        return ResponseEntity.ok(bookRepository.save(book));
    }

    // PUT update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                         @Valid @RequestBody Book updated) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updated.getTitle());
            book.setAuthor(updated.getAuthor());
            book.setIsbn(updated.getIsbn());
            book.setGenre(updated.getGenre());
            book.setDescription(updated.getDescription());
            book.setQuantity(updated.getQuantity());
            book.setAvailableQuantity(updated.getAvailableQuantity());
            return ResponseEntity.ok(bookRepository.save(book));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
