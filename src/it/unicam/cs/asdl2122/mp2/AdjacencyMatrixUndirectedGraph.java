package it.unicam.cs.asdl2122.mp2;

import java.util.*;

// ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 * 
 * I nodi sono indicizzati da 0 a nodeCount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 * 
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 * 
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 *
 * Questa classe supporta i metodi di cancellazione di nodi e archi e
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 * 
 * @author Luca Tesei (template)
 * Federico Maria Cruciani, federicomar.cruciani@studenti.unicam.it (implementazione)
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    /*
     * Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
     * matrice di adiacenza
     */
    protected Map<GraphNode<L>, Integer> nodesIndex;

    /*
     * Matrice di adiacenza, gli elementi sono null o oggetti della classe
     * GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
     * dimensione gradualmente ad ogni inserimento di un nuovo nodo e di
     * ridimensionarsi se un nodo viene cancellato.
     */
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<>();
        this.nodesIndex = new HashMap<>();
    }

    @Override
    public int nodeCount() {
        return nodesIndex.size();
    }

    @Override
    public int edgeCount() {
        int count = 0;

        for (int i = 0; i < matrix.size(); i++) {
            ArrayList<GraphEdge<L>> row = matrix.get(i);

            // Il secondo for parte da i, evitando di contare entrambi gli archi (i, j) e (j, i)
            for (int j = i; j < row.size(); j++) {
                if (row.get(j) != null)
                    count++;
            }
        }

        return count;
    }

    @Override
    public void clear() {
        nodesIndex.clear();
        matrix.clear();
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(GraphNode<L> node) {
        nullCheck(node);

        Integer index = nodesIndex.get(node);
        if (index != null)
            return false; // Il nodo è già presente

        nodesIndex.put(node, nodeCount());

        // Aumenta di 1 la dimensione di tutte le righe
        for (ArrayList<GraphEdge<L>> row : matrix) {
            row.add(null);
        }
        // Aggiunge una nuova riga di dimensione nodeCount()
        matrix.add(new ArrayList<>(Collections.nCopies(nodeCount(), null)));

        return true;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(L label) {
        return addNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(GraphNode<L> node) {
        nullCheck(node);

        Integer oldIndex = nodesIndex.remove(node);
        if (oldIndex == null)
            throw new IllegalArgumentException("Il nodo specificato non esiste");

        // Rimuove gli archi che collegavano il nodo eliminato
        // Il metodo remove aggiorna gli indici della matrice in automatico
        matrix.remove(oldIndex.intValue());
        for (ArrayList<GraphEdge<L>> row : matrix) {
            row.remove(oldIndex.intValue());
        }

        for (Map.Entry<GraphNode<L>, Integer> entry : nodesIndex.entrySet()) {
            Integer index = entry.getValue();
            // Aggiorna gli indici maggiori di quello del nodo eliminato
            if (index != null && index > oldIndex)
                entry.setValue(index - 1);
        }
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(L label) {
        removeNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(int i) {
        removeNode(getNode(i));
    }

    @Override
    public GraphNode<L> getNode(GraphNode<L> node) {
        nullCheck(node);

        for (GraphNode<L> graphNode : nodesIndex.keySet()) {
            if (node.equals(graphNode))
                return graphNode;
        }
        return null;
    }

    @Override
    public GraphNode<L> getNode(L label) {
        return getNode(new GraphNode<>(label));
    }

    @Override
    public GraphNode<L> getNode(int i) {
        indexCheck(i);

        for (Map.Entry<GraphNode<L>, Integer> entry : nodesIndex.entrySet()) {
            Integer index = entry.getValue();
            if (index != null && index == i)
                return entry.getKey();
        }

        throw new IndexOutOfBoundsException("L'indice passato non corrisponde a nessun nodo");
    }

    @Override
    public int getNodeIndexOf(GraphNode<L> node) {
        nullCheck(node);

        Integer index = nodesIndex.get(node);
        if (index == null) // Map.get() restituisce null se la chiave non esiste
            throw new IllegalArgumentException("Il nodo specificato non esiste");

        return index;
    }

    @Override
    public int getNodeIndexOf(L label) {
        return getNodeIndexOf(new GraphNode<>(label));
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
        return new HashSet<>(nodesIndex.keySet());
    }

    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        nullCheck(edge);

        if (edge.isDirected())
            throw new IllegalArgumentException("Impossibile aggiungere un arco orientato in un grafo non orientato");

        int i = getNodeIndexOf(edge.getNode1());
        int j = getNodeIndexOf(edge.getNode2());

        ArrayList<GraphEdge<L>> row = matrix.get(i);
        // È sufficiente controllare solo una delle posizioni
        if (row.get(j) != null && edge.equals(row.get(j)))
            return false; // Esiste già un arco con gli stessi nodi di quello passato

        // Aggiunge l'arco in posizione (i, j)
        row.set(j, edge);

        // Aggiunge l'arco in posizione (j, i)
        row = matrix.get(j);
        row.set(i, edge);
        return true;
    }

    @Override
    public boolean addEdge(GraphNode<L> node1, GraphNode<L> node2) {
        return addEdge(new GraphEdge<>(node1, node2, isDirected()));
    }

    @Override
    public boolean addWeightedEdge(GraphNode<L> node1, GraphNode<L> node2, double weight) {
        return addEdge(new GraphEdge<>(node1, node2, isDirected(), weight));
    }

    @Override
    public boolean addEdge(L label1, L label2) {
        return addEdge(getNode(label1), getNode(label2));
    }

    @Override
    public boolean addWeightedEdge(L label1, L label2, double weight) {
        return addWeightedEdge(getNode(label1), getNode(label2), weight);
    }

    @Override
    public boolean addEdge(int i, int j) {
        return addEdge(getNode(i), getNode(j));
    }

    @Override
    public boolean addWeightedEdge(int i, int j, double weight) {
        return addWeightedEdge(getNode(i), getNode(j), weight);
    }

    @Override
    public void removeEdge(GraphEdge<L> edge) {
        nullCheck(edge);

        int i = getNodeIndexOf(edge.getNode1());
        int j = getNodeIndexOf(edge.getNode2());

        ArrayList<GraphEdge<L>> row = matrix.get(i);
        GraphEdge<L> graphEdge = row.get(j);

        if (!edge.equals(graphEdge))
            throw new IllegalArgumentException("L'arco passato non esiste");

        // Elimina l'arco in posizione (i, j)
        row.set(j, null);

        // Elimina l'arco in posizione (j, i)
        row = matrix.get(j);
        row.set(i, null);
    }

    @Override
    public void removeEdge(GraphNode<L> node1, GraphNode<L> node2) {
        removeEdge(new GraphEdge<>(node1, node2, isDirected()));
    }

    @Override
    public void removeEdge(L label1, L label2) {
        removeEdge(getNode(label1), getNode(label2));
    }

    @Override
    public void removeEdge(int i, int j) {
        removeEdge(getEdge(i, j));
    }

    @Override
    public GraphEdge<L> getEdge(GraphEdge<L> edge) {
        nullCheck(edge);

        int i = getNodeIndexOf(edge.getNode1());
        int j = getNodeIndexOf(edge.getNode2());

        return matrix.get(i).get(j);
    }

    @Override
    public GraphEdge<L> getEdge(GraphNode<L> node1, GraphNode<L> node2) {
        return getEdge(new GraphEdge<>(node1, node2, isDirected()));
    }

    @Override
    public GraphEdge<L> getEdge(L label1, L label2) {
        return getEdge(new GraphNode<>(label1), new GraphNode<>(label2));
    }

    @Override
    public GraphEdge<L> getEdge(int i, int j) {
        indexCheck(i);
        indexCheck(j);

        return matrix.get(i).get(j);
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        nullCheck(node);

        Set<GraphNode<L>> nodes = new HashSet<>();
        
        for (GraphEdge<L> edge : getEdgesOf(node)) {
            GraphNode<L> node1 = edge.getNode1();
            // Non è specificato se node corrisponde a node1 o node2, quindi va controllato
            nodes.add(node.equals(node1) ? edge.getNode2() : node1);
        }
        return nodes;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(L label) {
        return getAdjacentNodesOf(getNode(label));
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(int i) {
        return getAdjacentNodesOf(getNode(i));
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        return getEdgesOf(getNodeIndexOf(node));
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(L label) {
        return getEdgesOf(new GraphNode<>(label));
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(int i) {
        indexCheck(i);

        Set<GraphEdge<L>> edges = new HashSet<>();
        for (GraphEdge<L> edge : matrix.get(i)) {
            if (edge != null)
                edges.add(edge);
        }
        return edges;
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> edges = new HashSet<>();

        for (ArrayList<GraphEdge<L>> row : matrix) {
            for (GraphEdge<L> edge : row) {
                if (edge != null)
                    edges.add(edge);
            }
        }

        return edges;
    }

    /**
     * Metodo di utilità che lancia una {@link IndexOutOfBoundsException}
     * se l'indice passato non è valido
     *
     * @param index l'indice da controllare
     *
     * @throws IndexOutOfBoundsException se l'indice è fuori dai limiti dell'intervallo
     * <code>[0, nodeCount() - 1]</code>
     */
    private void indexCheck(int index) {
        if (index < 0 || index > nodeCount() - 1)
            throw new IndexOutOfBoundsException("Impossibile eseguire il metodo con un indice non valido");
    }

    /**
     * Metodo di utilità che lancia una {@link NullPointerException} se l'oggetto passato è <code>null</code>
     *
     * @param o l'oggetto da controllare
     *
     * @throws NullPointerException se l'oggetto è <code>null</code>
     */
    private void nullCheck(Object o) {
        if (o == null)
            throw new NullPointerException("Impossibile eseguire il metodo con un argomento null");
    }
}
