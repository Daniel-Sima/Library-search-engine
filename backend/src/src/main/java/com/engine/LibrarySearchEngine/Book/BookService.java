package com.engine.LibrarySearchEngine.Book;

import com.engine.LibrarySearchEngine.Automates.Automata;
import com.engine.LibrarySearchEngine.Automates.RegEx;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/*************************************************************************************************/
/*************************************************************************************************/
/*************************************************************************************************/
@Service
public class BookService {

    private final BookRepository bookRepository;

    private BookDataRepository bookDataRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookDataRepository bookDataRepository) {
        this.bookRepository = bookRepository;
        this.bookDataRepository = bookDataRepository;
    }

    /*************************************************************************************************/
    /**
     * Get all the books
     *
     * @return the books
     */
    public List<BookDTO> getBooks() {
        System.out.println("Getting books...");

        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*************************************************************************************************/
    /**
     * Convert a book to a bookDTO
     *
     * @param book the book
     * @return the bookDTO
     */
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setLanguage(book.getLanguage());
        dto.setImagePath(book.getImagePath());
        dto.setTextPath(book.getTextPath());

        // Convertir les mots avec TF_IDF
        List<WordDTO> wordsDTO = book.getBookData().stream()
                .map(bookData -> new WordDTO(bookData.getWord(), bookData.get_tf_idf()))
                .collect(Collectors.toList());
        dto.setWords(wordsDTO);

        return dto;
    }

    /*************************************************************************************************/
    /**
     * Get the book by its id
     *
     * @param word the word to search
     * @return the book
     */
    public List<BookDTO> searchBooks(String word) {
        String decodedWord = URLDecoder.decode(word, StandardCharsets.UTF_8);
        System.out.println("decodedWord: " + decodedWord);
        System.out.println("word: " + word);

        boolean isRegEx = false;
        for (int i = 0; i < decodedWord.length(); i++) {
            char c = decodedWord.charAt(i);
            if (c == '|' || c == '+' || c == '(' || c == ')' || c == '*' || c == '.') {
                isRegEx = true;
                break;
            }
        }

        List<BookData> bookData = new ArrayList<>();
        if (isRegEx) {
            try {
                List<Book> booksRes = bookRepository.findAll();
                RegEx regEx = new RegEx(decodedWord);
                Automata resDFA = regEx.regEx_automate();
                for (Book book : booksRes) {
                    for (BookData data : book.getBookData()) {
                        int cptOcc = resDFA.search(data.getWord(), true);
                        if (cptOcc > 0) {
                            bookData.add(data);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String[] words = word.split(" ");
            bookData = bookDataRepository.findByWord(words[0]);

            if (words.length > 1) {
                for (int i = 1; i < words.length; i++) {
                    List<BookData> bookData2 = bookDataRepository.findByWord(words[i]);
                    for (BookData data : bookData2) {
                        if (!bookData.contains(data)) {
                            bookData.add(data);
                        }
                    }
                }
            }
        }

        /// Classement implicite par le score TF-IDF dans l'ordre décroissant
        bookData.sort(Comparator.comparingDouble(BookData::get_tf_idf).reversed());
        List<Book> books = new ArrayList<>();
        for (BookData data : bookData) {
            books.add(data.getBook());
        }
        System.out.println("books: " + books);

        List<BookDTO> sortedBooks = books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return sortedBooks;
    }

    /*************************************************************************************************/
    /**
     * Get the book and its neighbours based on the Jaccard similarity
     *
     * @param id              the id of the book
     * @param jaccardDistance the Jaccard distance
     * @return
     */
    public List<BookDTO> getBookAndBookNeighbours(Long id, Double jaccardDistance) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Book with id " + id + " does not exist"));
        System.out.println("book: " + book);

        List<JaccardNeighbour> jaccardNeighbours = book.getJaccardNeighbours();
        System.out.println("jaccardNeighbours: " + jaccardNeighbours);

        List<Book> books = new ArrayList<>();
        books.add(book);
        for (JaccardNeighbour neighbour : jaccardNeighbours) {
            if (neighbour.getJaccardSimilarity() > jaccardDistance) {
                books.add(neighbour.getBook2());
            }
        }

        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*************************************************************************************************/
    /******************************************** TESTS **********************************************/
    /*************************************************************************************************/
    public List<BookDTO> searchBooks_nbBooks(String word, int nbBooks) {
        double startTime = System.currentTimeMillis();
        String decodedWord = URLDecoder.decode(word, StandardCharsets.UTF_8);
        System.out.println("decodedWord: " + decodedWord);
        System.out.println("word: " + word);

        boolean isRegEx = false;
        for (int i = 0; i < decodedWord.length(); i++) {
            char c = decodedWord.charAt(i);
            if (c == '|' || c == '+' || c == '(' || c == ')' || c == '*' || c == '.') {
                isRegEx = true;
                break;
            }
        }

        HashMap<Book, Double> books = new HashMap<>();
        List<BookData> bookData = new ArrayList<>();
        if (isRegEx) {
            try {
                List<Book> booksRes = bookRepository.findAll();
                // Recuperation que des nbBooks premiers livres
                booksRes = booksRes.subList(0, nbBooks);

                RegEx regEx = new RegEx(decodedWord);
                Automata resDFA = regEx.regEx_automate();
                for (Book book : booksRes) {
                    for (BookData data : book.getBookData()) {
                        int cptOcc = resDFA.search(data.getWord(), true);
                        if (cptOcc > 0) {
                            bookData.add(data);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String[] words = word.split(" ");
            // Application de la recherche que sur les nbBooks premiers livres dans bookDataRepository
            bookData = bookDataRepository.findByWord_nbBooks(words[0], nbBooks);

            if (words.length > 1) {
                for (int i = 1; i < words.length; i++) {
                    List<BookData> bookData2 = bookDataRepository.findByWord(words[i]);
                    for (BookData data : bookData2) {
                        if (!bookData.contains(data)) {
                            bookData.add(data);
                        }
                    }
                }
            }
        }

        // Classement implicite par nombre d’occurrences du mot-clef dans le document
        Collections.sort(bookData, Comparator.comparingDouble(BookData::get_tf_idf).reversed());
        for (BookData data : bookData) {
            books.put(data.getBook(), data.get_tf_idf());
        }

        double endTime = System.currentTimeMillis();

        List<BookDTO> res = books.entrySet().stream()
                .map(entry -> {
                    BookDTO dto = convertToDTO(entry.getKey());
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("searchBooks_nbBooks: " + (endTime - startTime) + " ms");

        return res;
    }
}
/*************************************************************************************************/
/*************************************************************************************************/
/*************************************************************************************************/
