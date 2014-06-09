package com.github.skhatri;

import java.io.File;

public class App {

    public static void main(String[] args) {
        CommandRepository commandRepo = new CommandRepository(new File("src/main/resources/commands.json"));
        OpenerFrame opener = new OpenerFrame(commandRepo);
        opener.setSize(840, 375);
        opener.perform();
        opener.pack();
        opener.setVisible(true);
    }
}
