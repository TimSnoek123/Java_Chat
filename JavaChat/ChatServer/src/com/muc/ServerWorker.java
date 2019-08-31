package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.topicSet = new HashSet<>();
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null){
           String[] tokens = StringUtils.split(line);
           if (tokens != null && tokens.length > 0){
                String cmd = tokens[0];
                if ("logoff".equalsIgnoreCase(line)){
                    handleLogOff();
                    break;
                }
                else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                }
                else if ("privateMsg".equalsIgnoreCase(cmd)){
                    System.out.println("Sending private msg");
                    String[] tokenMsg = StringUtils.split(line,null, 3);
                    handlePrivateMsg(tokenMsg);
                }
                else if ("join".equalsIgnoreCase(cmd)){
                    handleJoin(tokens);
                }
                else{
                    outputStream.write(("Unknown " + cmd + "\n").getBytes());
                }
                String msg = "You typed: " + line + "\n";
                outputStream.write(msg.getBytes());
           }
        }

        clientSocket.close();
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length > 1){
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    //format: "privateMsg" "handlePrivateMsg" msg
    private void handlePrivateMsg(String[] tokens) throws IOException {
        String userToSendMessageTo = tokens[1];
        String msg = tokens[2];

        List<ServerWorker> workerList = server.getWorkerList();

        for (ServerWorker worker: workerList) {
            if (worker.getLogin() != null && worker.getLogin().equalsIgnoreCase(userToSendMessageTo)){
                worker.send("receivedMsg " + login + " " + msg + "\n");
            }
        }
    }

    private void handleLogOff() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();

        for (ServerWorker worker: workerList) {
            if (worker.getLogin() != null && !worker.getLogin().equals(login)){
                worker.send("offline " + login + "\n");
            }
        }

        server.removeWorker(this);
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if (login.equals("guest") && password.equals("guest") || login.equals("test") && password.equals("test")){
                String msg  = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                //send current user all other online logins
                for (ServerWorker worker: workerList) {
                        if (worker.getLogin() != null){
                            if (!login.equals(worker.getLogin())){
                                String onlineMsg = "online " + worker.getLogin() + "\n";
                                send(onlineMsg);
                            }
                        }
                }

                //send other online users current user's status
                for (ServerWorker worker: workerList) {
                        String onlineMsg = "online " + login + "\n";
                        worker.send(onlineMsg);
                }
            }
            else{
                String msg  = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for: " + login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null){
            outputStream.write(msg.getBytes());
        }
    }

}
