package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private String[] tokens;
    private String line;

    public String getLine() {
        return line;
    }

    public String[] getTokens() {
        return tokens;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    private final Server server;

    public Server getServer() {
        return server;
    }

    private Menu menu;


    private String login = null;

    public void setLogin(String login) throws Exception {
        if (this.login != null) {
            throw new Exception("Can't change login, cause a user is already logged in!");
        }
        this.login = login;
    }

    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    private HashSet<String> topicSet;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.topicSet = new HashSet<>();
        initializeMenu();
    }

    private void initializeMenu() {
        this.menu = new Menu();
        menu.setCommand("login", new LoginCommand(this));
        menu.setCommand("logoff", new LogoffCommand(this));
        menu.setCommand("privateMsg", new MessageCommand(this));
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            System.out.println("User closed application");
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while ((this.line = reader.readLine()) != null) {
            this.tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {

                String cmd = tokens[0];
                menu.runCommand(cmd);
            }
        }

        clientSocket.close();
    }

    public String getLogin () {
        return login;
    }


    public void send (String msg) throws IOException {
        if (login != null) {
                outputStream.write(msg.getBytes());
        }
    }

}

