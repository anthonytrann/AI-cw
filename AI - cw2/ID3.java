// ECS629/759 Assignment 2 - ID3 Skeleton Code
// Author: Simon Dixon

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

class ID3 {

	/** Each node of the tree contains either the attribute number (for non-leaf
	*  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
	*  tree nodes in <b>children</b> containing each of the children of the
	*  node (for non-leaf nodes).
	*  The attribute number corresponds to the column number in the training
	*  and test files. The children are ordered in the same order as the
	*  Strings in strings[][]. E.g., if value == 3, then the array of
	*  children correspond to the branches for attribute 3 (named data[0][3]):
	*      children[0] is the branch for attribute 3 == strings[3][0]
	*      children[1] is the branch for attribute 3 == strings[3][1]
	*      children[2] is the branch for attribute 3 == strings[3][2]
	*      etc.
	*  The class number (leaf nodes) also corresponds to the order of classes
	*  in strings[][]. For example, a leaf with value == 3 corresponds
	*  to the class label strings[attributes-1][3].
	**/
	class TreeNode {

		TreeNode[] children;
		int value;

		public TreeNode(TreeNode[] ch, int val) {
			value = val;
			children = ch;
		} // constructor

		public String toString() {
			return toString("");
		} // toString()

		String toString(String indent) {
			if (children != null) {
				String s = "";
				for (int i = 0; i < children.length; i++)
				s += indent + data[0][value] + "=" +
				strings[value][i] + "\n" +
				children[i].toString(indent + '\t');
				return s;
			} else
			return indent + "Class: " + strings[attributes-1][value] + "\n";
		} // toString(String)

	} // inner class TreeNode

	private int attributes; 	// Number of attributes (including the class)
	private int examples;		// Number of training examples
	private TreeNode decisionTree;	// Tree learnt in training, used for classifying
	private String[][] data;	// Training data indexed by example, attribute
	private String[][] strings; // Unique strings for each attribute
	private int[] stringCount;  // Number of unique strings for each attribute


	public ID3() {
		attributes = 0;
		examples = 0;
		decisionTree = null;
		data = null;
		strings = null;
		stringCount = null;
	} // constructor

	public void printTree() {
		if (decisionTree == null)
		error("Attempted to print null Tree");
		else
		System.out.println(decisionTree);
	} // printTree()

	/** Print error message and exit. **/
	static void error(String msg) {
		System.err.println("Error: " + msg);
		System.exit(1);
	} // error()

	static final double LOG2 = Math.log(2.0);

	static double xlogx(double x) {
		return x == 0? 0: x * Math.log(x) / LOG2;
	} // xlogx()

	/** Execute the decision tree on the given examples in testData, and print
	*  the resulting class names, one to a line, for each example in testData.
	**/
	public void classify(String[][] testData) {
		if (decisionTree == null)
		error("Please run training phase before classification");
		// PUT  YOUR CODE HERE FOR CLASSIFICATION

		// This loop goes through each of the row in the testData. The row and the  TreeNode decisionTree (which is
		// created from the train() method and the trainingData) is passed through the traverse() method. This method
		// traverses through the tree recursivly which then prints the row's corresponding class value (e.g. 'Yes' or
		// 'No' if the class column contains the just those 'Yes' or 'No' values).
		for(int i = 1; i< testData.length; i++){
			traverse(testData[i], decisionTree);
		}
	} // classify()

	public void train(String[][] trainingData) {
		indexStrings(trainingData);

		// Initialises the TreeNode with dummy values
		decisionTree = new TreeNode(null, -1);

		// creates a list to keep track of the attributes used for the tree
		List<String> tracker = new ArrayList<>();

		trainingRecursion(trainingData, decisionTree, tracker);
	} // train()


	/** Given a 2-dimensional array containing the training data, numbers each
	*  unique value that each attribute has, and stores these Strings in
	*  instance variables; for example, for attribute 2, its first value
	*  would be stored in strings[2][0], its second value in strings[2][1],
	*  and so on; and the number of different values in stringCount[2].
	**/

	private void traverse(String[] row, TreeNode tree){

		// Base case for the recursive method which checks if the position in the tree is at the leaf - this would
		// mean that the TreeNode will if have no children and the value of the TreeNode would contain the index of the
		// strings 2d array of that class value. 
		if(tree.children == null){
			String className = strings[strings.length-1][tree.value];
			System.out.println(className);
		} else {

			// gets the row from the strings 2d array that corresponds to the attribute number (the 'value' saved in the
			// TreeNode - which is just the index of the position in which the attribute is in the train data). This
			// just gives an array of strings of unique children of the attribute number
			String[] rowOfStrings = strings[tree.value];

			// gets the actual value from the attribute number
			String matcher = row[tree.value];

			// going through all the unique children to find the matching actual value and its corresponding index value
			// then recursive call to itself which the treeNode of the correspoding child  is passed along as the
			// arugment - simulating going down the treeNode
			for (int i = 0; i < stringCount[tree.value]; i++) {
				if(matcher.equals(rowOfStrings[i]))
					traverse(row, tree.children[i]);
			}
		}

	}

	private void trainingRecursion(String[][] trainingData, TreeNode decisionTree, List<String> tracker){
		
		// This if-statement checks if the Class attribute/column in the current trainingData have all the same
		// name/value and if it does then it checks for the index number of the class name and saves this index value
		// to the node
		if(allSameAnswer(trainingData)){

			String classValue = trainingData[1][trainingData[0].length-1];

			for(int i = 0; i < strings[0].length; i++){
				if(classValue.equals(strings[trainingData[0].length-1][i])){
					decisionTree.value = i;
					break;
				}
			}
		} else {

			// The call to the attributeGain() method returns the attribute number the corresponds to the attribute with
			// the highest gain-value.
			int attributeNumber = attributeGain(trainingData, tracker);

			// attribute number is -1 if there is no more questions/attributes to ask
			if(attributeNumber == -1){

				// gets the actual value of the majority value
				String val = majorityClass(trainingData);

				for(int i = 0; i < strings[0].length; i++){
					if(val.equals(strings[trainingData[0].length-1][i])){
						decisionTree.value = i;
						break;
					}
				}
			} else {
				ArrayList<String[][]> listOfChildren = split(trainingData, attributeNumber);

				TreeNode[] children = new TreeNode[listOfChildren.size()];
				for(int i = 0; i<children.length; i++){
					children[i] = new TreeNode(null, -1);

				}

				decisionTree.children = children;
				decisionTree.value = attributeNumber;

				for(int i = 0; i < listOfChildren.size(); i++){
					List<String> track = new ArrayList<>();
					for(String attri : tracker){
						track.add(attri);
					}
					trainingRecursion(listOfChildren.get(i), children[i], track);
				}
			}
		}
	}

	// Checks if the class children of the class attribute is the same
	private boolean allSameAnswer(String[][] trainingData){
		String firstValue = trainingData[1][trainingData[0].length-1];
		for(int i=1; i<trainingData.length; i++){
			if(!firstValue.equals(trainingData[i][trainingData[0].length-1])){
				return false;
			}
		}
		return true;
	}


	// This method calculates the gain for of the attributes and returns the index of the attribute with the highest
	// gain
	private int attributeGain(String[][] trainingData, List<String> tracker){

		// Creates an arraylist for the class attribute
		ArrayList<String> classAttributeArraylist = attributeToArraylist(trainingData, trainingData[0].length - 1);

		// An arraylist gets the class column and saves it to an arraylist
		HashMap<String, Integer> classAttriHashTable = arraylistToHashMap(classAttributeArraylist);

		// gets the Entropy for the class Attribute column using the class-arraylist
		double classEntropy = 0;
		double classLength = (double)classAttributeArraylist.size();
		for(String key : classAttriHashTable.keySet()){
			classEntropy += -xlogx(classAttriHashTable.get(key)/classLength);
		}



		// this sets the highest attribute-gain out of the attributes
		double bestGainSoFar = Double.NEGATIVE_INFINITY;
		String bestAttritubeSoFar = "";

		// going through all the attributes from the trainingData and calculates the gain of that attribute, excluding
		// the class attribute column
		for(int i = 0; i < trainingData[0].length-1; i++){

			// checks if the attribute is already used/selected from the tracker list if not then the gain is calculated
			if(!tracker.contains(trainingData[0][i])){

				ArrayList<String> attributeArraylist = attributeToArraylist(trainingData, i);

				// calculates gain of the attribute by finding the entropy of that attribute (which returns a negative
				// number) and adding the answer to the entropy of the class attribute
				double attributeGain = classEntropy + findEntropy(attributeArraylist, classAttributeArraylist);

				// saves the attribute with the highest attribute gain
				if(attributeGain > bestGainSoFar){
					bestGainSoFar = attributeGain;
					bestAttritubeSoFar = trainingData[0][i];
				}
			}
		}

		// gets the corresponding attribute number (index from the training data) of the attribute with the highest
		// gain and returns the value
		for(int i = 0; i < trainingData[0].length; i++){
			if(trainingData[0][i].equals(bestAttritubeSoFar)){
				tracker.add(bestAttritubeSoFar);
				return i;
			}
		}

		// -1 is returned if you reach then end and there is no more attributes to ask
		return -1;

	}

	// Creates and returns an arraylist from the attribute column in the trainingData
	private ArrayList<String> attributeToArraylist(String[][] trainingData, int columnPosition){
		ArrayList<String> attributeColumn = new ArrayList<>();

		for(int i=1; i<trainingData.length; i++){
			attributeColumn.add(trainingData[i][columnPosition]);
		}

		return attributeColumn;
	}

	// Creates and returns a hash map which correponds to the arraylist it receives - counting the occurances of each
	// of the children
	private HashMap<String, Integer> arraylistToHashMap(ArrayList<String> arrayList){
		HashMap<String, Integer> childrenWithCount = new HashMap<>();

		for(int i = 0; i < arrayList.size(); i++){
			if(childrenWithCount.containsKey(arrayList.get(i))){
				childrenWithCount.put(arrayList.get(i), childrenWithCount.get(arrayList.get(i)) + 1);
			} else {
				childrenWithCount.put(arrayList.get(i), 1);
			}
		}

		return childrenWithCount;
	}


	// calculates the entropy of the attribute passed in the arugment
	private double findEntropy(ArrayList<String> attributeArraylist, ArrayList<String> classArraylist){

		double totalEntropy = 0;

		// gets a HashMap of the attribute passed in the argument - counting the number of occurances of the each of
		// the children
		HashMap<String, Integer> attributeCountHashMap = arraylistToHashMap(attributeArraylist);

		// loop through the children
		for(String attributeChild : attributeCountHashMap.keySet()){
			double entropy = 0;

			// creates a HashMap of the number of occurances of the class value with the corresponding child
			HashMap<String, Integer> classHashMapOfChild = new HashMap<>();

			for(int j = 0; j < attributeArraylist.size(); j++){
				if(attributeArraylist.get(j).equals(attributeChild)){
					if(classHashMapOfChild.containsKey(classArraylist.get(j))){
						classHashMapOfChild.put(classArraylist.get(j), classHashMapOfChild.get(classArraylist.get(j)) + 1);
					} else {
						classHashMapOfChild.put(classArraylist.get(j), 1);
					}
				}
			}

			// calculates the entropy of the corresponding child
			for(String classChild : classHashMapOfChild.keySet()){
				entropy += -xlogx((double)classHashMapOfChild.get(classChild)/(double)attributeCountHashMap.get(attributeChild));
			}

			// calculates the total entropy of all the children (entropy of the attribute)
			double attributeLength = (double)attributeArraylist.size();
			totalEntropy += -((double)attributeCountHashMap.get(attributeChild)/attributeLength)*entropy;

		}

		return totalEntropy;
	}

	// finds the class value which is the majority from the class-attribute column
	private String majorityClass(String[][] trainingData){

		// counts the occurances of the class values
		HashMap<String, Integer> mostClassHashMap = arraylistToHashMap(attributeToArraylist(trainingData, trainingData[0].length - 1));

		int mostSoFar = 0;
		String mostStringSoFar = "";

		// goes through the HashMap and finds the highest occurance
		for(String className: mostClassHashMap.keySet()){
			if(mostClassHashMap.get(className) > mostSoFar){
				mostSoFar = mostClassHashMap.get(className);
				mostStringSoFar = className;
			}
		}
		return mostStringSoFar;
	}

	//
	private ArrayList<String[][]> split(String[][] trainingData, int attributeNumber){
		ArrayList<String[][]> childSplit = new ArrayList<>();


		ArrayList<String> uniqueChildren = new ArrayList<>(Arrays.asList(strings[attributeNumber]));
		uniqueChildren.removeAll(Collections.singleton(null));

		for(String child : uniqueChildren){
			ArrayList<String[]> subset = new ArrayList<>();
			String[] headers = trainingData[0];

			subset.add(headers);
			for(int i = 1; i < trainingData.length; i++){
				if(trainingData[i][attributeNumber].equals(child)) {
					String[] childRow = trainingData[i];
					subset.add(childRow);
				}
			}

			String[][] subset2d = new String[subset.size()][subset.get(0).length];
			for(int i = 0; i <subset.size(); i++){
				subset2d[i] = subset.get(i);
			}
			childSplit.add(subset2d);
		}

		return childSplit;
	}

	void indexStrings(String[][] inputData) {
		data = inputData;
		examples = data.length;
		attributes = data[0].length;
		stringCount = new int[attributes];
		strings = new String[attributes][examples];// might not need all columns
		int index = 0;
		for (int attr = 0; attr < attributes; attr++) {
			stringCount[attr] = 0;
			for (int ex = 1; ex < examples; ex++) {
				for (index = 0; index < stringCount[attr]; index++)
				if (data[ex][attr].equals(strings[attr][index]))
				break;	// we've seen this String before
				if (index == stringCount[attr])		// if new String found
				strings[attr][stringCount[attr]++] = data[ex][attr];
			} // for each example
		} // for each attribute
	} // indexStrings()

	/** For debugging: prints the list of attribute values for each attribute
	*  and their index values.
	**/
	void printStrings() {
		for (int attr = 0; attr < attributes; attr++)
		for (int index = 0; index < stringCount[attr]; index++)
		System.out.println(data[0][attr] + " value " + index +
		" = " + strings[attr][index]);
	} // printStrings()

	/** Reads a text file containing a fixed number of comma-separated values
	*  on each line, and returns a two dimensional array of these values,
	*  indexed by line number and position in line.
	**/
	static String[][] parseCSV(String fileName)
	throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String s = br.readLine();
		int fields = 1;
		int index = 0;
		while ((index = s.indexOf(',', index) + 1) > 0)
		fields++;
		int lines = 1;
		while (br.readLine() != null)
		lines++;
		br.close();
		String[][] data = new String[lines][fields];
		Scanner sc = new Scanner(new File(fileName));
		sc.useDelimiter("[,\n]");
		for (int l = 0; l < lines; l++)
		for (int f = 0; f < fields; f++)
		if (sc.hasNext())
		data[l][f] = sc.next();
		else
		error("Scan error in " + fileName + " at " + l + ":" + f);
		sc.close();
		return data;
	} // parseCSV()

	public static void main(String[] args) throws FileNotFoundException,
	IOException {
		if (args.length != 2)
		error("Expected 2 arguments: file names of training and test data");
		String[][] trainingData = parseCSV(args[0]);
		String[][] testData = parseCSV(args[1]);
		ID3 classifier = new ID3();
		classifier.train(trainingData);
		classifier.printTree();
		classifier.classify(testData);
	} // main()

} // class ID3
