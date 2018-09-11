/**
 * 
 */
package graph;

/**
 * @author alexfalconer-athanassakos
 *
 */
public class GraphTest {

	/**
	 * 
	 */
	
	public static void main(String[] argv) throws NoSuchNodeException{
		Node<Integer> a;
		Node<Integer> b;
		DirectionalGraph<Integer> graph = new DirectionalGraph<Integer>();
		for (int i = 1; i < 10; i++){
			graph.addNode(i, i);
			if (i == 2){
				graph.addEdge(1, 2);
			}
			if (i == 3){
				graph.addEdge(2, 3);
			}
			if (i == 4){
				graph.addEdge(3, 4);
			}
			if (i == 5){
				graph.addEdge(4, 5);
			}
			if (i == 6){
				graph.addEdge(1, 6);
			}
			if (i == 7){
				graph.addEdge(1, 7);
			}
			if (i == 8){
				graph.addEdge(7, 8);
				graph.addEdge(8, 5);
			}
			if (i == 9){
				graph.addEdge(8, 9);
			}
		}
		System.out.println(graph);
		a = new Node<Integer>(1, 1);
		b = new Node<Integer>(2 ,2);
		//Should print one path 1, 2
		System.out.println("One level search" + graph.getPaths(a, b));
		
		b = new Node<Integer>(5, 5);
		//Should print include paths 1, 2, 3, 4, 5 and paths 1, 7, 8, 5
		System.out.println("Recursive search" + graph.getPaths(a, b));
		
		
	}

}
