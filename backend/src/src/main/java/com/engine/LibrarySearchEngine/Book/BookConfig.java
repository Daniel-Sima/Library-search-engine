package com.engine.LibrarySearchEngine.Book;

import com.engine.LibrarySearchEngine.utils.Quintuplet;
import com.engine.LibrarySearchEngine.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
@Configuration
@EnableTransactionManagement
public class BookConfig {
    private final BookService bookService;

    @Autowired
    public BookConfig(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Méthode qui permet de charger les livres dans la base de données si non chargées
     * @param repository            BookRepository
     * @param bookDataRepository    BookDataRepository
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner(BookRepository repository, BookDataRepository bookDataRepository){
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Loading books...");
                int bookNb = 1;
                HashMap<String, Integer> motsDansLivres = new HashMap<>();
                for (int i=1; i<=50; i++) {
                    // Tant que le livre n'existe pas on incrémente le numéro (ex: book1, book10, ...)
                    while (Files.notExists(Paths.get("src/data/books/book" + bookNb + ".txt"))) {
                        bookNb++;
                    }

                    Quintuplet<String, String, String, HashMap<String, Double>, HashMap<String, Integer>> book1 = Utils.load_book_TF(
                            bookNb,
                            motsDansLivres
                    );
                    String title = book1.getElement1();
                    String author = book1.getElement2();
                    String language = book1.getElement3();
                    // Format de l'URL pour l'image de couverture
                    String imagePath = "https://www.gutenberg.org/cache/epub/"+bookNb+"/pg"+bookNb+".cover.medium.jpg";
                    String textPath = "https://www.gutenberg.org/cache/epub/"+bookNb+"/pg"+bookNb+".txt";
                    HashMap<String, Double> motsTF = book1.getElement4();
                    HashMap<String, Integer> motsOccurences = book1.getElement5();

                    Book book = new Book(title, author, language, new ArrayList<>(), imagePath, new ArrayList<>(), textPath);
                    repository.save(book);

                    // On ajoute les mots du livre dans la table BookData
                    for (Map.Entry<String, Double> entry : motsTF.entrySet()) {
                        BookData bookData = new BookData();
                        bookData.setBook(book);
                        bookData.setWord(entry.getKey());
                        bookData.set_tf_idf(entry.getValue());
                        bookData.setNb_occurences(motsOccurences.get(entry.getKey()));
                        bookDataRepository.save(bookData);
                    }

                    System.out.println("Book " + bookNb + " loaded");
                    bookNb++;
                }

                System.out.println("All books loaded");

                System.out.println("Calculating TF_IDF for each word in each book");
                long nbLivres = repository.count();

                repository.findAll().forEach(book -> {
                    System.out.println("Book: "+ book.toString());
                    book.getBookData().forEach(bookData -> {
                        double idf =  Math.log10((double) nbLivres / motsDansLivres.get(bookData.getWord()));
                        double tf = bookData.get_tf_idf();
                        bookData.set_tf_idf(tf * idf);
                        bookDataRepository.updateTFIDFById(bookData.getId(), bookData.get_tf_idf());
                    });
                });


                System.out.println("TF_IDF calculated for each word in each book");

                System.out.println("Calculating Jaccard Similarity for each book");
                List<Book> books = repository.findAll();

                for (int i=0; i<books.size(); i++) {
                    Book book1 = books.get(i);
                    for (int j=i+1; j<books.size(); j++) {
                        Book book2 = books.get(j);
                        double jaccard = Utils.jaccard_similarity(book1, book2);
                        System.out.println("Jaccard Similarity between " + book1.getTitle() + " and " + book2.getTitle() + " is " + jaccard);
                        book1.getJaccardNeighbours().add(new JaccardNeighbour(book1, jaccard, book2));
                        book2.getJaccardNeighbours().add(new JaccardNeighbour(book2, jaccard, book1));
                        repository.save(book1);
                        repository.save(book2);
                    }
                }

                System.out.println("Jaccard Similarity calculated for each book");

            } else {
                System.out.println("Books already loaded");
            }
        };
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
