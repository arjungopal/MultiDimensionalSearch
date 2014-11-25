import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;

/*
 * Author Arjun Gopal NET-ID : AXG145630
 * 
 */

public class AXG145630_Project6 {

	public static long sum;
	private static long maxPrice;
	private static long minPrice;
	private static NameNode nameNode;
	private static boolean alreadyExist;
	private static ProductNode product;
	private static ProductNode oldNode;
	private static List<Long> namesInIt;
	private static String operation;

	public static void main(String[] args) {
		List<Long> nameList = new ArrayList<Long>();
		Scanner sc = new Scanner(System.in);
		// The main red black tree which stores all the products with id as the
		// key
		RedBlackBST<Long, ProductNode> idTree = new RedBlackBST<Long, ProductNode>();
		SeparateChainingHashST<Long, ProductNode> idMap = new SeparateChainingHashST<Long, ProductNode>();
		SeparateChainingHashST<Long, NameNode> nameMap = new SeparateChainingHashST<Long, NameNode>();
		sum = 0;
		long id = 0;
		long price = 0;
		String[] input;
		//Going to an infinite loop of operations. Give 101 as input to exit.
		while (true) {
			input = sc.nextLine().split("\\s+");
			operation = input[0];
			/*
			 * if (operation.equals('#')) { sc.nextLine(); continue; }
			 */if (operation.equals("101")) {
				break;
			}
			// Operation is Insert. Insert into the main id tree and updates the
			// references in the name trees
			if (operation.equals("Insert")) {

				alreadyExist = false;
				//Read ID
				id = Long.parseLong(input[1]);
				//Read Price
				price = (Math.round(Double.parseDouble(input[2]) * 100));
				nameList.clear();
				for (int i = 3; !(input[i].equals("0")); i++) {
					long name = Long.parseLong(input[i]);
					nameList.add(name);
				}
				product = new ProductNode(id, nameList, price);
				//Get the oldNode from the tree
				oldNode = idMap.get(id);
				if (oldNode != null) {
					//New Entry
					alreadyExist = true;
				}
				if (nameList.isEmpty()) {
					// update the price and continue.
					oldNode.price = price;
					continue;
				}
				if (alreadyExist) {
					namesInIt = oldNode.nameList;
					for (Long name : namesInIt) {
						nameNode = nameMap.get(name);
						nameNode.links.remove(oldNode);
					}
					oldNode.price = price;
					oldNode.nameList = product.nameList;
					product = oldNode;

				} else {
					// Its a new Node. Put the node in the main red black tree
					idTree.put(id, product);
					idMap.put(id, product);
				}

				// Now update the names in the hash buckets of redblack tree
				for (long name : nameList) {
					NameNode nameNode = nameMap.get(name);
					if (nameNode != null) {
						List<ProductNode> prodList = nameNode.links;
						prodList.add(product);
					} else {
						nameNode = new NameNode(name, product);
						nameMap.put(name, nameNode);
					}
				}
				if (!alreadyExist) {
					sum = sum + 100;
				}

			}
			// Operation is find. Search in the main tree.
			if (operation.equals("Find")) {
				id = Long.parseLong(input[1]);
				ProductNode productNode = idMap.get(id);
				if (productNode != null) {
					sum = sum + productNode.price;
					// sum = Double.valueOf(df.format(sum));
				}
			}
			// Operation is delete.
			if (operation.equals("Delete")) {
				id = Long.parseLong(input[1]);
				ProductNode pro = idMap.get(id);
				if (pro != null) {
					nameList = pro.nameList;
					for (long name : nameList) {
						NameNode nameNode = nameMap.get(name);
						List<ProductNode> prodList = nameNode.links;
						prodList.remove(pro);
						if (prodList.isEmpty())
							nameMap.delete(name);
						sum = sum + name * 100;

					}
					idTree.delete(id);
					idMap.delete(id);
				}
			}
			// Operation is FindMinPrice. Just hash the name. Go to the bucket.
			// Find the corresponding name in the tree and search within the
			// references attached to them
			if (operation.equals("FindMinPrice")) {
				long name = Long.parseLong(input[1]);
				minPrice = Long.MAX_VALUE;
				nameNode = nameMap.get(name);
				if (nameNode == null) {
					continue;
				}

				List<ProductNode> prodList = nameNode.links;
				if (prodList.isEmpty()) {
					continue;
				}
				for (ProductNode pro : prodList) {
					if (pro.price < minPrice) {
						minPrice = pro.price;
					}
				}
				sum = sum + minPrice;
				// sum = Double.valueOf(df.format(sum));
			}
			// Operation is FindMaxPrice. Just hash the name. Go to the bucket.
			// Find the corresponding name in the tree and search within the
			// references attached to them
			if (operation.equals("FindMaxPrice")) {
				long name = Long.parseLong(input[1]);
				maxPrice = Long.MIN_VALUE;
				NameNode nameNode = nameMap.get(name);
				if (nameNode == null) {
					continue;
				}
				List<ProductNode> prodList = nameNode.links;
				if (prodList.isEmpty()) {

					continue;
				}
				for (ProductNode pro : prodList) {
					if (pro.price > maxPrice) {
						maxPrice = pro.price;
					}
				}
				sum = sum + maxPrice;
				// sum = Double.valueOf(df.format(sum));
			}
			// Operation is FindPriceRange. Just hash the name. Go to the
			// bucket.
			// Find the corresponding name in the tree and search within the
			// references attached to them
			if (operation.equals("FindPriceRange")) {
				long name = Long.parseLong(input[1]);
				long low = (Math.round(Double.parseDouble(input[2]) * 100));
				long high = (Math.round(Double.parseDouble(input[3]) * 100));

				NameNode nameNode = nameMap.get(name);
				List<ProductNode> links = nameNode.links;
				int count = 0;
				for (ProductNode pro : links) {
					if (pro.price >= low && pro.price <= high) {
						count++;
					}
				}
				sum = sum + count * 100;
			}
			// Search the main tree for all the nodes in the range. Update the
			// value price
			long oldPrice;
			long newPrice;
			long difference;
			long id1;
			long id2;
			long hike;
			if (operation.equals("PriceHike")) {
				id1 = Long.parseLong(input[1]);
				id2 = Long.parseLong(input[2]);
				hike = Long.parseLong(input[3]);
				for (ProductNode pro : idTree.values(id1, id2)) {
					oldPrice = pro.price;
					newPrice = ((oldPrice * hike) / 100) + oldPrice;
					pro.price = newPrice;
					difference = newPrice - oldPrice;
					sum = sum + difference;
				}
			}

		}
		BigDecimal finalSum = new BigDecimal(
				Double.toString((((double) sum) / 100)));
		System.out.println(finalSum);
	}

}

// Name Node which stores the name and the assosiated product nodes
class NameNode {
	long name;
	List<ProductNode> links = null;

	NameNode(long name, ProductNode productNode) {
		if (links == null) {
			links = new ArrayList<ProductNode>();
			links.add(productNode);
			this.name = name;
		} else {
			links.add(productNode);
		}
	}
}

// Product Node which contains the product iformation.
class ProductNode {
	long id;
	List<Long> nameList;
	long price;

	ProductNode(long id, List<Long> nameList, long price) {
		this.id = id;
		this.nameList = new ArrayList<Long>();
		for (long name : nameList) {
			this.nameList.add(name);
		}
		this.price = price;
	}
}

// Red Black Tree code base
// /http://algs4.cs.princeton.edu/code/
class RedBlackBST<Key extends Comparable<Key>, Value> {

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	public Node root; // root of the BST

	// BST helper node data type
	private class Node {
		private Key key; // key
		private Value val; // associated data
		private Node left, right; // links to left and right subtrees
		private boolean color; // color of parent link
		private int N; // subtree count

		public Node(Key key, Value val, boolean color, int N) {
			this.key = key;
			this.val = val;
			this.color = color;
			this.N = N;
		}
	}

	/*************************************************************************
	 * Node helper methods
	 *************************************************************************/
	// is node x red; false if x is null ?
	private boolean isRed(Node x) {
		if (x == null)
			return false;
		return (x.color == RED);
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(Node x) {
		if (x == null)
			return 0;
		return x.N;
	}

	/*************************************************************************
	 * Size methods
	 *************************************************************************/

	// return number of key-value pairs in this symbol table
	public int size() {
		return size(root);
	}

	// is this symbol table empty?
	public boolean isEmpty() {
		return root == null;
	}

	/*************************************************************************
	 * Standard BST search
	 *************************************************************************/

	// value associated with the given key; null if no such key
	public Value get(Key key) {
		return get(root, key);
	}

	// value associated with the given key in subtree rooted at x; null if no
	// such key
	private Value get(Node x, Key key) {
		while (x != null) {
			int cmp = key.compareTo(x.key);
			if (cmp < 0)
				x = x.left;
			else if (cmp > 0)
				x = x.right;
			else
				return x.val;
		}
		return null;
	}

	// is there a key-value pair with the given key?
	public boolean contains(Key key) {
		return (get(key) != null);
	}

	// is there a key-value pair with the given key in the subtree rooted at x?
	// private boolean contains(Node x, Key key) {
	// return (get(x, key) != null);
	// }

	/*************************************************************************
	 * Red-black insertion
	 *************************************************************************/

	// insert the key-value pair; overwrite the old value with the new value
	// if the key is already present
	public void put(Key key, Value val) {
		root = put(root, key, val);
		root.color = BLACK;
		// assert check();
	}

	// insert the key-value pair in the subtree rooted at h
	private Node put(Node h, Key key, Value val) {
		if (h == null)
			return new Node(key, val, RED, 1);

		int cmp = key.compareTo(h.key);
		if (cmp < 0)
			h.left = put(h.left, key, val);
		else if (cmp > 0)
			h.right = put(h.right, key, val);
		else
			h.val = val;

		// fix-up any right-leaning links
		if (isRed(h.right) && !isRed(h.left))
			h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))
			flipColors(h);
		h.N = size(h.left) + size(h.right) + 1;

		return h;
	}

	/*************************************************************************
	 * Red-black deletion
	 *************************************************************************/

	// delete the key-value pair with the minimum key
	public void deleteMin() {
		if (isEmpty())
			throw new NoSuchElementException("BST underflow");

		// if both children of root are black, set root to red
		if (!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = deleteMin(root);
		if (!isEmpty())
			root.color = BLACK;
		// assert check();
	}

	// delete the key-value pair with the minimum key rooted at h
	private Node deleteMin(Node h) {
		if (h.left == null)
			return null;

		if (!isRed(h.left) && !isRed(h.left.left))
			h = moveRedLeft(h);

		h.left = deleteMin(h.left);
		return balance(h);
	}

	// delete the key-value pair with the maximum key
	public void deleteMax() {
		if (isEmpty())
			throw new NoSuchElementException("BST underflow");

		// if both children of root are black, set root to red
		if (!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = deleteMax(root);
		if (!isEmpty())
			root.color = BLACK;
		// assert check();
	}

	// delete the key-value pair with the maximum key rooted at h
	private Node deleteMax(Node h) {
		if (isRed(h.left))
			h = rotateRight(h);

		if (h.right == null)
			return null;

		if (!isRed(h.right) && !isRed(h.right.left))
			h = moveRedRight(h);

		h.right = deleteMax(h.right);

		return balance(h);
	}

	// delete the key-value pair with the given key
	public void delete(Key key) {
		// if both children of root are black, set root to red
		if (!isRed(root.left) && !isRed(root.right))
			root.color = RED;

		root = delete(root, key);
		if (!isEmpty())
			root.color = BLACK;
		// assert check();
	}

	// delete the key-value pair with the given key rooted at h
	private Node delete(Node h, Key key) {
		// assert contains(h, key);

		if (key.compareTo(h.key) < 0) {
			if (!isRed(h.left) && !isRed(h.left.left))
				h = moveRedLeft(h);
			h.left = delete(h.left, key);
		} else {
			if (isRed(h.left))
				h = rotateRight(h);
			if (key.compareTo(h.key) == 0 && (h.right == null))
				return null;
			if (!isRed(h.right) && !isRed(h.right.left))
				h = moveRedRight(h);
			if (key.compareTo(h.key) == 0) {
				Node x = min(h.right);
				h.key = x.key;
				h.val = x.val;
				// h.val = get(h.right, min(h.right).key);
				// h.key = min(h.right).key;
				h.right = deleteMin(h.right);
			} else
				h.right = delete(h.right, key);
		}
		return balance(h);
	}

	/*************************************************************************
	 * red-black tree helper functions
	 *************************************************************************/

	// make a left-leaning link lean to the right
	private Node rotateRight(Node h) {
		// assert (h != null) && isRed(h.left);
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// make a right-leaning link lean to the left
	private Node rotateLeft(Node h) {
		// assert (h != null) && isRed(h.right);
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// flip the colors of a node and its two children
	private void flipColors(Node h) {
		// h must have opposite color of its two children
		// assert (h != null) && (h.left != null) && (h.right != null);
		// assert (!isRed(h) && isRed(h.left) && isRed(h.right))
		// || (isRed(h) && !isRed(h.left) && !isRed(h.right));
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private Node moveRedLeft(Node h) {
		// assert (h != null);
		// assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private Node moveRedRight(Node h) {
		// assert (h != null);
		// assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
		flipColors(h);
		if (isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	// restore red-black tree invariant
	private Node balance(Node h) {
		// assert (h != null);

		if (isRed(h.right))
			h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))
			flipColors(h);

		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

	/*************************************************************************
	 * Utility functions
	 *************************************************************************/

	// height of tree (1-node tree has height 0)
	public int height() {
		return height(root);
	}

	private int height(Node x) {
		if (x == null)
			return -1;
		return 1 + Math.max(height(x.left), height(x.right));
	}

	/*************************************************************************
	 * Ordered symbol table methods.
	 *************************************************************************/

	// the smallest key; null if no such key
	public Key min() {
		if (isEmpty())
			return null;
		return min(root).key;
	}

	// the smallest key in subtree rooted at x; null if no such key
	private Node min(Node x) {
		// assert x != null;
		if (x.left == null)
			return x;
		else
			return min(x.left);
	}

	// the largest key; null if no such key
	public Key max() {
		if (isEmpty())
			return null;
		return max(root).key;
	}

	// the largest key in the subtree rooted at x; null if no such key
	private Node max(Node x) {
		// assert x != null;
		if (x.right == null)
			return x;
		else
			return max(x.right);
	}

	// the largest key less than or equal to the given key
	public Key floor(Key key) {
		Node x = floor(root, key);
		if (x == null)
			return null;
		else
			return x.key;
	}

	// the largest key in the subtree rooted at x less than or equal to the
	// given key
	private Node floor(Node x, Key key) {
		if (x == null)
			return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0)
			return x;
		if (cmp < 0)
			return floor(x.left, key);
		Node t = floor(x.right, key);
		if (t != null)
			return t;
		else
			return x;
	}

	// the smallest key greater than or equal to the given key
	public Key ceiling(Key key) {
		Node x = ceiling(root, key);
		if (x == null)
			return null;
		else
			return x.key;
	}

	// the smallest key in the subtree rooted at x greater than or equal to the
	// given key
	private Node ceiling(Node x, Key key) {
		if (x == null)
			return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0)
			return x;
		if (cmp > 0)
			return ceiling(x.right, key);
		Node t = ceiling(x.left, key);
		if (t != null)
			return t;
		else
			return x;
	}

	// the key of rank k
	public Key select(int k) {
		if (k < 0 || k >= size())
			return null;
		Node x = select(root, k);
		return x.key;
	}

	// the key of rank k in the subtree rooted at x
	private Node select(Node x, int k) {
		// assert x != null;
		// assert k >= 0 && k < size(x);
		int t = size(x.left);
		if (t > k)
			return select(x.left, k);
		else if (t < k)
			return select(x.right, k - t - 1);
		else
			return x;
	}

	// number of keys less than key
	public int rank(Key key) {
		return rank(key, root);
	}

	// number of keys less than key in the subtree rooted at x
	private int rank(Key key, Node x) {
		if (x == null)
			return 0;
		int cmp = key.compareTo(x.key);
		if (cmp < 0)
			return rank(key, x.left);
		else if (cmp > 0)
			return 1 + size(x.left) + rank(key, x.right);
		else
			return size(x.left);
	}

	/***********************************************************************
	 * Range count and range search.
	 ***********************************************************************/

	// all of the keys, as an Iterable
	public Iterable<Value> values() {
		return values(min(), max());
	}

	// the keys between lo and hi, as an Iterable
	public Iterable<Value> values(Key lo, Key hi) {
		Queue<Value> queue = new Queue<Value>();
		// if (isEmpty() || lo.compareTo(hi) > 0) return queue;
		keys(root, queue, lo, hi);
		return queue;
	}

	// add the keys between lo and hi in the subtree rooted at x
	// to the queue
	private void keys(Node x, Queue<Value> queue, Key lo, Key hi) {
		if (x == null)
			return;
		int cmplo = lo.compareTo(x.key);
		int cmphi = hi.compareTo(x.key);
		if (cmplo < 0)
			keys(x.left, queue, lo, hi);
		if (cmplo <= 0 && cmphi >= 0)
			queue.enqueue(x.val);
		if (cmphi > 0)
			keys(x.right, queue, lo, hi);
	}

	// --------------------

	// --------------------
	// number keys between lo and hi
	public int size(Key lo, Key hi) {
		if (lo.compareTo(hi) > 0)
			return 0;
		if (contains(hi))
			return rank(hi) - rank(lo) + 1;
		else
			return rank(hi) - rank(lo);
	}

	/*
	 * private boolean check() { if (!isBST())
	 * StdOut.println("Not in symmetric order"); if (!isSizeConsistent())
	 * StdOut.println("Subtree counts not consistent"); if (!isRankConsistent())
	 * StdOut.println("Ranks not consistent"); if (!is23())
	 * StdOut.println("Not a 2-3 tree"); if (!isBalanced())
	 * StdOut.println("Not balanced"); return isBST() && isSizeConsistent() &&
	 * isRankConsistent() && is23() && isBalanced(); }
	 */
	// does this binary tree satisfy symmetric order?
	// Note: this test also ensures that data structure is a binary tree since
	// order is strict

	// is the tree rooted at x a BST with all keys strictly between min and max
	// (if min or max is null, treat as empty constraint)
	// Credit: Bob Dondero's elegant solution

}

class Queue<Item> implements Iterable<Item> {
	private int N; // number of elements on queue
	private Node<Item> first; // beginning of queue
	private Node<Item> last; // end of queue

	// helper linked list class
	private static class Node<Item> {
		private Item item;
		private Node<Item> next;
	}

	/**
	 * Initializes an empty queue.
	 */
	public Queue() {
		first = null;
		last = null;
		N = 0;
	}

	/**
	 * Is this queue empty?
	 * 
	 * @return true if this queue is empty; false otherwise
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Returns the number of items in this queue.
	 * 
	 * @return the number of items in this queue
	 */
	public int size() {
		return N;
	}

	/**
	 * Returns the item least recently added to this queue.
	 * 
	 * @return the item least recently added to this queue
	 * @throws java.util.NoSuchElementException
	 *             if this queue is empty
	 */
	public Item peek() {
		if (isEmpty())
			throw new NoSuchElementException("Queue underflow");
		return first.item;
	}

	/**
	 * Adds the item to this queue.
	 * 
	 * @param item
	 *            the item to add
	 */
	public void enqueue(Item item) {
		Node<Item> oldlast = last;
		last = new Node<Item>();
		last.item = item;
		last.next = null;
		if (isEmpty())
			first = last;
		else
			oldlast.next = last;
		N++;
	}

	/**
	 * Removes and returns the item on this queue that was least recently added.
	 * 
	 * @return the item on this queue that was least recently added
	 * @throws java.util.NoSuchElementException
	 *             if this queue is empty
	 */
	public Item dequeue() {
		if (isEmpty())
			throw new NoSuchElementException("Queue underflow");
		Item item = first.item;
		first = first.next;
		N--;
		if (isEmpty())
			last = null; // to avoid loitering
		return item;
	}

	/**
	 * Returns a string representation of this queue.
	 * 
	 * @return the sequence of items in FIFO order, separated by spaces
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Item item : this)
			s.append(item + " ");
		return s.toString();
	}

	/**
	 * Returns an iterator that iterates over the items in this queue in FIFO
	 * order.
	 * 
	 * @return an iterator that iterates over the items in this queue in FIFO
	 *         order
	 */
	public Iterator<Item> iterator() {
		return new ListIterator<Item>(first);
	}

	// an iterator, doesn't implement remove() since it's optional
	private class ListIterator<Item> implements Iterator<Item> {
		private Node<Item> current;

		public ListIterator(Node<Item> first) {
			current = first;
		}

		public boolean hasNext() {
			return current != null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Item next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
			return item;
		}
	}

}

// Seperate Chaining Hashing code
// http://algs4.cs.princeton.edu/code/
class SeparateChainingHashST<Key, Value> {
	private static final int INIT_CAPACITY = 4;

	// largest prime <= 2^i for i = 3 to 31
	// not currently used for doubling and shrinking
	// private static final int[] PRIMES = {
	// 7, 13, 31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381,
	// 32749, 65521, 131071, 262139, 524287, 1048573, 2097143, 4194301,
	// 8388593, 16777213, 33554393, 67108859, 134217689, 268435399,
	// 536870909, 1073741789, 2147483647
	// };

	private int N; // number of key-value pairs
	private int M; // hash table size
	private SequentialSearchST<Key, Value>[] st; // array of linked-list symbol
													// tables

	// create separate chaining hash table
	public SeparateChainingHashST() {
		this(INIT_CAPACITY);
	}

	// create separate chaining hash table with M lists
	public SeparateChainingHashST(int M) {
		this.M = M;
		st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[M];
		for (int i = 0; i < M; i++)
			st[i] = new SequentialSearchST<Key, Value>();
	}

	// resize the hash table to have the given number of chains b rehashing all
	// of the keys
	private void resize(int chains) {
		SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(
				chains);
		for (int i = 0; i < M; i++) {
			for (Key key : st[i].keys()) {
				temp.put(key, st[i].get(key));
			}
		}
		this.M = temp.M;
		this.N = temp.N;
		this.st = temp.st;
	}

	// hash value between 0 and M-1
	private int hash(Key key) {
		return (key.hashCode() & 0x7fffffff) % M;
	}

	// return number of key-value pairs in symbol table
	public int size() {
		return N;
	}

	// is the symbol table empty?
	public boolean isEmpty() {
		return size() == 0;
	}

	// is the key in the symbol table?
	public boolean contains(Key key) {
		return get(key) != null;
	}

	// return value associated with key, null if no such key
	public Value get(Key key) {
		int i = hash(key);
		return st[i].get(key);
	}

	// insert key-value pair into the table
	public void put(Key key, Value val) {
		if (val == null) {
			delete(key);
			return;
		}

		// double table size if average length of list >= 10
		if (N >= 10 * M)
			resize(2 * M);

		int i = hash(key);
		if (!st[i].contains(key))
			N++;
		st[i].put(key, val);
	}

	// delete key (and associated value) if key is in the table
	public void delete(Key key) {
		int i = hash(key);
		if (st[i].contains(key))
			N--;
		st[i].delete(key);

		// halve table size if average length of list <= 2
		if (M > INIT_CAPACITY && N <= 2 * M)
			resize(M / 2);
	}

	// return keys in symbol table as an Iterable
	public Iterable<Key> keys() {
		Queue<Key> queue = new Queue<Key>();
		for (int i = 0; i < M; i++) {
			for (Key key : st[i].keys())
				queue.enqueue(key);
		}
		return queue;
	}

}

class SequentialSearchST<Key, Value> {
	private int N; // number of key-value pairs
	private Node first; // the linked list of key-value pairs

	// a helper linked list data type
	private class Node {
		private Key key;
		private Value val;
		private Node next;

		public Node(Key key, Value val, Node next) {
			this.key = key;
			this.val = val;
			this.next = next;
		}
	}

	/**
	 * Initializes an empty symbol table.
	 */
	public SequentialSearchST() {
	}

	/**
	 * Returns the number of key-value pairs in this symbol table.
	 * 
	 * @return the number of key-value pairs in this symbol table
	 */
	public int size() {
		return N;
	}

	/**
	 * Is this symbol table empty?
	 * 
	 * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt>
	 *         otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Does this symbol table contain the given key?
	 * 
	 * @param key
	 *            the key
	 * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
	 *         <tt>false</tt> otherwise
	 */
	public boolean contains(Key key) {
		return get(key) != null;
	}

	/**
	 * Returns the value associated with the given key.
	 * 
	 * @param key
	 *            the key
	 * @return the value associated with the given key if the key is in the
	 *         symbol table and <tt>null</tt> if the key is not in the symbol
	 *         table
	 */
	public Value get(Key key) {
		for (Node x = first; x != null; x = x.next) {
			if (key.equals(x.key))
				return x.val;
		}
		return null;
	}

	/**
	 * Inserts the key-value pair into the symbol table, overwriting the old
	 * value with the new value if the key is already in the symbol table. If
	 * the value is <tt>null</tt>, this effectively deletes the key from the
	 * symbol table.
	 * 
	 * @param key
	 *            the key
	 * @param val
	 *            the value
	 */
	public void put(Key key, Value val) {
		if (val == null) {
			delete(key);
			return;
		}
		for (Node x = first; x != null; x = x.next)
			if (key.equals(x.key)) {
				x.val = val;
				return;
			}
		first = new Node(key, val, first);
		N++;
	}

	/**
	 * Removes the key and associated value from the symbol table (if the key is
	 * in the symbol table).
	 * 
	 * @param key
	 *            the key
	 */
	public void delete(Key key) {
		first = delete(first, key);
	}

	// delete key in linked list beginning at Node x
	// warning: function call stack too large if table is large
	private Node delete(Node x, Key key) {
		if (x == null)
			return null;
		if (key.equals(x.key)) {
			N--;
			return x.next;
		}
		x.next = delete(x.next, key);
		return x;
	}

	/**
	 * Returns all keys in the symbol table as an <tt>Iterable</tt>. To iterate
	 * over all of the keys in the symbol table named <tt>st</tt>, use the
	 * foreach notation: <tt>for (Key key : st.keys())</tt>.
	 * 
	 * @return all keys in the sybol table as an <tt>Iterable</tt>
	 */
	public Iterable<Key> keys() {
		Queue<Key> queue = new Queue<Key>();
		for (Node x = first; x != null; x = x.next)
			queue.enqueue(x.key);
		return queue;
	}

}
