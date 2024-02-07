package com.engine.LibrarySearchEngine.Automates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
/**
 * Classe gerant les RegEx.
 */
public class RegEx {
    // MACROS
    static final int CONCAT = 0xC04CA7;
    static final int ETOILE = 0xE7011E;
    static final int PLUS = 0xFFFFFF;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int PARENTHESEOUVRANT = 0x16641664;
    static final int PARENTHESEFERMANT = 0x51515151;
    static final int DOT = 0xD07;

    // REGEX
    private static String regEx;

    // TEXT
    private static String text;

    // CONSTRUCTORS
    public RegEx() {
    }

    public RegEx(String regex) {
        this.regEx = regex;
    }

    // TIME
    private static long startDFA, startDOT, startMDFA, startNFDA, startWT, startSearch, endSearch;
    private static long endDFA, endDOT, endMDFA, endNFDA, endWT, endAll, totalDOT;

    /***************************************************************************************/
    public Automata regEx_automate() throws IOException {
        try {
            RegExTree ret = parse();

            /*
             * Partie Arbre Syntaxique (en utilisant la structure de donnees Automate pour
             * stocker les transitions)
             */
            Automata resSyntaxTree = new Automata(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            resSyntaxTree = resSyntaxTree.toSyntaxTree(ret);


            /* Partie NDFA */
            Automata res = new Automata(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            res = res.toNDFA(ret);

            /* Partie DFA */
            Automata resDFA = new Automata(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            resDFA = resDFA.toDFA(res);

            /* Partie Min-DFA */
//            Automata resMDFA = new Automata(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//            resMDFA = resMDFA.toMinDFA(resDFA);

            return resDFA;
            /* Partie de recherche dans le texte */
//            int cptOcc = resMDFA.search(text, true);
//            System.out.println("\n  >> We found " + cptOcc + " occurences of pattern with Automates method.");

        } catch (Exception e) {
            System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\". | Exception: " + e);
        }

        return null;
    }

    /***************************************************************************************/
    public boolean find_with_regEx(String text) {
        try {
            Automata resDFA = regEx_automate();
            int cptOcc = resDFA.search(text, true);
            if (cptOcc > 0) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    /***************************************************************************************/
    /**
     * FROM REGEX TO SYNTAX TREE
     *
     * @return RegExTree parse
     * @throws Exception
     */
    static RegExTree parse() throws Exception {
        // BEGIN DEBUG: set conditionnal to true for debug example
        if (false)
            throw new Exception();
        RegExTree example = exampleAhoUllman();
        if (false)
            return example;
        // END DEBUG

        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        for (int i = 0; i < regEx.length(); i++)
            result.add(new RegExTree(charToRoot(regEx.charAt(i)), new ArrayList<RegExTree>()));

        return parse(result);
    }

    /***************************************************************************************/
    /**
     *
     * @param
     * @return
     */
    private static int charToRoot(char c) {
        if (c == '.')
            return DOT;
        if (c == '*')
            return ETOILE;
        if (c == '+')
            return PLUS;
        if (c == '|')
            return ALTERN;
        if (c == '(')
            return PARENTHESEOUVRANT;
        if (c == ')')
            return PARENTHESEFERMANT;
        return (int) c;
    }

    /***************************************************************************************/
    /**
     *
     * @param result
     * @return
     * @throws Exception
     */
    private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
        while (containParenthese(result))
            result = processParenthese(result);
        while (containEtoile(result))
            result = processEtoile(result);
        while (containPlus(result))
            result = processPlus(result);
        while (containConcat(result))
            result = processConcat(result);
        while (containAltern(result))
            result = processAltern(result);

        if (result.size() > 1)
            throw new Exception();

        return removeProtection(result.get(0));
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     */
    private static boolean containParenthese(ArrayList<RegExTree> trees) {
        for (RegExTree t : trees)
            if (t.root == PARENTHESEFERMANT || t.root == PARENTHESEOUVRANT)
                return true;
        return false;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     * @throws Exception
     */
    private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t : trees) {
            if (!found && t.root == PARENTHESEFERMANT) {
                boolean done = false;
                ArrayList<RegExTree> content = new ArrayList<RegExTree>();
                while (!done && !result.isEmpty())
                    if (result.get(result.size() - 1).root == PARENTHESEOUVRANT) {
                        done = true;
                        result.remove(result.size() - 1);
                    } else
                        content.add(0, result.remove(result.size() - 1));
                if (!done)
                    throw new Exception();
                found = true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(parse(content));
                result.add(new RegExTree(PROTECTION, subTrees));
            } else {
                result.add(t);
            }
        }
        if (!found)
            throw new Exception();
        return result;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     */
    private static boolean containEtoile(ArrayList<RegExTree> trees) {
        for (RegExTree t : trees)
            if (t.root == ETOILE && t.subTrees.isEmpty())
                return true;
        return false;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     */
    private static boolean containPlus(ArrayList<RegExTree> trees) {
        for (RegExTree t : trees)
            if (t.root == PLUS && t.subTrees.isEmpty())
                return true;
        return false;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     * @throws Exception
     */
    private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t : trees) {
            if (!found && t.root == ETOILE && t.subTrees.isEmpty()) {
                if (result.isEmpty())
                    throw new Exception();
                found = true;
                RegExTree last = result.remove(result.size() - 1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                result.add(new RegExTree(ETOILE, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     * @throws Exception
     */
    private static ArrayList<RegExTree> processPlus(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t : trees) {
            if (!found && t.root == PLUS && t.subTrees.isEmpty()) {
                if (result.isEmpty())
                    throw new Exception();
                found = true;
                RegExTree last = result.remove(result.size() - 1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                result.add(new RegExTree(PLUS, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     */
    private static boolean containConcat(ArrayList<RegExTree> trees) {
        boolean firstFound = false;
        for (RegExTree t : trees) {
            if (!firstFound && t.root != ALTERN) {
                firstFound = true;
                continue;
            }
            if (firstFound)
                if (t.root != ALTERN)
                    return true;
                else
                    firstFound = false;
        }
        return false;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     * @throws Exception
     */
    private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        boolean firstFound = false;
        for (RegExTree t : trees) {
            if (!found && !firstFound && t.root != ALTERN) {
                firstFound = true;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root == ALTERN) {
                firstFound = false;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root != ALTERN) {
                found = true;
                RegExTree last = result.remove(result.size() - 1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                subTrees.add(t);
                result.add(new RegExTree(CONCAT, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     */
    private static boolean containAltern(ArrayList<RegExTree> trees) {
        for (RegExTree t : trees)
            if (t.root == ALTERN && t.subTrees.isEmpty())
                return true;
        return false;
    }

    /***************************************************************************************/
    /**
     *
     * @param trees
     * @return
     * @throws Exception
     */
    private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        RegExTree gauche = null;
        boolean done = false;
        for (RegExTree t : trees) {
            if (!found && t.root == ALTERN && t.subTrees.isEmpty()) {
                if (result.isEmpty())
                    throw new Exception();
                found = true;
                gauche = result.remove(result.size() - 1);
                continue;
            }
            if (found && !done) {
                if (gauche == null)
                    throw new Exception();
                done = true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(gauche);
                subTrees.add(t);
                result.add(new RegExTree(ALTERN, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    /***************************************************************************************/
    /**
     *
     * @param tree
     * @return
     * @throws Exception
     */
    private static RegExTree removeProtection(RegExTree tree) throws Exception {
        if (tree.root == PROTECTION && tree.subTrees.size() != 1)
            throw new Exception();
        if (tree.subTrees.isEmpty())
            return tree;
        if (tree.root == PROTECTION)
            return removeProtection(tree.subTrees.get(0));

        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        for (RegExTree t : tree.subTrees)
            subTrees.add(removeProtection(t));
        return new RegExTree(tree.root, subTrees);
    }

    /***************************************************************************************/
    /**
     * Read file
     *
     * @param path    of the file
     * @param pattern RegEx
     * @return TODO
     */
    public static int processKMP(String path, String pattern, boolean print) {
        File directory = new File(path);
        String strLine = "";
        int cpt = 0;
        if (print) {
            System.out.println("  >> Reading: " + directory.getAbsolutePath() + "\n");
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((strLine = br.readLine()) != null) {
                int[] lps = computeLPSArray(regEx);
                int index = search(strLine, pattern, lps);
                if (index != -1) {
                    cpt++;
                    if (print) {
                        System.out.println("  >> Found: " + strLine);
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        }
        return cpt;
    }

    /***************************************************************************************/
    /**
     * TODO Compute the LPS (Longest Proper Prefix which is also Suffix) array
     *
     * @param pattern
     * @return
     */
    private static int[] computeLPSArray(String pattern) {
        int length = pattern.length();
        int[] lps = new int[length];
        int j = 0;
        for (int i = 1; i < length;) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                lps[i] = j + 1;
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /***************************************************************************************/
    /**
     * TODO Search for the pattern in the given text using KMP algorithm
     *
     * @param text
     * @param pattern
     * @param lps
     * @return
     */
    private static int search(String text, String pattern, int[] lps) {
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    // Pattern found, return the starting index of the occurrence
                    return i - j;
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        // Pattern not found in the text
        return -1;
    }

    /***************************************************************************************/
    /**
     * EXAMPLE --> RegEx from Aho-Ullman book Chap.10 Example 10.25
     *
     * @return
     */
    private static RegExTree exampleAhoUllman() {
        RegExTree a = new RegExTree((int) 'a', new ArrayList<RegExTree>());
        RegExTree b = new RegExTree((int) 'b', new ArrayList<RegExTree>());
        RegExTree c = new RegExTree((int) 'c', new ArrayList<RegExTree>());
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(c);
        RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(b);
        subTrees.add(cEtoile);
        RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(a);
        subTrees.add(dotBCEtoile);
        return new RegExTree(ALTERN, subTrees);
    }
}

/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
/**
 * UTILITARY CLASS
 */
class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;

    /***************************************************************************************/
    /**
     * Constructor
     *
     * @param root
     * @param subTrees
     */
    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    /***************************************************************************************/
    /**
     * FROM TREE TO PARENTHESIS
     */
    public String toString() {
        if (subTrees.isEmpty())
            return rootToString();
        String result = rootToString() + "(" + subTrees.get(0).toString();
        for (int i = 1; i < subTrees.size(); i++)
            result += "," + subTrees.get(i).toString();
        return result + ")";
    }

    /***************************************************************************************/
    /**
     *
     * @return
     */
    public String rootToString() {
        if (root == RegEx.CONCAT)
            return ".";
        if (root == RegEx.ETOILE)
            return "*";
        if (root == RegEx.PLUS)
            return "+";
        if (root == RegEx.ALTERN)
            return "|";
        if (root == RegEx.DOT)
            return ".";
        return Character.toString((char) root);
    }
}

/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
/***************************************************************************************/
