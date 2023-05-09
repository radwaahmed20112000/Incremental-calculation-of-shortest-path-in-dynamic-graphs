package org.example;

import jdk.dynalink.Operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;

public class Graph implements BatchProcessing{

    private class Operation{
        String op;
        int src;
        int des;

        Operation(String op, int src, int des){
            this.op = op;
            this.src = src;
            this.des = des;
        }
    }
    private Map<Integer, Set<Integer>> graph;
    final static private String INIT_GRAPH_TERM = "S";
    final static private String TERM_BATCH = "F";
    final static private String QUERY = "Q";
    final static private String ADD = "A";
    final static private String DELETE = "D";

    public Graph(){}

    public Graph(String initFilePath){

        this.graph = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(initFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(INIT_GRAPH_TERM)) break;

                String[] args = line.split(" ");
                Integer src = Integer.parseInt(args[0]);
                Integer des = Integer.parseInt(args[1]);

                if (graph.containsKey(src)){
                    graph.get(src).add(des);
                }else {
                    Set<Integer> adj = new HashSet<>();
                    adj.add(des);
                    graph.put(src, adj);
                }

                if (!graph.containsKey(des)){
                    Set<Integer> adj = new HashSet<>();
                    graph.put(des, adj);
                }
            }
            System.out.println("Done initializing graph with " + graph.size() + " nodes.");
        } catch (IOException e) {
            System.err.println("Error reading graph initialization file: " + e.getMessage());
        }
    }
    @Override
    public List<Integer> processBatch(String batchQuery) throws RemoteException {
        System.out.println("start processBatch");
        String[] queries = batchQuery.split("\n");

        List<Operation> operations = new LinkedList<Operation>();

        for (String query:queries) {
            String[] args = query.split(" ");
            if(args.length == 3 && isValidOp(args[0])){
                operations.add(new Operation(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            }
            else if (args.length == 1 && args[0].equals(TERM_BATCH)) {
                break;
            }
        }
        return execBatch(operations);
    }

    private boolean isValidOp(String op){
        return op.equals(QUERY) || op.equals(ADD) || op.equals(DELETE);
    }
    private List<Integer> execBatch(List<Operation> operations){
        //TODO: optimize
        List<Integer> res = new LinkedList<>();
        for(Operation op : operations){
            switch (op.op) {
                case QUERY:
                    res.add(shortestPath(op.src, op.des));
                    break;
                case ADD:
                    addEdge(op.src, op.des);
                    break;
                case DELETE:
                    removeEdge(op.src, op.des);
                    break;
                default:
                    System.out.println("Invalid operation: " + op);
            }
        }
       return res;
    }

    private void addEdge(int u, int v) {
        Set<Integer> edges = graph.get(u);
        if (edges == null) {
            edges = new HashSet<>();
            graph.put(u, edges);
        }
        edges.add(v);
    }

    private void removeEdge(int u, int v) {
        Set<Integer> edges = graph.get(u);
        if (edges != null) {
            edges.remove(v);
        }
    }

    private int shortestPath(int u, int v) {
        Map<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<Integer>();
        Set<Integer> visited = new HashSet<>();

        visited.add(u);
        distance.put(u, 0);
        queue.add(u);

        // bfs Algorithm
        while (!queue.isEmpty()) {
            int curr = queue.remove();
            for (int neighbor : graph.getOrDefault(curr, Collections.emptySet())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distance.put(neighbor, distance.get(curr) + 1);
                    if (neighbor == v) // stopping condition
                        return distance.get(v);
                    queue.add(neighbor);
                }
            }
        }
        return -1;  // no path
    }

}
