package com.chris.cachedemo.book;

public interface BookRepository extends org.springframework.data.jpa.repository.JpaRepository<com.chris.cachedemo.book.Book, java.lang.Long> ,org.springframework.data.jpa.repository.JpaSpecificationExecutor<com.chris.cachedemo.book.Book> {
  }