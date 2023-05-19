package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BatchProcessing extends Remote {

    List<Integer> processBatch(String clientID, String query) throws RemoteException;
}