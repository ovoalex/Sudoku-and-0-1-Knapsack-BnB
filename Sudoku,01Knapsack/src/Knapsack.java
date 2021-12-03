import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Knapsack {
    private static Scanner contents;

    private int capacity;
    private int counter = 1;
    private int itemAmount;
    private int achievable = 0;
    private Node mostProfit = new Node();

    private int[][] items;
    private double[] ratios;
    private ArrayList<Node> nodes = new ArrayList<Node>();

    private class Node {
        private int id;
        private ArrayList<Integer> items;
        private int level;
        private int profit;
        private int weight;
        private double bound;
        private ArrayList<Integer> omitted;
        private int parent;

        public Node() {
            this.id = 0;
            this.items = new ArrayList<Integer>();
            this.level = 0;
            this.profit = 0;
            this.weight = 0;
            this.bound = 0;
            this.omitted = new ArrayList<Integer>();
            this.parent = 0;
        }

        @Override
        public String toString() {
            return "<Node " + id + ": { items: " + items + " -- level: " + level +
                   " -- profit: " + profit + " -- weight: " + weight +
                   " -- bound: " + bound + " } >";
        }
    }

    public static void main(String[] args) {
        Knapsack project = new Knapsack();
        project.run();
    }

    private void run() {
        printHeader();
        getFile();
        verifyContents();
        printItems();
        processTree();
    }

    private void printHeader() {
        System.out.println("0/1 knapsack problem ");
        System.out.println("Using the Best-First search with Branch-and-Bound algorithm.\n");
    }

    private void getFile() {
        Scanner console = new Scanner(System.in);
        String filename;
        boolean isValid;
        
        System.out.print("Please input the filename: ");
        filename = console.nextLine();

        do {
            try {
                contents = new Scanner(new File(filename));

                // only executes if contents doesn't throw FNF
                isValid = true;
            }
            catch (FileNotFoundException e) {
                isValid = false;

                System.out.println("");
                System.out.print("File not found. Please input a valid filename: ");
                filename = console.nextLine();
            }
        }
        while (!isValid);
    }

    private void verifyContents() {
    	Scanner sc = new Scanner(System.in);
        System.out.print("Please input knapsack capacity: ");
        //capacity = sc.nextInt();
        try {
            String buffer;
            //buffer = contents.nextLine();
            capacity = sc.nextInt();

            buffer = contents.nextLine();
            itemAmount = Integer.parseInt(buffer);

            items = new int[itemAmount][2];
            ratios = new double[itemAmount];

            for (int i = 0; i < itemAmount; i++) {

                buffer = contents.nextLine();
                String[] tokens = buffer.split("\\s");

                items[i][0] = Integer.parseInt(tokens[0]);
                items[i][1] = Integer.parseInt(tokens[1]);

                ratios[i] = items[i][0] / (double)items[i][1];
            }
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException |
               NoSuchElementException ex) {

            System.out.println("");
            System.out.println("Invalid file contents. Restarting...");
            System.out.println("");

            getFile();
            verifyContents();
        }
    }

    //print items values and capacity of knapsack
    private void printItems() {
        if (items != null) {
            System.out.println("");
            System.out.println("Capacity of knapsack is " + capacity);
            System.out.println("Items are:");

            for (int i = 0; i < items.length; i++) {
                System.out.println((i+1) + ": " + items[i][0] + "  " + items[i][1]);
            }

            System.out.println("");
        }
    }

    private void processTree() {
        Node first = new Node();
        first.id = counter++;
        first.bound = getBound(first);

        processNode(first);

        while (nodes.size() > 0) {
            processNode(getPriorityNode());
        }

        System.out.println("The most profitable is " + mostProfit);
    }

    private void processNode(Node current) {
        System.out.println("Exploring " + current);

        if (current.bound < achievable) {
            System.out.println("           achievable profit " + achievable +
                               " is greater than current bound " + current.bound);
        }
        else if (current.level == items.length) {
            System.out.println("    complete - lowest level of tree");
            System.out.println("    achievable profit of " + current.profit);
            if (achievable < current.profit) {
                achievable = current.profit;
            }
        }
        else {
            branchLeft(current);
            branchRight(current);
        }

        nodes.remove(current);

        System.out.println("");
    }

    private void branchLeft(Node current) {
        Node left = new Node();

        left.id = counter++;
        left.items = new ArrayList<Integer>(current.items);
        left.level = current.level + 1;
        left.profit = current.profit;
        left.weight = current.weight;
        left.omitted = new ArrayList<Integer>(current.omitted);
        left.omitted.add(current.level + 1);
        left.bound = getBound(left);
        left.parent = current.id;

        nodes.add(left);

        System.out.println("    Left child is " + left);
        

        if (left.profit > mostProfit.profit) {
            mostProfit = left;
        }
    }

    private void branchRight(Node current) {
        Node right = new Node();

        right.id = counter++;
        right.items = new ArrayList<Integer>(current.items);
        right.items.add(current.level + 1);
        right.level = current.level + 1;
        right.profit = current.profit + items[current.level][0];
        right.weight = current.weight + items[current.level][1];
        right.omitted = new ArrayList<Integer>(current.omitted);
        right.bound = getBound(right);
        right.parent = current.id;

        System.out.println("    Right child is " + right);

        if (right.weight > capacity) {
            System.out.println("        pruned -- current weight exceeds capacity");
        }
        else {
            if (right.weight == capacity) {
                System.out.println("        complete -- current node is at capacity weight");
            }
            else {
                
                nodes.add(right);
            }

            System.out.println("        note achievable profit of " + right.profit);
            if (achievable < right.profit) {
                achievable = right.profit;
            }

            if (right.profit > mostProfit.profit) {
                mostProfit = right;
            }
        }
    }

    private double getBound(Node current) {
        int remainingWeight = capacity;
        double sum = 0;

        for (int i = 0; i < items.length; i++) {
            if (!current.omitted.contains(i+1)) {
                if (remainingWeight > items[i][1]) {
                    sum += items[i][0];
                    remainingWeight -= items[i][1];
                }
                else {
                    sum += ratios[i] * remainingWeight;
                    break;
                }
            }
        }

        if (current.profit > sum) {
            return current.profit;
        }
        else {
            return sum;
        }
    }

    private Node getPriorityNode() {
        double best = 0;
        Node buffer = null;

        for (Node current : nodes) {
            if (current.bound > best) {
                buffer = current;
                best = current.bound;
            }
        }

        return buffer;
    }
}