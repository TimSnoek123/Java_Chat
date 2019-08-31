package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements IUserStatusListener {

    private final ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);
        userListModel = new DefaultListModel<>();
        this.userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1){
                    String value = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, value);

                    JFrame frame = new JFrame("Message: " + value);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(500, 500);
                    frame.getContentPane().add(messagePane, BorderLayout.CENTER);
                    frame.setVisible(true);
                }
            }
        });

    }

    @Override
    public void online(String userName) {
        userListModel.addElement(userName);
    }

    @Override
    public void offline(String userName) {
        userListModel.removeElement(userName);
    }
}
