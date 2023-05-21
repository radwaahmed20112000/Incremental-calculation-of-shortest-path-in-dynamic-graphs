package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class Graph implements BatchProcessing{

    private static class Operation{
        String operation;
        int src;
        int des;

        Operation(String operation, int src, int des){
            this.operation = operation;
            this.src = src;
            this.des = des;
        }
    }
    private final Map<Integer, Set<Integer>> graph;
    private final Logger logger;
    final static private String INIT_GRAPH_TERM = "S";
    final static private String TERM_BATCH = "F";
    final static private String QUERY = "Q";
    final static private String ADD = "A";
    final static private String DELETE = "D";

    public Graph(String initFilePath, Logger logger){
        this.logger = logger;
        logger.log("Start Graph Initialization:");

        this.graph = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(initFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(INIT_GRAPH_TERM)) break;

                String[] args = line.split(" ");
                int src = Integer.parseInt(args[0]);
                int des = Integer.parseInt(args[1]);

                addEdge(src, des);
            }

            logger.log(String.format("Done initializing graph with %d nodes.", graph.size()));
            logger.log("R");
        } catch (IOException e) {
            logger.log(String.format("Error reading graph initialization file: %s", e.getMessage()));
        }
    }

    /**
     * Processes a batch of operations (Query/Add/Delete) to be performed on the graph.
     * @param batchQuery a single batch sent by the client via rmi
     * @return list of integers representing query results in the same order as in batchQuery
     */
    @Override
    public List<Integer> processBatch(String clientID, String batchQuery) throws RemoteException {
        String[] queries = batchQuery.split("\n");

        List<Operation> operations = new LinkedList<>();

        for (String query:queries) {
            String[] args = query.split(" ");
            if(args.length == 3 && isValidOp(args[0])){
                operations.add(new Operation(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            }
            else if (args.length == 1 && args[0].equals(TERM_BATCH)) {
                break;
            }
        }
        return execBatch(clientID, operations);
    }

    private boolean isValidOp(String op){
        return op.equals(QUERY) || op.equals(ADD) || op.equals(DELETE);
    }
// 2253 ms
    private synchronized List<Integer> execBatch(String clientID, List<Operation> operations){
        logger.log("Client " + clientID);
        long startTime = System.currentTimeMillis();
        List<Integer> res = new LinkedList<>();
        for(Operation op : operations){
            switch (op.operation) {
                case QUERY:
                    res.add(shortestPath(op.src, op.des)); // TODO: change here *********************
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
        long duration = System.currentTimeMillis() - startTime;
        logger.log(String.format("batch processed within %d ms", duration));
        return res;
    }

    private void addEdge(int u, int v) {
        Set<Integer> edges = graph.computeIfAbsent(u, k -> new HashSet<>());
        edges.add(v);

        graph.computeIfAbsent(v, k -> new HashSet<>());

        logger.logWithTimestamp(String.format("Add edge %d -> %d", u, v));
    }

    private void removeEdge(int u, int v) {
        Set<Integer> edges = graph.get(u);
        if (edges != null) {
            edges.remove(v);
        }
        logger.logWithTimestamp(String.format("Remove edge between %d and %d", u, v));
    }

    public int shortestPath(int u, int v) {
        Map<Integer, Integer> distance = new HashMap<>();
        distance.put(u, 0);

        // Relax edges n-1 times
        for (int i = 1; i < graph.size(); i++) {
            for (int node : graph.keySet()) {
                for (int neighbor : graph.get(node)) {
                    if (distance.containsKey(node) && !distance.containsKey(neighbor)) {
                        distance.put(neighbor, distance.get(node) + 1);
                    }
                }
            }
        }

        return distance.getOrDefault(v, -1);
    }

    private int optimizedShortestPath(int u, int v) {
        Map<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
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
