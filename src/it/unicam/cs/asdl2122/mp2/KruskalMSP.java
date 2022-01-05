package it.unicam.cs.asdl2122.mp2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 * 
 * @author Luca Tesei (template)
 * Federico Maria Cruciani, federicomar.cruciani@studenti.unicam.it (implementazione)
 * 
 * @param <L> tipo delle etichette dei nodi del grafo
 */
@SuppressWarnings({"ConstantConditions", "FieldMayBeFinal"})
public class KruskalMSP<L> {

    /**
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     * 
     * @param g un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     *
     * @throws NullPointerException se il grafo g è null
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     * con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        if (g == null)
            throw new NullPointerException("Impossibile eseguire l'algoritmo con un parametro nullo");
        if (g.isDirected())
            throw new IllegalArgumentException("Impossibile eseguire l'algoritmo su un grafo orientato");

        Set<GraphEdge<L>> edges = g.getEdges();
        for (GraphEdge<L> edge : edges) {
            if (!edge.hasWeight() || edge.getWeight() < 0)
                throw new IllegalArgumentException(
                        "Impossibile eseguire l'algoritmo su un grafo con pesi negativi o non esistenti");
        }

        Set<GraphEdge<L>> result = new HashSet<>();

        disjointSets.clear();
        for (GraphNode<L> node : g.getNodes()) {
            disjointSets.makeSet(node);
        }

        List<GraphEdge<L>> edgeList = new ArrayList<>(edges);
        sortEdges(edgeList, 0, edgeList.size() - 1);

        for (GraphEdge<L> edge : edgeList) {
            GraphNode<L> node1 = edge.getNode1();
            GraphNode<L> node2 = edge.getNode2();
            if (!disjointSets.findSet(node1).equals(disjointSets.findSet(node2))) {
                result.add(edge);
                disjointSets.union(node1, node2);
            }
        }
        
        return result;
    }

    private void sortEdges(List<GraphEdge<L>> edges, int low, int high) {
        if (low < high) {
            int partIndex = partition(edges, low, high);

            sortEdges(edges, low, partIndex - 1);
            sortEdges(edges, partIndex + 1, high);
        }
    }

    private int partition(List<GraphEdge<L>> edges, int low, int high) {
        GraphEdge<L> pivot = edges.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            GraphEdge<L> jEdge = edges.get(j);
            if (jEdge.getWeight() <= pivot.getWeight()) {
                i++;

                GraphEdge<L> iEdge = edges.set(i, jEdge);
                edges.set(j, iEdge);
            }
        }

        GraphEdge<L> iEdge = edges.set(i + 1, pivot);
        edges.set(high, iEdge);
        return i + 1;
    }
}
