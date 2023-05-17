package org.example;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public Server() {}

    public static void main(String args[]) {

        try {
<<<<<<< HEAD
            System.setProperty("java.rmi.server.hostname", "192.168.1.14");
            Graph obj = new Graph("init_graph.txt");
=======
            Logger logger = new Logger("logs");
            System.setProperty("java.rmi.server.hostname","192.168.1.12");
            Graph obj = new Graph("init_graph.txt", logger);
>>>>>>> fcea6da7f826ecb9d694972e43a01025131c47e0
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
