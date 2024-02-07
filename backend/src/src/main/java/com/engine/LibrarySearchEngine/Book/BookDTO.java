package com.engine.LibrarySearchEngine.Book;

import java.util.List;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String language;

    private String imagePath;

    private String textPath;
    private List<WordDTO> words;  // Liste de mots avec leur TF

    /*************************************** Constructors *********************************************************/
    public BookDTO() {
        // Constructeur par défaut
    }

    public BookDTO(Long id, String title, String author, String language, String imagePath, List<WordDTO> words) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.language = language;
        this.imagePath = imagePath;
        this.words = words;
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

    public List<WordDTO> getWords() {
        return words;
    }

    public void setWords(List<WordDTO> words) {
        this.words = words;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
