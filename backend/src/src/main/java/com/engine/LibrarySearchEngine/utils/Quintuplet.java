package com.engine.LibrarySearchEngine.utils;

/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
/** Classe generique pour representer un quintuplet d'elements */
public class Quintuplet<T1, T2, T3, T4, T5> {
    private T1 element1;
    private T2 element2;
    private T3 element3;
    private T4 element4;
    private T5 element5;

    /*********************************************** Constructor *****************************************************/

    public Quintuplet(T1 element1, T2 element2, T3 element3, T4 element4, T5 element5) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
        this.element4 = element4;
        this.element5 = element5;
    }

    /*********************************************** Getters *****************************************************/
    public T1 getElement1() {
        return element1;
    }

    public T2 getElement2() {
        return element2;
    }

    public T3 getElement3() {
        return element3;
    }

    public T4 getElement4() {
        return element4;
    }

    public T5 getElement5() {
        return element5;
    }

    public void afficherElements() {
        System.out.println("Element 1: " + element1);
        System.out.println("Element 2: " + element2);
        System.out.println("Element 3: " + element3);
        System.out.println("Element 4: " + element4);
        System.out.println("Element 5: " + element5);
    }
}
/************************************************************************************************************/
/************************************************************************************************************/
/************************************************************************************************************/
