package com.chris.cachedemo.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public Page<Book> getBooks(
          @RequestParam(name = "page", defaultValue = "0") int page,
          @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    return bookService.findAll(PageRequest.of(page, size));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book createBook(@RequestBody Book book) {
    return bookService.save(book);
  }

  @GetMapping("/{id}")
  public Book getBook(@PathVariable Long id) {
    return bookService.findById(id);
  }

  @PutMapping("/{id}")
  public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
    return bookService.update(id, book);
  }

  @DeleteMapping("/{id}")
  public void deleteBook(@PathVariable Long id) {
    bookService.deleteById(id);
  }

  @DeleteMapping("/all")
  public void deleteAllBooks() {
    bookService.deleteAll();
  }
}
