/**
 * 
 */
package graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * An acyclic directed Graph of Node<T>.
 * Features:
 * - Edges are strictly one-directional;
 * - Node A is adjacent to Node B iff Node A has an Edge to Node B;
 * - One node can have edges to multiple nodes;
 * - Multiple nodes can have edges to one node;
 * - There are no cycles;
 * - There are no self-edges.
 * @author alexfalconer-athanassakos
 *
 */
public class DirectionalGraph<T> extends Graph<T> implements Serializable {
	
	/*Notes*/
	//Add a method to ensure no bi-directional pointers.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 801523104874741834L;

	/** Initializes an instance of DirectionalGraph. */
	public DirectionalGraph() {
		super(); //call the Graph constructor.
	}
	
	/**Adds a one directional edge from node with id to node with id, if from
	 * id does not equal to id.
	 * Does nothing in the case of an attempted self-edge.
	 * @param int from Node<T> with id.
	 * @param int to Node<T> with id.
	 */
	@Override
	public void addEdge(int from, int to) throws NoSuchNodeException{
		//loop through the Nodes in the Graph.
			for (Node<T> node: this.getNodes()){
				 //Check if this is the node to start from.
				if (node.equals(this.getNode(from)) && !(from == to)){
					//add the edge between them.
					this.nodeSetMap.get(node).add(this.getNode(to));
					return;
				}
			}
			
	}
	
	/** Adds a one-directional edge from Node to Node, if they are not the same.
	 * Does nothing in the case of an attempted self-edge.
	 * @param from The Node to add edge from.
	 * @param to The Node to end the edge at.
	 */
	@Override
	public void addEdge(Node<T> from, Node<T> to) throws NoSuchNodeException {
			//Loop through Nodes in the Graph.
		if ( this.nodeSetMap.containsKey(to) ) {
			for (Node<T> node: this.getNodes()){
				//Check if this is the node to start from, and from != to
				if (node.equals(from) && !from.equals(to)){ 
					//add the edge between them.
					this.nodeSetMap.get(node).add(to); 
					return; //terminate if we've added the edge.
				}
			} 
			throw new NoSuchNodeException("Node with ID: " + from.getId() + 
					"missing from this DirectionalGraph");
		} else {
			//In case of missing from or to Node...
			throw new NoSuchNodeException("Node with ID: " +  to.toString() + 
					" missing from this Directional Graph");
		}
	}
	
	/** Returns a List of List of Nodes of T which represents the
	 *  possible paths between Node A and Node B, presupposing the
	 *   uniqueness of nodes A and B.
	 * 
	 * @param A The Node to start the search from.
	 * @param B The Node to end the search at.
	 * @return List of List of Nodes of T.
	 */
	public HashSet<List<Node<T>>> getPaths(Node<T> A, Node<T> B){
		/* Create lists of tentative and final results,
		 *  to avoid modifying a List during iteration. */
		List<List<Node<T>>> tentative = new ArrayList<List<Node<T>>>();
		HashSet<List<Node<T>>> results = new HashSet<List<Node<T>>>();
		
		//Find the node A.
		for (Node<T> node: this.getNodes()){
			if (node.equals(A)){
				//Start search
				for (Node<T> neighbour : this.nodeSetMap.get(node)){
					//Add a path from A to neighbour, for each neighbour.
					List<Node<T>> path = new ArrayList<Node<T>>();
					path.add(A);
					if (neighbour.equals(B)){
						path.add(neighbour);
					} else { //loop through the lists of Nodes of T.
						for (List<Node<T>> p: this.getPaths(neighbour, B)){
							path.addAll(p); //add nodes in p to path.
						}
					}
					tentative.add(path); //add the path to list of tentatives.
				}
			}
		}
		//Remove all paths that don't end with B.
		for (List<Node<T>> path: tentative){
			if (path.get(path.size()-1).equals(B)){//check if last element is B.
				results.add(path); //add path to list of results.
			}
		}
		return results;
	}      

	/** Returns a list of T values of Nodes from nodeList.
	 * 
	 * @param nodeList List of nodes to 'unbox'.
	 * @return List of T.
	 */
	public List<T> unboxNodes(List<Node<T>> nodeList) {
		//initialise the list to store the objects.
		List<T> itemList = new ArrayList<T>();
		//loop through the nodes.
		for (Node<T> node: nodeList) {
			itemList.add(node.getValue()); //add item to the itemList.
		}
		return itemList; //return the itemList.
	}
	
	/** Returns an int number of edges in this DirectionalGraph.
	 *  @return The number of edges in this Graph.
	 */
	@Override
	public int getNumEdges() {
        int total = 0;

        for (Node<T> node : getNodes()) {
            total += getNeighbours(node).size();
        }

        return total;
		
	}
}
