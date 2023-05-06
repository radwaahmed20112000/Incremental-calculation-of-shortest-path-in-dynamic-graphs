package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;

public class Graph implements BatchProcessing{
    final static private String INIT_GRAPH_TERM = "S";
    final static private String QUERY = "Q";
    final static private String ADD = "A";
    final static private String DELETE = "D";

    public Graph(){}

    public Graph(String initFilePath){

        Map<Integer, Set<Integer>> graph = new HashMap<>();

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
            //TODO: initialize the actual graph
        } catch (IOException e) {
            System.err.println("Error reading graph initialization file: " + e.getMessage());
        }
    }
    @Override
    public List<Integer> processBatch(String batchQuery) throws RemoteException {
        System.out.println("start processBatch");
        String[] queries = batchQuery.split("\n");

        List<Integer> result = new LinkedList<Integer>();

        for (String query:queries) {
            String[] args = query.split(" ");
            //type = args[0]
            //start = args[1]
            //end = args[2]
            if (args[0].equals(QUERY)){
                result.add(5);
            }
        }

        //TODO: process query in an optimized way

        return result;
    }

}
