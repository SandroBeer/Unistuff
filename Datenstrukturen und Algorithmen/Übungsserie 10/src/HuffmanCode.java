import java.util.Scanner;
import java.util.PriorityQueue;

public class HuffmanCode {
	
	Node root; // root node of the tree that represents the huffman code
	Node[] leaves = new Node[256]; // contains the leaf nodes or null if the ascii-character (with the corresponding int-index)
								   // isn't in the input-String
	
	public static void main(String[] args) {
		String output;
		float zeichen, bits;
		HuffmanCode code = new HuffmanCode();
		Scanner scan = new Scanner(System.in);
		System.out.println("This program converts a given input String into huffman code. Please enter your input:");
		String input = scan.nextLine();
		scan.close();
		System.out.println("Thanks, your given input in huffman code gives the following ouput:");
		output = code.printCode(input);
		System.out.println(output);
		zeichen = input.length();
		bits = output.length();
		System.out.println("The average number of bits per character is " + (bits / zeichen) + " for your input.");
		
	}
	
	// method for printing the code in the console
	public String printCode(String input) {
		String output = "";
		this.prefixCode(input);
		for (int i = 0; i < input.length(); i++) {
			output += (this.encode(input.charAt(i)));
		}
		return output;
	}
	
	// method that generates a huffman code to the input-String
	public void prefixCode(String input) {
		// ascii counts how many times each ascii-character occurs in the input-String
		int[] ascii = new int[256];
		
		for (int i = 0; i < input.length(); i++) {
			ascii[(int) input.charAt(i)]++;
		}
		
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		for (int i = 0; i < 256; i++) {
			if (ascii[i] == 0) continue;
			Node toAdd = new Node((char) i, ascii[i]);
			this.leaves[i] = toAdd;
			queue.add(toAdd);
		}

		while (queue.size() > 1) {
			Node left = queue.poll();
			Node right = queue.poll();
			Node n = new Node(left.occurrence + right.occurrence, null, left, right);
			left.parent = n;
			right.parent = n;
			queue.add(n);
		}
		this.root = queue.poll();
	}
	
	// this method is called by the printCode-method with each character of the input-String
	public String encode(char toEncode) {
		Node start = leaves[(int) toEncode];
		String code = "";
		while (start.parent != null) {
			if (start == start.parent.left) {
				code += "0";
			}
			else {
				code += "1";
			}
			start = start.parent;
		}
		return this.reverse(code);
	}
	
	// reverses a String (is necessary with the String code from the encode-method, because we concatenate the String from bottom to top)
	public String reverse(String input) {
		String output = "";
		for (int i = input.length() - 1; i >= 0; i--) {
			output += input.charAt(i);
		}
		return output;
	}
}