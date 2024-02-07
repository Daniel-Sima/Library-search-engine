package com.engine.LibrarySearchEngine.Book;

import jakarta.persistence.*;
import java.util.List;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@Entity
@Table (name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "language")
    private String language;

    @Column(name = "imagePath")
    private String imagePath;

    @Column(name = "textPath")
    private String textPath;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookData> bookData;

    @OneToMany(mappedBy = "book1", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<JaccardNeighbour> jaccardNeighbours;


    /*************************************** Constructors *********************************************************/

    public Book() {
    }

    public Book(String title,
                String author,
                String language,
                List<BookData> bookData,
                String imagePath,
                List<JaccardNeighbour> jaccardNeighbours,
                String textPath) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.bookData = bookData;
        this.imagePath = imagePath;
        this.jaccardNeighbours = jaccardNeighbours;
        this.textPath = textPath;
    }

    /*************************************** Getters and Setters  ***************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<BookData> getBookData() {
        return bookData;
    }

    public void setBookData(List<BookData> bookData) {
        this.bookData = bookData;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<JaccardNeighbour> getJaccardNeighbours() {
        return jaccardNeighbours;
    }

    public void setJaccardNeighbours(List<JaccardNeighbour> jaccardNeighbours) {
        this.jaccardNeighbours = jaccardNeighbours;
    }

    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", language='" + language + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", textPath='" + textPath + '\'' +
//                ", bookData=" + bookData +
//                ", jaccardNeighbours=" + jaccardNeighbours +
                '}';
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
