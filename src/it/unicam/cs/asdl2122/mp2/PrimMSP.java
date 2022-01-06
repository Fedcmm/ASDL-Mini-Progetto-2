package it.unicam.cs.asdl2122.mp2;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

import java.util.ArrayList;
import java.util.List;

/**
 * Classe singoletto che implementa l'algoritmo di Prim per trovare un Minimum
 * Spanning Tree di un grafo non orientato, pesato e con pesi non negativi.
 * 
 * L'algoritmo richiede l'uso di una coda di min priorità tra i nodi che può
 * essere realizzata con una semplice ArrayList (non c'è bisogno di ottimizzare
 * le operazioni di inserimento, di estrazione del minimo, o di decremento della
 * priorità).
 * 
 * Si possono usare i colori dei nodi per registrare la scoperta e la visita
 * effettuata dei nodi.
 * 
 * @author Luca Tesei (template)
 * Federico Maria Cruciani, federicomar.cruciani@studenti.unicam.it (implementazione)
 * 
 * @param <L> tipo delle etichette dei nodi del grafo
 */
@SuppressWarnings("ConstantConditions")
public class PrimMSP<L> {

    /*
     * In particolare: si deve usare una coda con priorità che può semplicemente
     * essere realizzata con una List<GraphNode<L>> e si deve mantenere un
     * insieme dei nodi già visitati
     */
    List<GraphNode<L>> queue;

    /**
     * Crea un nuovo algoritmo e inizializza la coda di priorità con una coda
     * vuota.
     */
    public PrimMSP() {
        queue = new ArrayList<>();
    }

    /**
     * Utilizza l'algoritmo goloso di Prim per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. Dopo l'esecuzione del metodo nei nodi del grafo il campo
     * previous deve contenere un puntatore a un nodo in accordo all'albero di
     * copertura minimo calcolato, la cui radice è il nodo sorgente passato.
     * 
     * @param g un grafo non orientato, pesato, con pesi non negativi
     * @param s il nodo del grafo g sorgente, cioè da cui parte il calcolo
     *          dell'albero di copertura minimo. Tale nodo sarà la radice
     *          dell'albero di copertura trovato
     * 
     * @throws NullPointerException se il grafo g o il nodo sorgente s sono nulli
     * @throws IllegalArgumentException se il nodo sorgente s non esiste in g
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     * con pesi negativi
     */
    public void computeMSP(Graph<L> g, GraphNode<L> s) {
        if (g == null || s == null)
            throw new NullPointerException("Impossibile eseguire l'algoritmo con parametri nulli");
        if (g.isDirected())
            throw new IllegalArgumentException("Impossibile eseguire l'algoritmo su un grafo orientato");

        for (GraphEdge<L> edge : g.getEdges()) {
            if (!edge.hasWeight() || edge.getWeight() < 0)
                throw new IllegalArgumentException(
                        "Impossibile eseguire l'algoritmo su un grafo con pesi negativi o non esistenti");
        }

        GraphNode<L> root = g.getNode(s);
        if (root == null)
            throw new IllegalArgumentException("Il nodo specificato non esiste nel grafo");

        for (GraphNode<L> node : g.getNodes()) {
            node.setFloatingPointDistance(Double.POSITIVE_INFINITY);
            node.setPrevious(null);
        }
        root.setFloatingPointDistance(0);

        queue.addAll(g.getNodes());
        while (!queue.isEmpty()) {
            GraphNode<L> node = extractMinNode();
            node.setColor(GraphNode.COLOR_BLACK);

            for (GraphNode<L> graphNode : g.getAdjacentNodesOf(node)) {
                GraphEdge<L> edge = g.getEdge(node, graphNode);

                if (queue.contains(graphNode) &&
                        edge.getWeight() < graphNode.getFloatingPointDistance()) {
                    graphNode.setPrevious(node);
                    graphNode.setFloatingPointDistance(edge.getWeight());
                }
            }
        }
    }

    private GraphNode<L> extractMinNode() {
        GraphNode<L> min = queue.get(0);
        int minIndex = 0;

        for (int i = 1; i < queue.size(); i++) {
            GraphNode<L> node = queue.get(i);
            if (node.getFloatingPointDistance() < min.getFloatingPointDistance()) {
                min = node;
                minIndex = i;
            }
        }
        return queue.remove(minIndex);
    }
}
