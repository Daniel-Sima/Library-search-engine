package com.engine.LibrarySearchEngine.Automates;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern; /**
 * Classe manipulant les automates avec ses etats et transitions
 */
public class Automata {
    /***************************************************************************************/
    /**
     * Classe gerant les etats de debut/fin et toutes les transitions sous forme
     * ['startState' -- 'transitionSymbol' --> 'endState']
     */
    class Transition {
        private String startState;
        private String transitionSymbol;
        private String endState;

        /***************************************************************************************/
        /**
         * Constructor
         *
         * @param startState       Etat de debut de la transition
         * @param transitionSymbol Symbole de la transition
         * @param endState         Etat de fin de la transition
         */
        public Transition(String startState, String transitionSymbol, String endState) {
            this.startState = startState;
            this.transitionSymbol = transitionSymbol;
            this.endState = endState;
        }

        /***************************************************************************************/
        /**
         * Retourne l'etat de debut de la transition
         *
         * @return Chaine de caracteres de l'etat de debut
         */
        public String getStartState() {
            return this.startState;
        }

        /***************************************************************************************/
        /**
         * Retourne le symbole de la transition
         *
         * @return Chaine de caracteres du symbole
         */
        public String getTransitionSymbol() {
            return this.transitionSymbol;
        }

        /***************************************************************************************/
        /**
         * Retourne l'etat de fin de la transition
         *
         * @return Chaine de caracteres de l'etat de fin
         */
        public String getEndState() {
            return this.endState;
        }

        /***************************************************************************************/
        /**
         * Changement du toString() Objet pour un meilleur affichage
         */
        @Override
        public String toString() {
            return "[" + getStartState() + " -- " + getTransitionSymbol() + " -- >" + getEndState() + "]";
        }

        /***************************************************************************************/
        /**
         * Changement du equals() Objet pour verifier les doublons selon notre
         * implementation
         */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Transition nouvelle = (Transition) o;
            return Objects.equals(startState, nouvelle.getStartState())
                    && Objects.equals(endState, nouvelle.getEndState())
                    && Objects.equals(transitionSymbol, nouvelle.getTransitionSymbol());
        }
    }

    /***************************************************************************************/
    /**
     * Classe permettant de retourner deux valeurs de type different (K, V)
     */
    class Pair<K, V> {
        private K key;
        private V value;

        /***************************************************************************************/
        /**
         * Constructor
         *
         * @param key
         * @param value
         */
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /***************************************************************************************/
        /**
         * Retourne la cle
         *
         * @return cle du Pair
         */
        public K getKey() {
            return key;
        }

        /***************************************************************************************/
        /**
         * Retourne valeur
         *
         * @return valeur du Pair
         */
        public V getValue() {
            return value;
        }

        /***************************************************************************************/
        /**
         * Modifier la cle
         *
         * @param key
         */
        public void setKey(K key) {
            this.key = key;
        }

        /***************************************************************************************/
        /**
         * Modifier la valeur
         *
         * @param value
         */
        public void setValue(V value) {
            this.value = value;
        }
    }

    // ArrayList pusique necessaire pour stocker plusieurs lors du parcours et pour
    // la Min-DFA
    protected ArrayList<String> initialStates;
    protected ArrayList<String> finalStates;
    protected ArrayList<Transition> transitions;
    // Poour garanir que tous les etats ont un numero different (necessaire pour
    // generer le '.dot')
    protected Integer numberStates = 0;

    // DEBUG
    private static boolean DEBUG = false;

    /**
     * Constructor
     *
     * @param initialState
     * @param finalStates
     * @param transitions
     */
    public Automata(ArrayList<String> initialState, ArrayList<String> finalStates, ArrayList<Transition> transitions) {
        this.initialStates = initialState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    /***************************************************************************************/
    /**
     * Charge un RegExTree dans la classe 'Automata' pour permettre l'affichage en
     * fichier '.dot' en utilisant les 'transitions'. Les listes 'initialState' et
     * 'finalStates' servent a sotcker le premier et dernier etat visite.
     *
     * @param tree RegExTree
     * @return
     */
    public Automata toSyntaxTree(RegExTree tree) {
        // Si plus de fils on fait la 'transition' avec son pere
        if (tree.subTrees.isEmpty()) {
            if (!initialStates.isEmpty()) {
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), null,
                        ("\"" + tree.rootToString() + "__" + (numberStates++)) + "\""));
            }
            return new Automata(initialStates, finalStates, transitions);
        }

        Automata resAutomata = null;
        for (int i = 0; i < tree.subTrees.size(); i++) {
            // Si on recommence un nouveau parcours de fils, alors on se connecte a son pere
            // s'il y en a un
            if (!(initialStates.isEmpty()) && i == 0) {
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), null,
                        "\"" + tree.rootToString() + "__" + (numberStates) + "\""));
                initialStates.add("\"" + tree.rootToString() + "__" + (numberStates++) + "\"");
            }
            // Cas premier pere
            if (initialStates.isEmpty()) {
                initialStates.add("\"" + tree.rootToString() + "__" + (numberStates++) + "\"");
            }

            // Appel recursif sur le second fils s'il existe
            resAutomata = toSyntaxTree(tree.subTrees.get(i));

            // Si on est a parcourir le second fils (i=1) et que la root est ETOIL ou PLUS
            // alors plus de fils a parcourir car operateurs unaires et on s'efface de la
            // liste
            // de stockage
            if ((!(initialStates.isEmpty()) && i == 1) || (tree.rootToString() == "*")
                    || (tree.rootToString() == "+")) {
                initialStates.remove(initialStates.size() - 1);
            }
        }

        return resAutomata;
    }

    /***************************************************************************************/
    /**
     * Trouve toutes les transitions entre un etat de depart (present dans
     * 'statesToSearch') et jusqu'a plus avoir d'etat. Change leur numeros pour le
     * fichier '.dot'.
     *
     * @param accTransitions Accumulateur de transitions
     * @param statesToSearch Etats qui restent à pacourir
     * @return Liste avec toutes les transitions depuis le premier etat de
     *         'statesToSearch'
     */
    public ArrayList<Transition> findTransitionsBetweenStates(ArrayList<Transition> accTransitions,
                                                              ArrayList<String> statesToSearch) {
        ArrayList<String> newStatesToSearch = new ArrayList<>();
        for (String etat : statesToSearch) {
            for (Transition e : transitions) {
                if (e.getStartState().equals(etat)) {
                    // Duplication du numero pour etre sur de ne pas avoir deux fois le meme
                    accTransitions.add(new Transition(e.getStartState() + e.getStartState(), e.getTransitionSymbol(),
                            e.getEndState() + e.getEndState()));
                    if (!newStatesToSearch.contains(e.getEndState())) {
                        newStatesToSearch.add(e.getEndState());
                    }
                }
            }
        }

        if (!newStatesToSearch.isEmpty()) {
            return findTransitionsBetweenStates(accTransitions, newStatesToSearch);
        }

        return accTransitions;
    }

    /***************************************************************************************/
    /**
     * Converit un 'RegExTree' en 'Automata' NFDA
     *
     * @param tree RegExTree a convertir
     * @return Automata convertit depuis 'tree'
     */
    public Automata toNDFA(RegExTree tree) {
        // Si plus de fils, transition entre le noeud courant et son pere
        if (tree.subTrees.isEmpty()) {
            initialStates.add("" + numberStates);
            finalStates.add("" + (numberStates + 1));
            transitions.add(new Transition("" + numberStates, tree.rootToString(), "" + (numberStates + 1)));
            numberStates += 2;

            return new Automata(initialStates, finalStates, transitions);
        }

        Automata resAutomata = null;
        for (int i = 0; i < tree.subTrees.size(); i++) {
            resAutomata = toNDFA(tree.subTrees.get(i));

            // Application de la regle de "closure"
            if (tree.rootToString() == "*") {
                // R1
                String lastInitialEtat = initialStates.remove(initialStates.size() - 1);
                String lastFinalEtat = finalStates.remove(finalStates.size() - 1);
                transitions.add(new Transition(lastFinalEtat, "ε", lastInitialEtat));
                initialStates.add("" + numberStates);
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), "ε", lastInitialEtat));
                finalStates.add("" + (numberStates + 1));
                transitions.add(new Transition(lastFinalEtat, "ε", finalStates.get(finalStates.size() - 1)));
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), "ε",
                        finalStates.get(finalStates.size() - 1)));
                numberStates += 2;
            }
            // Application de la regle de "concatenation", applicable que si on est le
            // second fils
            else if ((tree.rootToString() == ".") && (i != 0)) {
                // R2
                String lastInitialEtat = initialStates.remove(initialStates.size() - 1);
                String lastFinalEtat = finalStates.remove(finalStates.size() - 2);
                transitions.add(new Transition(lastFinalEtat, "ε", lastInitialEtat));
            }
            // Application de la regle de "union", applicable que si on est le second fils
            else if ((tree.rootToString() == "|") && (i != 0)) {
                // R2
                String lastInitialEtatR2 = initialStates.remove(initialStates.size() - 1);
                String lastFinalEtatR2 = finalStates.remove(finalStates.size() - 1);
                // R1
                String lastInitialEtatR1 = initialStates.remove(initialStates.size() - 1);
                String lastFinalEtatR1 = finalStates.remove(finalStates.size() - 1);

                initialStates.add("" + numberStates);
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), "ε", lastInitialEtatR1));
                transitions.add(new Transition(initialStates.get(initialStates.size() - 1), "ε", lastInitialEtatR2));
                finalStates.add("" + (numberStates + 1));
                transitions.add(new Transition(lastFinalEtatR1, "ε", finalStates.get(initialStates.size() - 1)));
                transitions.add(new Transition(lastFinalEtatR2, "ε", finalStates.get(initialStates.size() - 1)));
                numberStates += 2;
            }
            // Application de la regle de "PLUS"
            else if ((tree.rootToString() == "+")) {
                if (DEBUG) {
                    System.out.println();
                    printTransitions();
                }
                String lastInitialEtat = initialStates.get(initialStates.size() - 1);
                String lastFinalEtat = finalStates.get(finalStates.size() - 1);
                if (DEBUG) {
                    System.out.println("lastInitialEtat: " + lastInitialEtat);
                    System.out.println("lastFinalEtat: " + lastFinalEtat);
                }

                ArrayList<Transition> plusTransitons = new ArrayList<>();
                ArrayList<String> statesToSearch = new ArrayList<>();
                statesToSearch.add(lastInitialEtat);
                // Recopie de l'automate de l'argument du PLUS ([argument]+)
                plusTransitons = findTransitionsBetweenStates(plusTransitons, statesToSearch);

                transitions.addAll(plusTransitons);
                // Changement des etats finaux et inital
                String newInitialEtat = plusTransitons.get(0).getStartState();
                String newFinalEtat = plusTransitons.get(plusTransitons.size() - 1).getEndState();

                transitions.add(new Transition(lastFinalEtat, "ε", newInitialEtat));
                transitions.add(new Transition(newFinalEtat, "ε", newInitialEtat));

                // Etat ultime de l'automate avec PLUS
                String newFinalFinalEtat = "" + numberStates;
                numberStates++;

                finalStates.remove(finalStates.size() - 1);
                finalStates.add(newFinalFinalEtat);
                transitions.add(new Transition(lastFinalEtat, "ε", newFinalFinalEtat));
                transitions.add(new Transition(newFinalEtat, "ε", newFinalFinalEtat));

                if (DEBUG) {
                    System.out.println();
                    System.out.println("--------> finalStates: " + finalStates);
                    System.out.println("--------> initialStates: " + initialStates);
                }
            }
            // else {
            // }
        }
        return resAutomata;
    }

    /**
     * Getting epsilon close inital states
     *
     * @return ArrayList with initals states
     */
    /***************************************************************************************/
    /**
     * Obtenir les etats epsilon pres.
     *
     * @param startStates Etats a partir des quels on cherche
     * @return Liste des etats proches a epsilon pres
     */
    private ArrayList<String> getInitalEpsilonStates(ArrayList<String> startStates) {
        ArrayList<String> res = new ArrayList<>();
        res.addAll(startStates);

        for (String s : startStates) {
            for (Transition e : this.getTransitions()) {
                if ((e.getStartState().equals(s)) && (e.getTransitionSymbol().equals("ε"))
                        && !(res.contains(e.getEndState()))) {
                    res.add(e.getEndState());
                }
            }
        }

        if (res.size() != startStates.size()) {
            res = this.getInitalEpsilonStates(res);
        }

        return res;
    }

    /***************************************************************************************/
    /**
     * Testing all 256 ASCII carcacters to find stats that are acceptable
     *
     * @param states Etats de l'automate
     * @return Map with ASCI caracter and state number
     */
    private HashMap<String, ArrayList<String>> getASCII_transitions(ArrayList<String> states) {
        HashMap<String, ArrayList<String>> res = new HashMap<>();

        for (int i = 0; i <= 255; i++) {
            char caractere = (char) i;
            for (Transition e : this.transitions) {
                for (String s : states) {
                    if (e.getStartState().equals(s)) {
                        if (("" + caractere).equals(e.getTransitionSymbol())) {
                            ArrayList<String> etatsASCII = new ArrayList<>();
                            etatsASCII.add(e.getEndState());
                            etatsASCII = this.getInitalEpsilonStates(etatsASCII);
                            res.put("" + caractere, etatsASCII);
                        }
                    }
                }
            }
        }

        return res;
    }

    /***************************************************************************************/
    /**
     * Trouver pour chaque ensemble d'etats les transitions et les etats d'arrivee.
     * Construire une nouvelle liste d'etats avec comme numero les indices du
     * tableau contenant les listes de hachage avec les transitions.
     *
     * @param states Etats a visiter (etats initiaux au debut)
     * @param tab    Liste avec toutes les transitions du nouveau etat de depart
     *               represente par le numero de l'indice de ce tableau
     * @param cpt    compteur permettant de ne plus realculer les etats deja trouves
     * @return Liste de tables de hachages avec les tranistion depuis l'indice des
     *         etats et une liste de ces nouveaux etats
     */
    private Pair<ArrayList<HashMap<String, ArrayList<String>>>, ArrayList<ArrayList<String>>> findStates_DFA(
            ArrayList<ArrayList<String>> states, ArrayList<HashMap<String, ArrayList<String>>> tab, int cpt) {
        ArrayList<HashMap<String, ArrayList<String>>> tableau = new ArrayList<>(tab);
        ArrayList<ArrayList<String>> newStates = new ArrayList<>(states);
        HashMap<String, ArrayList<String>> res = new HashMap<>();

        for (int i = 0; i < states.size(); i++) {
            if (i >= cpt) { // pour ne pas recalculer
                res = this.getASCII_transitions(states.get(i));
                if (DEBUG) {
                    System.out.println("--> Pour: " + states.get(i) + " res: " + res);
                }
                if ((!tableau.contains(res)) && (!newStates.containsAll(res.values()))) {
                    newStates.addAll(res.values());
                }
                tableau.add(res);
            }
        }

        if (DEBUG) {
            System.out.println("--> New states: " + newStates);
            System.out.println("--> Tableau: " + tableau);
        }

        if (states.size() != newStates.size()) {
            if (DEBUG) {
                System.err.println("\n");
            }
            return this.findStates_DFA(newStates, tableau, states.size());
        }

        Pair<ArrayList<HashMap<String, ArrayList<String>>>, ArrayList<ArrayList<String>>> resultat = new Pair<>(tableau,
                newStates);
        return resultat;
    }

    /***************************************************************************************/
    /**
     * Conversion du NDFA en DFA
     *
     * @param NDFA Automate non deterministe
     * @return Automate deterministe
     */
    public Automata toDFA(Automata NDFA) {
        if (DEBUG) {
            System.out.println("\n\n----------- DFA ----------- ");
            System.out.println("Inital state: " + NDFA.getInitialStates());
            System.out.println("Final states: " + NDFA.getFinalStates());
            NDFA.printTransitions();
            System.out.println();
        }

        ArrayList<String> startState = new ArrayList<>(NDFA.getInitialStates());
        startState = NDFA.getInitalEpsilonStates(startState);
        ArrayList<ArrayList<String>> states = new ArrayList<>();
        states.add(startState);

        if (DEBUG) {
            System.out.println("--> states: " + states);
            System.out.println("--> startState: " + startState);
        }

        // Tableau final de l'automate
        ArrayList<HashMap<String, ArrayList<String>>> tableau = new ArrayList<>();
        Pair<ArrayList<HashMap<String, ArrayList<String>>>, ArrayList<ArrayList<String>>> pairTableauStates = NDFA
                .findStates_DFA(states, tableau, 0);

        if (DEBUG) {
            System.out.println("\nFinal tableau: " + pairTableauStates.getKey());
            System.out.println("Final states: " + pairTableauStates.getValue());
            System.out.println();
        }

        Automata automataDFA = new Automata(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        automataDFA.addInitialState("0");
        for (int i = 0; i < pairTableauStates.getValue().size(); i++) {
            HashMap<String, ArrayList<String>> tabTransitions = pairTableauStates.getKey().get(i);

            // Parcours simultane de cle valeur par ensemble
            for (Map.Entry<String, ArrayList<String>> entry : tabTransitions.entrySet()) {
                automataDFA.addTransition(new Transition("" + i, entry.getKey(),
                        "" + pairTableauStates.getValue().indexOf(entry.getValue())));

                // Ajout des etats finaux si on en trouve dans le 'tableau'
                if (entry.getValue().contains(NDFA.getFinalStates().get(0))) {
                    if (!automataDFA.getFinalStates()
                            .contains("" + pairTableauStates.getValue().indexOf(entry.getValue()))) {
                        automataDFA.addFinalState("" + pairTableauStates.getValue().indexOf(entry.getValue()));
                    }
                }
            }
        }

        if (DEBUG) {
            System.out.print("Final DFA: ");
            automataDFA.printTransitions();
        }

        return automataDFA;
    }

    /***************************************************************************************/
    /**
     * Decoupe l'ensemble des etats jusqu'a ce que on ne peut plus decouper
     * parcequ'il y en a qu'un seul ou parceque les deux sont dans le meme ensemble.
     *
     * @param ensembleTotal  Ensembles a analyser (finaux et non finaux au debut)
     * @param tabTransitions Liste des tables de hachages des transitions par indice
     *                       de l'etat
     * @param symboles       Lettres de la RegEx
     * @param i              Indice de la la chaine 'symboles' (0 au debut)
     * @return Ensemble des ensembles differents
     */
    public Set<Set<String>> decoupage(Set<Set<String>> ensembleTotal, ArrayList<HashMap<String, String>> tabTransitions,
                                      String symboles, int i) {
        if (i == symboles.length()) {
            return ensembleTotal;
        }

        String symbole = "" + symboles.charAt(i);
        Set<Set<String>> ensembleTotalBis = new LinkedHashSet<>(ensembleTotal);

        for (Set<String> ens : ensembleTotal) {
            if (DEBUG)
                System.out.println("==> ens: " + ens + " i: " + i);
            // Si un ensemble est plus grand que 1 on essaye de voir si on peut
            // le separer en regardant leur transitions
            if (!(ens.size() == 1)) {
                int cpt = 0;
                Set<String> dedans = new LinkedHashSet<>();
                Set<String> pasDedans = new LinkedHashSet<>();
                for (HashMap<String, String> table : tabTransitions) {
                    if (!table.isEmpty() && ens.contains("" + cpt)) {
                        if (table.containsKey(symbole)) {
                            dedans.add("" + cpt);
                        } else {
                            pasDedans.add("" + cpt);
                        }
                    }
                    cpt++;
                }

                // Pour separer les etats qui n'ont pas de transitions
                // sortantes de ceux qui ont
                if (dedans.isEmpty()) {
                    ens.removeAll(pasDedans);
                    dedans = ens;
                }
                ensembleTotalBis.remove(ens);

                if (!dedans.isEmpty()) {
                    ensembleTotalBis.add(dedans);
                }
                if (!pasDedans.isEmpty()) {
                    ensembleTotalBis.add(pasDedans);
                }

                if (DEBUG) {
                    System.out.println("==> dedans: " + dedans);
                    System.out.println("==> pasDedans: " + pasDedans);
                }
            }
        }

        if (DEBUG) {
            System.out.println("==> ensembleTotalBis: " + ensembleTotalBis + " i: " + i + "\n");
        }

        return decoupage(ensembleTotalBis, tabTransitions, symboles, (i + 1));
    }

    /***************************************************************************************/
    /**
     * Minimisation de l'automate deterministe DFA.
     *
     * @param DFA Automate deterministe non minimise
     * @return Automate deterministe minimise
     */
    public Automata toMinDFA(Automata DFA) {
        ArrayList<HashMap<String, String>> tabTransitions = new ArrayList<>();

        if (DEBUG) {
            System.out.println("\n\n----------- Min-DFA -----------");
            DFA.printTransitions();
        }

        // Recuperation du tableau des transitions
        for (Transition e : DFA.getTransitions()) {
            // Si rien n'a ete ajoute encore
            if (tabTransitions.size() == Integer.parseInt(e.getStartState())) {
                HashMap<String, String> val = new HashMap<>();
                val.put(e.getTransitionSymbol(), e.getEndState());
                tabTransitions.add(Integer.parseInt(e.getStartState()), val);
            } else if (tabTransitions.size() < Integer.parseInt(e.getStartState())) {
                // On remplit les cases du tableau jusqu'a la case interesse par des elements
                // nuls
                for (int i = tabTransitions.size(); i <= Integer.parseInt(e.getStartState()); i++) {
                    HashMap<String, String> val = new HashMap<>();
                    tabTransitions.add(i, val);
                    if (i == Integer.parseInt(e.getStartState())) {
                        tabTransitions.get(Integer.parseInt(e.getStartState())).put(e.getTransitionSymbol(),
                                e.getEndState());
                    }
                }
            } else {
                tabTransitions.get(Integer.parseInt(e.getStartState())).put(e.getTransitionSymbol(), e.getEndState());
            }
        }

        if (DEBUG) {
            System.out.println("--> tabTransitions: " + tabTransitions);
        }

        // Recuperation etats finaux
        Set<String> ensembleFinaux = new LinkedHashSet<>();
        for (String finaux : DFA.finalStates) {
            ensembleFinaux.add(finaux);
        }

        // Recuperation etats non finaux
        Set<String> ensembleNonFinaux = new LinkedHashSet<>();
        for (int i = 0; i < tabTransitions.size(); i++) {
            if (!ensembleFinaux.contains("" + i)) {
                ensembleNonFinaux.add("" + i);
            }
        }

        if (DEBUG) {
            System.out.println("--> Set finaux: " + ensembleFinaux);
            System.out.println("--> Set non finaux: " + ensembleNonFinaux);
            System.out.println("--> Symboles: " + DFA.getSymbolesTransition());
        }

        // Separatio en etats distincts pour minimiser
        Set<Set<String>> ensembleTotal = new LinkedHashSet<>();
        ensembleTotal.add(ensembleFinaux);
        ensembleTotal.add(ensembleNonFinaux);
        ensembleTotal = decoupage(ensembleTotal, tabTransitions, DFA.getSymbolesTransition(), 0);
        if (DEBUG) {
            System.out.println("--> ensembleTotal: " + ensembleTotal);
            System.out.println("--> Transitions actuelles: " + transitions);
        }

        // Creation des nouvelles transitions pour l'automate minimise
        for (Set<String> ens : ensembleTotal) {
            Iterator<String> it = ens.iterator();
            while (it.hasNext()) {
                String indexString = it.next();
                int indexInt = Integer.parseInt(indexString);
                if (!(tabTransitions.size() <= indexInt)) {
                    for (Map.Entry<String, String> entry : tabTransitions.get(indexInt).entrySet()) {
                        Set<String> endSet = getSetFromSets(ensembleTotal, entry.getValue());
                        if ((ens.size() == 1) && (endSet.size() == 1)) {
                            if (!transitions.contains(new Transition(indexString, entry.getKey(), entry.getValue()))) {
                                transitions.add(new Transition(indexString, entry.getKey(), entry.getValue()));
                            }
                        } else if ((ens.size() > 1) && (endSet.size() == 1)) {
                            if (!transitions.contains(
                                    new Transition("\"" + ens.toString() + "\"", entry.getKey(), entry.getValue()))) {
                                transitions.add(
                                        new Transition("\"" + ens.toString() + "\"", entry.getKey(), entry.getValue()));
                            }
                        } else if ((ens.size() == 1) && (endSet.size() > 1)) {
                            if (!transitions.contains(
                                    new Transition(indexString, entry.getKey(), "\"" + endSet.toString() + "\""))) {
                                transitions.add(
                                        new Transition(indexString, entry.getKey(), "\"" + endSet.toString() + "\""));
                            }
                        } else {
                            if (!transitions.contains(new Transition("\"" + ens.toString() + "\"", entry.getKey(),
                                    "\"" + endSet.toString() + "\""))) {
                                transitions.add(new Transition("\"" + ens.toString() + "\"", entry.getKey(),
                                        "\"" + endSet.toString() + "\""));
                            }
                        }
                    }
                }
            }
        }

        if (DEBUG) {
            System.out.println("--> Transitions nouvelles: " + transitions);
        }

        // Recherche set de l'etat initial
        for (Set<String> ens : ensembleTotal) {
            if (ens.contains("0")) {
                List<String> liste = new ArrayList<>(ens);
                initialStates.addAll(liste);
            }
        }

        // Recherche sets des etats finaux
        for (String s : DFA.finalStates) {
            for (Set<String> ens : ensembleTotal) {
                if (ens.contains(s)) {
                    if (!finalStates.contains(s) && !finalStates.contains(ens.toString())) {
                        List<String> liste = new ArrayList<>();
                        if (ens.size() > 1) {
                            liste.add(ens.toString());
                        } else {
                            liste.add(s);
                        }
                        finalStates.addAll(liste);
                    }
                }
            }
        }

        if (DEBUG) {
            System.out.println("=====> initialStates: " + initialStates);
            System.out.println("=====> finalStates: " + finalStates);
            System.out.println("-------------------------------------------\n\n");
        }

        return this;
    }

    /***************************************************************************************/
    /**
     * Recuperation d'un set parmi un set de sets.
     *
     * @param setOfSets
     * @param val       Set recherche
     * @return Set recherche
     */
    public Set<String> getSetFromSets(Set<Set<String>> setOfSets, String val) {
        for (Set<String> sets : setOfSets) {
            if (sets.contains(val)) {
                return sets;
            }
        }

        return null;
    }

    /***************************************************************************************/
    /**
     * Recherche dans le text du pattern correspondant au RegEx
     *
     * @param texte
     * @param print
     * @return
     */
    public int search(String texte, boolean print) {
//        File directory = new File(path);
//        String strLine = "";
        int cpt = 0;
//        if (print) {
//            System.out.println("  >> Reading: " + directory.getAbsolutePath() + "\n");
//        }
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(directory.getAbsolutePath()));
//
//            while ((strLine = br.readLine()) != null) {
//                for (String init : this.initialStates) {
                    if (processAuto(this.initialStates, texte, false)) {
                        cpt++;
                        if (print) {
                            System.out.println("  >> Found: " + texte);
                            return cpt;
                        }
//                    }
                }
//            }
//            br.close();
//        } catch (FileNotFoundException e) {
//            System.err.println("File not found");
//        } catch (IOException e) {
//            System.err.println("Unable to read the file.");
//        }
        return cpt;
    }

    public static ArrayList<Integer> convertirEnEntiers(ArrayList<String> listeDeChaines) {
        ArrayList<Integer> listeDentiers = new ArrayList<>();

        for (String chaine : listeDeChaines) {
            try {
                // Convertir la chaîne en entier et ajouter à la nouvelle liste
                int entier = Integer.parseInt(chaine);
                listeDentiers.add(entier);
            } catch (NumberFormatException e) {
                // Gérer les cas où la conversion échoue
                System.out.println("Impossible de convertir la chaîne en entier : " + chaine);
            }
        }

        return listeDentiers;
    }

    public static ArrayList<String> splitString(String input) {
        // Utilise une expression régulière pour extraire les chiffres
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        // Initialise une ArrayList pour stocker les chiffres extraits
        ArrayList<String> resultList = new ArrayList<>();

        // Ajoute chaque chiffre à l'ArrayList
        while (matcher.find()) {
            resultList.add(matcher.group());
        }

        // Retourne l'ArrayList résultante
        return resultList;
    }

    /***************************************************************************************/
    /**
     * On process recursivement l'automate sur une ligne de text
     *
     * @param state
     * @param strLine
     * @param follow
     * @return
     */
    protected boolean processAuto(ArrayList<String> state, String strLine, boolean follow) {
        ArrayList<Integer> intFinals = convertirEnEntiers(this.finalStates);

        ArrayList<Integer> intState = convertirEnEntiers(state);

        if (intState.size() > 1) {
            for (int i : intState){
                if (intFinals.contains(i)){
                    return true;
                }
            }
        } else if (intFinals.contains(intState.get(0))) {
            return true;
        }

        ArrayList<Transition> currTransitions = new ArrayList<>();

        for (Transition t : this.transitions) {
            ArrayList<String> t_array = splitString(t.startState);
            ArrayList<Integer> intTStartState = convertirEnEntiers(t_array);


            for (int i : intState){
                if (intTStartState.contains(i) && !currTransitions.contains(t)) {
                    currTransitions.add(t);
                }
            }
        }

        if (strLine.isEmpty()) {
            return false;
        }

        for (Transition t : currTransitions) {
            if (follow) {
                if (t.transitionSymbol.equals("" + strLine.charAt(0))) {
                    return processAuto(splitString(t.endState), strLine.substring(1, strLine.length()), true);

                }
            } else {
                for (int i = 0; i < strLine.length(); i++) {
                    if (t.transitionSymbol.equals("" + strLine.charAt(i))) {
                        return processAuto(splitString(t.endState), strLine.substring(i + 1, strLine.length()), true);
                    }
                }
            }
        }

        if (!strLine.isEmpty()) {
            return processAuto(splitString(this.getInitialStates().get(0)), strLine.substring(1, strLine.length()), false);
        }

        return false;
    }

    /***************************************************************************************/
    /**
     * Recuperer les symboles des transitions dans pour faire une String depuis la
     * DFA, donc plus d'epsilon.
     *
     * @return
     */
    public String getSymbolesTransition() {
        String res = "";
        for (Transition e : this.getTransitions()) {
            if (!res.contains(e.getTransitionSymbol())) {
                res += e.getTransitionSymbol();
            }
        }

        return res;
    }

    /***************************************************************************************/
    /**
     * Recuperer les etats initiaux (initial si DFA/Min-DFA)
     *
     * @return Liste des etats (de l'etat) initiaux
     */
    public ArrayList<String> getInitialStates() {
        return this.initialStates;
    }

    /***************************************************************************************/
    /**
     * Recuperer les etats finaux
     *
     * @return Liste des etats finaux
     */
    public ArrayList<String> getFinalStates() {
        return this.finalStates;
    }

    /***************************************************************************************/
    /**
     * Recuperer toutes les transitions de l'automate.
     *
     * @return Liste des transitions
     */
    public ArrayList<Transition> getTransitions() {
        return this.transitions;
    }

    /***************************************************************************************/
    /**
     * Ajouter un etat initial dans la liste des etats initiaux de l'automate.
     *
     * @param initial L'etat a ajouter
     */
    public void addInitialState(String initial) {
        this.initialStates.add(initial); // ArrayList but only one into for DFA/Min-DFA
    }

    /***************************************************************************************/
    /**
     * Ajouter un etat final dans la liste des etats finaux de l'automate.
     *
     * @param finalS L'etat a ajouter
     */
    public void addFinalState(String finalS) {
        this.finalStates.add(finalS);
    }

    /***************************************************************************************/
    /**
     * Ajouter une liste d'etat finaux dans la liste de l'automate.
     *
     * @param listFinalS La liste a ajouter
     */
    public void addFinalStates(ArrayList<String> listFinalS) {
        this.finalStates.addAll(listFinalS);
    }

    /***************************************************************************************/
    /**
     * Ajouter une transition a la liste de transitions de l'automate.
     *
     * @param e Tranition a ajouter
     */
    public void addTransition(Transition e) {
        this.transitions.add(e);
    }

    /***************************************************************************************/
    /**
     * Ajouter une liste de transitions a la liste de transitions de l'automate.
     *
     * @param listE Liste a ajouter
     */
    public void addTransitions(ArrayList<Transition> listE) {
        this.transitions.addAll(listE);
    }

    /***************************************************************************************/
    /**
     * Affichage de toutes les transitions de l'automate.
     */
    public void printTransitions() {
        System.out.println("Transitions: ");
        for (Transition e : this.transitions) {
            System.out.println(e.toString());
        }
    }
}
