package com.engine.LibrarySearchEngine.utils;

import com.engine.LibrarySearchEngine.Book.Book;
import com.engine.LibrarySearchEngine.Book.BookData;
import com.engine.LibrarySearchEngine.Book.BookDataRepository;
import com.engine.LibrarySearchEngine.Book.BookRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
public class Utils {

    /************************************************************************************************************/
    /**
     * Cette methode permet de calculer la frequence d'apparition de chaque mot dans un livre
     * en suivant la methode TF (Term Frequency).
     *
     * @param bookNumber : le numero du livre a charger
     * @return un quadruplet contenant le titre, l'auteur, la langue et la frequence d'apparition de chaque mot
     */
    public static Quintuplet<String, String, String, HashMap<String, Double>, HashMap<String, Integer>> load_book_TF(
            int bookNumber,
            HashMap<String, Integer> motsDansLivres){
        try {
            Set<String> englishStopWords = load_stop_words("src/data/english_stopwords.txt");

            HashMap<String, Integer> motsOccurences = new HashMap<String, Integer>();

            int totalMots = 0; // qui ne sont pas des stop words
            String titre = "";
            String auteur = "";
            String langue = "";

            File file = new File("src/data/books/book"+bookNumber+".txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String ligne;
            while ((ligne = bufferedReader.readLine()) != null) {
                // Extraction du titre
                if (ligne.startsWith("Title:") && titre.isEmpty()) {
                    titre = ligne.substring("Title:".length()).trim();
                }

                // Extraction de l'auteur
                if (ligne.startsWith("Author:") && auteur.isEmpty()){
                    auteur = ligne.substring("Author:".length()).trim();
                }

                // Extraction de la langue
                if (ligne.startsWith("Language:") && langue.isEmpty()) {
                    langue = ligne.substring("Language:".length()).trim();
                }

                ligne = ligne.toLowerCase();
                String[] mots = ligne.split("\\s+");

                // Suppression des signes de ponctuation de chaque mot
                for (int i = 0; i < mots.length; i++) {
                    mots[i] = mots[i].replaceAll("[^a-zA-Z0-9]", "");
                }

                for (String mot : mots) {
                    // Charger les mots qui ne sont pas des stop words
                    // et qui ont une longueur superieure a 1
                    // (pour eliminer les singnes de ponctuation par exemple)
                    if (!englishStopWords.contains(mot) && mot.length() > 1){
                        // Si le mot est deja dans la map, on incremente son occurence sinon on l'ajoute
                        if (motsOccurences.containsKey(mot)) {
                            motsOccurences.put(mot, motsOccurences.get(mot) + 1);
                        } else {
                            motsOccurences.put(mot, 1);
                            if (motsDansLivres.containsKey(mot)){
                                motsDansLivres.put(mot, motsDansLivres.get(mot)+1);
                            } else {
                                motsDansLivres.put(mot, 1);
                            }
                        }
                        totalMots++;
                    }
                }
            }

            bufferedReader.close();
            fileReader.close();

            List<Map.Entry<String, Integer>> listeEntrees = new ArrayList<>(motsOccurences.entrySet());
            listeEntrees.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Correspond ici au nombre d'occurences de chaque mot
            LinkedHashMap<String, Integer> hashMapOccurences = new LinkedHashMap<>();
            // Correspond ici a la TF de chaque mot
            LinkedHashMap<String, Double> hashMapTriee = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : listeEntrees) {
                hashMapOccurences.put(entry.getKey(), entry.getValue());
                hashMapTriee.put(entry.getKey(), (double) entry.getValue());
            }

            for (Map.Entry<String, Double> entry : hashMapTriee.entrySet()) {
                double tf = (double) Math.round((entry.getValue()/totalMots)*10000.00)/10000.00;
                hashMapTriee.put(entry.getKey(), tf);
            }

            return new Quintuplet<>(titre, auteur, langue, hashMapTriee, hashMapOccurences);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /************************************************************************************************************/
    /**
     * Cette methode permet de charger les mots vides (stop words) depuis un ensemble.
     * @param cheminFichier : le chemin du fichier contenant les mots vides
     * @return un ensemble de mots vides (stop words)
     */
    private static Set<String> load_stop_words(String cheminFichier) {
        Set<String> stopWords = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                stopWords.add(ligne.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopWords;
    }

    /************************************************************************************************************/
    /**
     * Cette methode permet de calculer la similarite de Jaccard entre deux livres.
     * @param book1 : le premier livre
     * @param book2 : le deuxieme livre
     * @return la similarite de Jaccard entre les deux livres en double
     */
    public static double jaccard_similarity(Book book1, Book book2) {
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        for (BookData bookData : book1.getBookData()) {
            set1.add(bookData.getWord());
        }

        for (BookData bookData : book2.getBookData()) {
            set2.add(bookData.getWord());
        }

        int intersection = 0;
        for (String mot : set1) {
            if (set2.contains(mot)) {
                intersection++;
            }
        }

        int union = set1.size() + set2.size() - intersection;

        return (double) intersection / union;
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
