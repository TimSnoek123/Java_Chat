package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class MessageCommand implements ICommand {

    private final ServerWorker serverWorker;

    public MessageCommand(ServerWorker serverWorker){
        this.serverWorker = serverWorker;
    }

    @Override
    public void execute() throws IOException {
        String[] tokens = StringUtils.split(serverWorker.getLine(),null, 3);

        String userToSendMessageTo = tokens[1];
        String msg = tokens[2];

        List<ServerWorker> workerList = serverWorker.getServer().getWorkerList();

        for (ServerWorker worker: workerList) {
            if (worker.getLogin() != null && worker.getLogin().equalsIgnoreCase(userToSendMessageTo)){
                worker.send("receivedMsg " + serverWorker.getLogin() + " " + msg + "\n");
            }
        }
    }
}
