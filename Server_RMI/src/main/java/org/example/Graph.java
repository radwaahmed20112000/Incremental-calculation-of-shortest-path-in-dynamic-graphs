package org.example;

import jdk.dynalink.Operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Graph implements BatchProcessing{

    private class Operation{
        String operation;
        int src;
        int des;

        Operation(String operation, int src, int des){
            this.operation = operation;
            this.src = src;
            this.des = des;
        }
    }
    private class ReadThread extends Thread{
        private int src;
        private int des;
        private int result;

        ReadThread(int src, int des){
            this.src = src;
            this.des = des;
        }
        public void run() {
            this.result = shortestPath(this.src, this.des);
        }

        public int getResult() {
            return result;
        }
    }
    private Map<Integer, Set<Integer>> graph;
    final static private String INIT_GRAPH_TERM = "S";
    final static private String TERM_BATCH = "F";
    final static private String QUERY = "Q";
    final static private String ADD = "A";
    final static private String DELETE = "D";

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

    /**
     *
     * @param batchQuery
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Integer> processBatch(String batchQuery) throws RemoteException {
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
        return execBatch(operations);
//        try {
//            return execBatchOptimized(operations);
//        } catch (InterruptedException e) {
//            return new LinkedList<>();
//        }
    }

    private boolean isValidOp(String op){
        return op.equals(QUERY) || op.equals(ADD) || op.equals(DELETE);
    }
    private synchronized List<Integer> execBatch(List<Operation> operations){
        List<Integer> res = new LinkedList<>();
        for(Operation op : operations){
            switch (op.operation) {
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

    private synchronized List<Integer> execBatchOptimized(List<Operation> operations) throws InterruptedException {
        List<Integer> res = new LinkedList<>();

        ExecutorService executor = Executors.newFixedThreadPool(200);

        ListIterator<Operation> iterator = operations.listIterator();
        while (iterator.hasNext()) {
            Operation op = iterator.next();
            switch (op.operation) {
                case QUERY:
                    Queue<ReadThread> threadQueue = new LinkedList<>();
                    ReadThread t = new ReadThread(op.src, op.des);
//                    t.start();
                    executor.submit(t);
                    threadQueue.add(t);

                    while(iterator.hasNext()){
                        op = iterator.next();
                        if (!op.operation.equals(QUERY)){
                            iterator.previous();
                            break;
                        }
                        t = new ReadThread(op.src, op.des);
                        executor.submit(t);
                        threadQueue.add(t);
                    }
                    int nActiveThreads = Thread.activeCount();
                    System.out.println("There are " + nActiveThreads + " active threads.");
                    while (!threadQueue.isEmpty()){
                        t = threadQueue.remove();
                        t.join();
                        res.add(t.getResult());
                    }
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
