package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<IUserStatusListener> userStatusListeners;
    private ArrayList<IMessageListener> messageListeners;

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.userStatusListeners = new ArrayList<>();
        this.messageListeners = new ArrayList<>();
    }

    public void msg(String sendTo, String msg) throws IOException {
        String command = "privateMsg " + sendTo + " " + msg + "\n";
        serverOut.write(command.getBytes());
        serverOut.flush();
    }

    public void logoff() throws IOException{
        String command = "logoff\n";
        serverOut.write(command.getBytes());
    }


    public boolean tryLogin(String userName, String password) throws IOException {
        String command = "login " + userName + " " + password + "\n";
        serverOut.write(command.getBytes());

        String response = bufferedIn.readLine();
        System.out.println(response);

        if  ("ok login".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        }
        else{
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread(() -> readMessageLoop());
        t.start();
    }

    private void readMessageLoop()  {
        try{
            String line;
            while ((line = bufferedIn.readLine()) != null){
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);   
                    }
                    else if ("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    }
                    else if ("receivedMsg".equalsIgnoreCase(cmd)){
                        String[] tokenMsg = StringUtils.split(line,null, 3);
                        handleMessage(tokenMsg);
                    }
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleMessage(String[] tokens) {
       String sendFrom = tokens[1];
       String message = tokens[2];

        for (IMessageListener messageListener: messageListeners) {
            messageListener.onMessage(sendFrom, message);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];

        for(IUserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];

        for(IUserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    public boolean tryConnect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(IUserStatusListener listener){
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(IUserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(IMessageListener messageListener){ messageListeners.add(messageListener); }
    public void removeMessageListener(IMessageListener messageListener) {messageListeners.add(messageListener); }
}
