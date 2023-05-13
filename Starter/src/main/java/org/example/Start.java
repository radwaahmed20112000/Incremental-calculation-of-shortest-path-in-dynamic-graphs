package org.example;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Start {
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream("system.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the values of the properties
        String server = props.getProperty("GSP.server");
        int rmiPort = Integer.parseInt(props.getProperty("GSP.rmiregistry.port"));

        int clientsNumber = Integer.parseInt(props.getProperty("GSP.numberOfnodes"));
        String[] clients = new String[clientsNumber];

        for (int i = 0; i < clientsNumber; i++)
            clients[i] = props.getProperty("GSP.node" + i);



    }
}