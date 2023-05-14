package org.example;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public Server() {}

    public static void main(String args[]) {

        try {
            System.setProperty("java.rmi.server.hostname", "192.168.1.14");
            Graph obj = new Graph("init_graph.txt");
            BatchProcessing stub = (BatchProcessing) UnicastRemoteObject.exportObject(obj, 0);
            System.out.println("Start Server . . .");

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("BatchProcessing", stub);

            System.out.println("Server ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
