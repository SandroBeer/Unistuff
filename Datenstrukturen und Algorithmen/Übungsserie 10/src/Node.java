public class Node implements Comparable<Node> {
	
	char symbol; // is set to '*' for inner nodes (they don't represent a character)
	int occurrence; // how many times the specific character occurs in the input String
	Node parent, left, right; // root has parent == null
	
	// this constructor is used for creating leaf nodes
	public Node(char symbol, int occurrence) {
		this.symbol = symbol;
		this.occurrence = occurrence;
		this.left = null; // leaf nodes don't have any children
		this.right = null;
	}
	
	// this constructor is used for inner nodes
	public Node(int occurrence, Node parent, Node left, Node right) {
		this.symbol = '*'; // specifies that this node is an inner node
		this.occurrence = occurrence;
		this.parent = parent;
		this.left = left;
		this.right = right;
	}
	
	// overwriting the compareTo method of the Comparable Interface
	public int compareTo(Node second) {
		if (this.occurrence > second.occurrence) return 1;
		else if (this.occurrence < second.occurrence) return -1;
		else return 0;
	}
}