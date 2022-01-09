package it.unicam.cs.asdl2122.mp2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 * 
 * @author Luca Tesei (template)
 * Federico Maria Cruciani, federicomar.cruciani@studenti.unicam.it (implementazione)
 *
 * @param <E> il tipo degli elementi degli insiemi disgiunti
 */
@SuppressWarnings("ConstantConditions")
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;
    
    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         * 
         * @param item l'elemento conservato in questo nodo
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }
    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
        currentElements = new HashMap<>();
    }

    @Override
    public boolean isPresent(E e) {
        if (e == null)
            throw new NullPointerException("Impossibile cercare null");

        return currentElements.containsKey(e);
    }

    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */
    @Override
    public void makeSet(E e) {
        if (e == null)
            throw new NullPointerException("Impossibile creare un insieme con null");

        if (currentElements.get(e) != null)
            throw new IllegalArgumentException("L'elemento passato è già presente in un insieme");

        currentElements.put(e, new Node<>(e));
    }

    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */
    @Override
    public E findSet(E e) {
        if (e == null)
            throw new NullPointerException("Impossibile trovare il rappresentante di null");

        Node<E> node = currentElements.get(e);
        if (node == null)
            return null;

        // Se l'elemento passato non è già il rappresentante prosegue ricorsivamente verso l'alto
        if (!e.equals(node.parent.item)) {
            node.parent = currentElements.get(findSet(node.parent.item));
        }
        // Alla fine della ricorsione ogni nodo ha come padre il rappresentante
        return node.parent.item;
    }

    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */
    @Override
    public void union(E e1, E e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("Impossibile unire degli elementi nulli");

        E e1Rep = findSet(e1);
        E e2Rep = findSet(e2);
        if (e1Rep == null || e2Rep == null)
            throw new IllegalArgumentException("Almeno uno degli elementi non esiste in nessun insieme");

        link(e1Rep, e2Rep);
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
        Set<E> representatives = new HashSet<>();

        for (Node<E> node : currentElements.values()) {
            // I rappresentanti hanno nel campo "parent" un puntatore a loro stessi, quindi "==" è efficace
            if (node.parent == node)
                representatives.add(node.item);
        }

        return representatives;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        if (e == null)
            throw new NullPointerException("Nessun insieme può contenere null");

        E rep = findSet(e);
        if (rep == null)
            throw new IllegalArgumentException("L'elemento non esiste in nessun insieme");

        Set<E> elements = new HashSet<>();

        for (Node<E> node : currentElements.values()) {
            // Aggiunge solo gli elementi che hanno lo stesso rappresentante di e
            if (rep.equals(findSet(node.item)))
                elements.add(node.item);
        }

        return elements;
    }

    @Override
    public void clear() {
        currentElements.clear();
    }

    /**
     * Metodo di utilità per unire gli insiemi rappresentati dagli elementi passati.
     * Il rappresentante dell'insieme finale è quello dell'insieme di rango più alto, o quello
     * del secondo in caso di uguaglianza
     *
     * @param rep1 il rappresentante del primo insieme
     * @param rep2 il rappresentante del secondo insieme
     */
    private void link(E rep1, E rep2) {
        Node<E> node1 = currentElements.get(rep1);
        Node<E> node2 = currentElements.get(rep2);

        if (node1.rank > node2.rank) {
            node2.parent = node1;
        } else {
            node1.parent = node2;

            // Se il rango dei due rappresentanti è uguale va incrementato, perché l'altezza
            // dell'albero è aumentata e il limite superiore potrebbe non essere più corretto
            if (node1.rank == node2.rank)
                node2.rank++;
        }
    }
}
