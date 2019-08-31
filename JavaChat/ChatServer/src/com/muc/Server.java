package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final int port;
    private ArrayList<ServerWorker> serverWorkers;

    public Server(int port) {
        this.port = port;
        this.serverWorkers = new ArrayList<>();

    }

    public List<ServerWorker> getWorkerList(){
        return serverWorkers;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                System.out.println("accepting client connection.....");
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                serverWorkers.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        serverWorkers.remove(serverWorker);
    }
}
