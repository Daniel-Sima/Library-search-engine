package com.engine.LibrarySearchEngine.Book;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@Repository
public interface BookDataRepository extends JpaRepository<BookData, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE BookData bd SET bd.tf_idf = :tfidf WHERE bd.id = :id")
    void updateTFIDFById(@Param("id") Long id, @Param("tfidf") Double tfidf);

    List<BookData> findByWord(String word);

    @Query("SELECT bd FROM BookData bd WHERE bd.word = :word AND bd.book.id <= :nbBooks")
    List<BookData> findByWord_nbBooks(String word, int nbBooks);
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
