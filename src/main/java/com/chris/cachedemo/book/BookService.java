package com.chris.cachedemo.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  @Cacheable(cacheNames = "books", key = "#id")
  public Book findById(Long id) {
    log.info("findById: {}", id);
    doLongTask();

    return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
  }

  public Book save(Book book) {
    log.info("save: {}", book);
    doLongTask();

    book.setId(null);
    return bookRepository.save(book);
  }

  @CacheEvict(cacheNames = "books", key = "#result.id")
  public Book update(Long id, Book book) {
    log.info("update: {}", book);
    doLongTask();

    book.setId(id);
    return bookRepository.save(book);
  }

  @CacheEvict(cacheNames = "books", key = "#id")
  public void deleteById(Long id) {
    log.info("deleteById: {}", id);
    doLongTask();

    bookRepository.deleteById(id);
  }

  @CacheEvict(cacheNames = "books", allEntries = true)
  public void deleteAll() {
    log.info("deleteAll");

    doLongTask();
    bookRepository.deleteAll();
  }

  public Page<Book> findAll(Pageable request) {
    log.info("findAll: {}", request);
    doLongTask();

    return bookRepository.findAll(request);
  }

  private void doLongTask() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
