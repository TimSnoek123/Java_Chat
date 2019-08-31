package com.muc;

import java.io.IOException;
import java.util.List;

public class LogoffCommand implements ICommand {

    private final ServerWorker serverWorker;

    public LogoffCommand(ServerWorker serverWorker){
        this.serverWorker = serverWorker;
    }

    @Override
    public void execute() throws IOException {
        List<ServerWorker> workerList = serverWorker.getServer().getWorkerList();

        for (ServerWorker worker: workerList) {
            if (serverWorker.getLogin() != null && !serverWorker.getLogin().equals(serverWorker.getLogin())){
                serverWorker.send("offline " + serverWorker.getLogin() + "\n");
            }
        }

        serverWorker.getServer().removeWorker(serverWorker);
        serverWorker.getClientSocket().close();
    }
}
