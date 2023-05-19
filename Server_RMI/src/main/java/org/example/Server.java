package org.example;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public Server() {}

    public static void main(String args[]) {

        try {

            Logger logger = new Logger("logs");
            System.setProperty("java.rmi.server.hostname", args[0]);
            Graph obj = new Graph("init_graph.txt", logger);
            BatchProcessing stub = (BatchProcessing) UnicastRemoteObject.exportObject(obj, 0);
            System.out.println("Start Server . . .");

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[1]));
            registry.bind("BatchProcessing", stub);

            System.out.println("Server ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
