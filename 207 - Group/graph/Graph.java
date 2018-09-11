package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.io.Serializable;
/**
 * An undirected graph of Node<T>s.
 * @param <T> the type of values in this Graph's Nodes.
 */
public class Graph<T> implements  Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5124689914044031788L;
	// instance variables
	protected HashSet<Node<T>> nodes; //set of nodes.
	//hash map with the id of the node as a key, and the node as a value.
	protected HashMap<Integer, Node<T>> idNodeMap;
	/* hash map with the node as a key, and a hash set
	 *  of adjacent nodes as a value. */
	protected HashMap<Node<T>, HashSet<Node<T>>> nodeSetMap;

    /**
     * Creates a new empty Graph.
     */
        public Graph() {
        	//instantiate the variables.
        	this.nodes = new HashSet<Node<T>>();
        	this.idNodeMap = new HashMap<Integer, Node<T>>();	
        	this.nodeSetMap = new HashMap<Node<T>, HashSet<Node<T>>>();
        }

    /**
     * Returns a Set of Nodes in this Graph.
     * @return a Set of Nodes in this Graph.
     */
		public Set<Node<T>> getNodes() {
    		return (Set<Node<T>>) this.nodes;
    	}

    /**
     * Returns the Node from this Graph with the given ID.
     * @param id the ID of the Node to return.
     * @return the Node from this Graph with the given ID.
     * @throws NoSuchNodeException if there is no Node with ID
     *    id in this Graphs.
     */
        public Node<T> getNode(int id) throws NoSuchNodeException {
        	//check if the nodes exist in the hash map.
        	if ( this.idNodeMap.containsKey(id) )
        		return this.idNodeMap.get(id);
        	else //throw exception if the node isn't in the map.
        		throw new NoSuchNodeException
        		("Node with ID " + id + " not contained in the Graph.");
        }
        
        public Node<T> getNode(T value) throws NoSuchNodeException{
        	for (Node<T> node : this.nodeSetMap.keySet()){
        		if (node.getValue().equals(value)){
        			return node;
        		}
        	}
        	throw new NoSuchNodeException("No node with value " 
        	+ value.toString() + " was found");
        }

    /**
     * Returns a Set of neighbours of the given Node.
     * @param node the Node whose neighbours are returned.
     * @return a Set of neighbours of node.
     */        
		public Set<Node<T>> getNeighbours(Node<T> node) {
        	return (Set<Node<T>>) this.nodeSetMap.get(node);
        }

    /**
     * Returns whether Nodes with the given IDs are adjacent in this Graph.
     * @param id1 ID of the node to test for adjacency.
     * @param id2 ID of the node to test for adjacency.
     * @return true, if Nodes with IDs id1 and id2 are adjacent in this Graph,
     *    and false otherwise.
     * @throws NoSuchNodeException if node with ID id1 or id2 is not in this 
     * Graph.
     */
        public boolean areAdjacent(int id1, int id2) 
        		throws NoSuchNodeException {
        	//check if the nodes exist in the hash map.
        	if ( this.idNodeMap.containsKey(id1) &
        			this.idNodeMap.containsKey(id2) )
        	{
        		//get the nodes associated with id1 and id2.
        		Node<T> node1 = this.idNodeMap.get(id1);
        		Node<T> node2 = this.idNodeMap.get(id2);
        		//check if node2 is in the hash set of node1.
        		if ( this.nodeSetMap.get(node1).contains(node2) )
        		{
        			return true;
        		}
        	}
        	else//throw exception if one or all of the nodes aren't in the map.
        	{
        		throw new NoSuchNodeException
        		("One or more of the nodes are not contained in the Graph.");
        	}
        	return false;
        }

    /**
     * Returns whether the given Nodes are adjacent in this Graph.
     * @param node1 the Node to test for adjacency with node2.
     * @param node2 the Node to test for adjacency with node1.
     * @return true, if node1 and node2 are adjacent in this Graph,
     *    and false otherwise.
     * @throws NoSuchNodeException if node1 or node2 are not in this Graph.
     */
    // areAdjacent
        
        public boolean areAdjacent(Node<T> node1, Node<T> node2) 
        		throws NoSuchNodeException {
        	//check if the nodes exist in the hash map.
        	if ( this.nodeSetMap.containsKey(node1) &
        			this.nodeSetMap.containsKey(node2) )
        	{
        		//check if node2 is in the hash set of node1.
        		if ( this.nodeSetMap.get(node1).contains(node2) )
        		{
        			return true;
        		}
        	}
        	else//throw exception if one or all of the nodes aren't in the map.
        	{
        		throw new NoSuchNodeException
        		("One or more of the nodes are not contained in the Graph.");
        	}
        	return false;
        }

    /**
     * Returns the number of nodes in this Graph.
     * @return The number of nodes in this Graph.
     */
    public int getNumNodes() {
        return getNodes().size();	
    }

	/**
     * Returns the number of edges in this Graph.
     * @return The number of edges in this Graph.
     */
    public int getNumEdges() {	
        int total = 0;

        for (Node<T> node : getNodes()) {
            total += getNeighbours(node).size();
        }

        return total / 2;
    }

    /**
     * Adds a new Node with the given value to this Graph. 
     * @param id the ID of the new Node.
     * @param value the value of the new Node.
     */
    public void addNode(int id, T value) {
    	//create a new Node with id and value.
    	Node<T> node = new Node<T>(id, value);
    	//create a new Set of adjacent nodes for Node.
    	HashSet<Node<T>> hSet = new HashSet<Node<T>>();
    	//add node to the node set and hashSets.
    	this.nodes.add(node);
    	this.idNodeMap.put(id, node);
    	this.nodeSetMap.put(node, hSet);
    }
    
    /** Removes a node from the Graph.
     * 
     * @param node the node to remove.
     * @throws NoSuchNodeException if node is not in this Graph.
     */
	public void removeNode(Node<T> node) throws NoSuchNodeException {
		//check if the node is in the graph, throw an exception otherwise
		if ( !this.nodes.contains(node) ) {	
    		throw new NoSuchNodeException
    		("Node is not contained in the Graph");
    	}
		//Get the set of adjacent nodes of node.
		Set<Node<T>> adjacents = this.nodeSetMap.get(node);
		//loop through the nodes.
		for (Node<T> adjacentNode: adjacents) {
			/* Loop through each of the adjacent nodes, removing
			 * the edges connecting them to node. */
			this.removeEdge(node, adjacentNode);
			for (Node<T> newAdjacent: adjacents) {
				/* Loop through each of the adjacent nodes, adding the
				 * edges in the set as adjacents. */
				this.addEdge(adjacentNode, newAdjacent);
			}
		}
		//remove the node from the id-node map.
		this.idNodeMap.remove(node.getId());
		//remove the node from the node-set map.
		this.nodeSetMap.remove(node);
		//remove the node from the graph.
		this.nodes.remove(node);
	}
	
	/** Removes a node from the Graph.
	 * @param id id of the node.
	 */
	public void removeNode(int id) throws NoSuchNodeException {
		//get the node from the hash map using the id.
		Node<T> node = this.idNodeMap.get(id);
		this.removeNode(node); //remove the node (call the overloaded method).
	}
	
    /**
     * Adds an edge between the given nodes in this Graph. If there 
     * is already an edge between node1 and node2, does nothing.
     * @param node1 the node to add an edge to and from node2.
     * @param node2 the node to add an edge to and from node1.
     * @throws NoSuchNodeException if node1 or node2 is not in
     *    this Graph.
     */
    public void addEdge(Node<T> node1, Node<T> node2) 
    		throws NoSuchNodeException {
    	 //make sure we aren't putting self edges.
    	if ( node1.getId() == node2.getId() ) return;
    	//check if the nodes exist in the hash map.
    	if ( this.nodeSetMap.containsKey(node1) &
    			this.nodeSetMap.containsKey(node2) )
    	{
    		//check if node1 has node2 in its node set, and add it if not.
    		if  ( !( this.nodeSetMap.get(node1).contains(node2) ) )
    		{
    			this.nodeSetMap.get(node1).add(node2);
    		}
    		//check if node1 has node2 in its node set, and add it if not.
    		if  ( !( this.nodeSetMap.get(node2).contains(node1) ) )
    		{
    			this.nodeSetMap.get(node2).add(node1);
    		}
    	}
    	else //throw exception if one or all of the nodes aren't in the map.
    	{
    		throw new NoSuchNodeException
    		("One or more of the nodes are not contained in the Graph.");
    	}
    }
    
    /**
     * Adds an edge between the nodes with the given IDs in this Graph. 
     * @param id1 ID of the node to add an edge to and from.
     * @param id2 ID of the node to add an edge to and from.
     * @throws NoSuchNodeExceptionf there is no Node with ID
     *    id1 or ID id2 in this Graph.
     */
    public void addEdge(int id1, int id2) 
    		throws NoSuchNodeException {
    	if ( id1 == id2 ) return; //make sure we aren't putting self edges.
    	if ( this.idNodeMap.containsKey(id1) &
    			this.idNodeMap.containsKey(id2) )
    	{
    		Node<T> node1 = this.idNodeMap.get(id1);
    		Node<T> node2 = this.idNodeMap.get(id2);
    		//check if node1 has node2 in its node set, and add it if not.
    		if  ( !( this.nodeSetMap.get(node1).contains(node2) ) )
    		{
    			this.nodeSetMap.get(node1).add(node2);
    		}
    		//check if node1 has node2 in its node set, and add it if not.
    		if  ( !( this.nodeSetMap.get(node2).contains(node1) ) )
    		{
    			this.nodeSetMap.get(node2).add(node1);
    		}
    	}
    	else //throw exception if one or all of the nodes aren't in the map.
    	{
    		throw new NoSuchNodeException
    		("One or more of the nodes are not contained in the Graph.");
    	}
    }
    
    /**
     * Removes an edge between the given nodes in this Graph. If there
     * is no edge between node1 and node2, do nothing.
     * @param node1 the node to remove an edge with node2 from.
     * @param node2 the node to remove an edge with node1 from.
     * @throws NoSuchNodeException if node1 or node2 is not in
     *    this Graph.
     */
    public void removeEdge(Node<T> node1, Node<T> node2) 
    		throws NoSuchNodeException {
    	 //make sure we aren't trying to remove an edge from a node and itself.
    	if ( node1.getId() == node2.getId() ) return;
    	//check if the nodes exist in the hash map
    	if ( this.nodeSetMap.containsKey(node1) &
    			this.nodeSetMap.containsKey(node2) )
    	{
    		//check if node1 has node2 in its node set, and remove it if so.
    		if  ( this.nodeSetMap.get(node1).contains(node2) )
    		{
    			this.nodeSetMap.get(node1).remove(node2);
    		}
    		//check if node1 has node2 in its node set, and remove it if so.
    		if  ( this.nodeSetMap.get(node2).contains(node1) )
    		{
    			this.nodeSetMap.get(node2).remove(node1);
    		}
    	}
    	else //throw exception if one or all of the nodes aren't in the map.
    	{
    		throw new NoSuchNodeException
    		("One or more of the nodes are not contained in the Graph.");
    	}
    }
    
    /**
     * Remove an edge between the nodes with the given IDs in this Graph.
     * @param id1 ID of the node to remove an edge with node with id1 from.
     * @param id2 ID of the node to remove an edge with node with id2 from.
     * @throws NoSuchNodeExceptionf there is no Node with ID 
     *    id1 or ID id2 in this Graph.
     */
    public void removeEdge(int id1, int id2) 
    		throws NoSuchNodeException {
   	 //make sure we aren't trying to remove an edge from a node and itself.
    	if ( this.idNodeMap.containsKey(id1) &
    			this.idNodeMap.containsKey(id2) )
    	{
    		Node<T> node1 = this.idNodeMap.get(id1);
    		Node<T> node2 = this.idNodeMap.get(id2);
    		//check if node1 has node2 in its node set, and add it if not.
    		//check if node1 has node2 in its node set, and remove it if so.
    		if  ( this.nodeSetMap.get(node1).contains(node2) )
    		{
    			this.nodeSetMap.get(node1).remove(node2);
    		}
    		//check if node1 has node2 in its node set, and remove it if so.
    		if  ( this.nodeSetMap.get(node2).contains(node1) )
    		{
    			this.nodeSetMap.get(node2).remove(node1);
    		}
    	}
    	else //throw exception if one or all of the nodes aren't in the map.
    	{
    		throw new NoSuchNodeException
    		("One or more of the nodes are not contained in the Graph.");
    	}
    }

    @Override
    /** Returns a string representation of Graph.
     *  @return the string representation of the Graph.
     */
    public String toString() {

        String result = "";
        result += "Number of nodes: " + getNumNodes() + "\n";
        result += "Number of edges: " + getNumEdges() + "\n";

        for (Node<T> node: getNodes()) {
            result += node + " is adjacent to: ";
            for    (Node<T> neighbour: getNeighbours(node)) {
                result += neighbour + " ";
            }
            result += "\n";
        }
        return result;
    }
}