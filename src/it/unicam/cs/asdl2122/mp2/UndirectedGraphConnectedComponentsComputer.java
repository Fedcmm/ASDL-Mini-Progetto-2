package it.unicam.cs.asdl2122.mp2;

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
public class UndirectedGraphConnectedComponentsComputer<L> {

    /**
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
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
        // TODO implementare
        return null;
    }
}
