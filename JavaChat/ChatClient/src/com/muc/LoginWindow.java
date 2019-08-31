package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private final ChatClient client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginWindow(){
        super("Login");

        this.client = new ChatClient("localhost", 8818);
        client.tryConnect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(loginField);
        panel.add(passwordField);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            doLogin();
        });

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    private void doLogin() {
        String userName = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.tryLogin(userName, password)){
                setVisible(false);
                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);

                frame.getContentPane().add(new JScrollPane(userListPane), BorderLayout.CENTER);
                frame.setVisible(true);
            }
            else {
               JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}
