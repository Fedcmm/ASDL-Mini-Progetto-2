package it.unicam.cs.asdl2122.mp2;

import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@link ForestDisjointSets}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 * 
 * @author Luca Tesei (template)
 * Federico Maria Cruciani, federicomar.cruciani@studenti.unicam.it (implementazione)
 *
 * @param <L> il tipo delle etichette dei nodi del grafo
 */
@SuppressWarnings({"FieldMayBeFinal", "ConstantConditions"})
public class UndirectedGraphConnectedComponentsComputer<L> {

    /**
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     * 
     * @param g un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     *
     * @throws NullPointerException se il grafo passato è nullo
     * @throws IllegalArgumentException se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        if (g == null)
            throw new NullPointerException("Impossibile eseguire l'algoritmo su un grafo nullo");
        if (g.isDirected())
            throw new IllegalArgumentException("Impossibile eseguire l'algoritmo su un grafo orientato");

        Set<Set<GraphNode<L>>> result = new HashSet<>();

        // Crea un nuovo insieme singoletto per ogni nodo
        for (GraphNode<L> node : g.getNodes()) {
            f.makeSet(node);
        }
        for (GraphEdge<L> edge : g.getEdges()) {
            GraphNode<L> node1 = edge.getNode1();
            GraphNode<L> node2 = edge.getNode2();

            // Se i due nodi dell'arco sono in insiemi diversi li unisce
            if (!f.findSet(node1).equals(f.findSet(node2))) {
                f.union(node1, node2);
            }
        }

        // Aggiunge le componenti connesse all'insieme finale (se due nodi appartengono allo stesso
        // componente viene aggiunto solo una volta)
        for (GraphNode<L> node : g.getNodes()) {
            result.add(f.getCurrentElementsOfSetContaining(node));
        }

        // Elimina tutti gli insiemi disgiunti per poter riutilizzare l'istanza della classe
        f.clear();
        return result;
    }
}
