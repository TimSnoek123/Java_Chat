package com.muc;

import java.io.IOException;
import java.util.List;

public class LoginCommand implements ICommand {

    private ServerWorker serverWorker;

    public LoginCommand(ServerWorker serverWorker){
        this.serverWorker = serverWorker;
    }

    @Override
    public void execute() throws IOException {
        if (serverWorker.getTokens().length == 3) {
            String login = serverWorker.getTokens()[1];
            String password = serverWorker.getTokens()[2];

            if (login.equals("guest") && password.equals("guest") || login.equals("test") && password.equals("test")) {
                String msg = "ok login\n";
                serverWorker.getOutputStream().write(msg.getBytes());
                try {
                    serverWorker.setLogin(login);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("User logged in successfully: " + login);

                List<ServerWorker> workerList = serverWorker.getServer().getWorkerList();

                //send current user all other online logins
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String onlineMsg = "online " + worker.getLogin() + "\n";
                            serverWorker.send(onlineMsg);
                        }
                    }
                }

                //send other online users current user's status
                for (ServerWorker worker : workerList) {
                    String onlineMsg = "online " + login + "\n";
                    worker.send(onlineMsg);
                }
            } else {
                String msg = "error login\n";
                serverWorker.getOutputStream().write(msg.getBytes());
                System.err.println("Login failed for: " + login);
            }
        }
    }
}
