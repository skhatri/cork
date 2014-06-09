package com.github.skhatri;

import java.io.File;

public class App {

    private final static String CP = "classpath:";

    public static void main(String[] args) {
        String fileName = CP + "commands.json";
        if (args.length != 0) {
            fileName = args[0];
        }
        String resource = fileName.startsWith(CP) ? App.class.getClassLoader()
                .getResource(fileName.substring(CP.length()))
                .getFile() : fileName;
        CommandRepository commandRepo = new CommandRepository(new File(resource));
        OpenerFrame opener = new OpenerFrame(commandRepo);
        opener.setSize(840, 475);
        opener.perform();
        opener.setVisible(true);
    }
}
