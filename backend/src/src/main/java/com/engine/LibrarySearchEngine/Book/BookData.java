package com.engine.LibrarySearchEngine.Book;

import jakarta.persistence.*;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@Entity
@Table(name = "book_data")
public class BookData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private String word;
    private Double tf_idf;
    private Integer nb_occurences;

    /*************************************** Constructors *********************************************************/

    public BookData() {
    }

    public BookData(Book book, String word, Double tf_idf, Integer nb_occurences) {
        this.book = book;
        this.word = word;
        this.tf_idf = tf_idf;
        this.nb_occurences = nb_occurences;
    }

    /*************************************** Getters and Setters  ***************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double get_tf_idf() {
        return tf_idf;
    }

    public void set_tf_idf(Double tf) {
        this.tf_idf = tf;
    }

    public Integer getNb_occurences() {
        return nb_occurences;
    }

    public void setNb_occurences(Integer nb_occurences) {
        this.nb_occurences = nb_occurences;
    }

    @Override
    public String toString() {
        return "BookData{" +
                "id=" + id +
                ", book=" + book +
                ", word='" + word + '\'' +
                ", tf_idf=" + tf_idf +
                ", nb_occurences=" + nb_occurences +
                '}';
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
