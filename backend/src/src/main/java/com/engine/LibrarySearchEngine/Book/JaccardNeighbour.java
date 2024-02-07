package com.engine.LibrarySearchEngine.Book;

import jakarta.persistence.*;

/*************************************************************************************************/
/*************************************************************************************************/
/*************************************************************************************************/
@Entity
@Table(name = "book_neighbour")
public class JaccardNeighbour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Start book */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book1;

    /** Neighbour book */
    @ManyToOne
    @JoinColumn(name = "neighbour_id")
    private Book book2;

    private Double jaccardSimilarity;

    /*************************************** Constructors *********************************************************/

    public JaccardNeighbour() {
    }

    public JaccardNeighbour(Book book, Double jaccardSimilarity, Book book2) {
        this.book1 = book;
        this.jaccardSimilarity = jaccardSimilarity;
        this.book2 = book2;
    }

    /*************************************** Getters and Setters  ***************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook1() {
        return book1;
    }

    public void setBook1(Book book) {
        this.book1 = book;
    }

    public Book getBook2() {
        return book2;
    }

    public void setBook2(Book book) {
        this.book2 = book;
    }

    public Double getJaccardSimilarity() {
        return jaccardSimilarity;
    }

    public void setJaccardSimilarity(Double jaccardSimilarity) {
        this.jaccardSimilarity = jaccardSimilarity;
    }


    public void afficherElements() {
        System.out.println("Book: " + book1);
        System.out.println("Book neighbour: " + book2);
        System.out.println("Jaccard Similarity: " + jaccardSimilarity);
    }
}
/*************************************************************************************************/
/*************************************************************************************************/
/*************************************************************************************************/
