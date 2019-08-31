package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements IMessageListener {
    private final ChatClient client;
    private final String user;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public MessagePane(ChatClient client, String value){
        this.client = client;
        this.user = value;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(e -> {
            try {
                String text = inputField.getText();
                client.msg(user, text);
                listModel.addElement("You: " + text);
                inputField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msg) {
        String line  = fromLogin + ": " + msg;
        listModel.addElement(line);
    }
}
