package com.engine.LibrarySearchEngine.Book;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
