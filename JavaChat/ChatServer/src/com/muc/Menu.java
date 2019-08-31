package com.muc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Menu {
    Map<String, ICommand> menuItems;

    public Menu() {
        menuItems = new HashMap();
    }

    public void setCommand(String operation, ICommand command) {
        menuItems.put(operation, command);
    }

    public void runCommand(String operation) throws IOException {
           menuItems.get(operation).execute();
    }
 }

