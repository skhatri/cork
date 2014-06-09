package com.github.skhatri;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OpenerFrame extends JFrame {

    private JLabel displayLabel;
    private JPanel display;
    private CommandRepository commandRepository;
    private JPanel processPanel;
    private JTextArea console;

    public OpenerFrame(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
        setTitle("Opener");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addProcessPanel();
        addDisplay();
    }

    private void addProcessPanel() {
        processPanel = new JPanel();
        processPanel.setSize(getContentPane().getWidth(), 50);
        processPanel.setLayout(new FlowLayout());
        getContentPane().add(processPanel);
    }

    private void addDisplay() {
        displayLabel = new JLabel("Helper");
        display = new JPanel();
        display.add(displayLabel);
        getContentPane().add(display);
    }

    public void perform() {
        Component panel = addData();
        Component options = addMenu();
        BorderLayout layoutManager = new BorderLayout();
        layoutManager.addLayoutComponent(display, BorderLayout.NORTH);
        layoutManager.addLayoutComponent(panel, BorderLayout.CENTER);
        layoutManager.addLayoutComponent(options, BorderLayout.WEST);
        layoutManager.addLayoutComponent(processPanel, BorderLayout.SOUTH);
        getContentPane().setLayout(layoutManager);
    }

    private Component addData() {
        console = new JTextArea(10, 50);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton clear = new JButton("clear");
        clear.addActionListener((e)->console.setText(""));
        panel.add(clear);
        panel.add(new JScrollPane(console, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        getContentPane().add(panel);
        return panel;
    }

    private Color anyColor() {
        Color[] color = {Color.PINK, Color.ORANGE, Color.GREEN, Color.RED, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.BLACK};
        int random = ((Double) (Math.random() * color.length)).intValue();
        return color[random];
    }

    private Component addMenu() {
        OnNewProcessListener processListener = (name, process) -> {
            JButton processBtn = new JButton("X " + name);
            processBtn.addActionListener((action) -> {
                process.destroy();
                processBtn.setVisible(false);
                processBtn.getParent().remove(processBtn);
            });
            processPanel.add(processBtn);
            processPanel.updateUI();
        };

        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

        Consumer<Map<String, Object>> bindUI = (command) -> {
            String label = Utils.toStr.apply(command.get("label"));
            JButton item = new JButton(label);
            item.setForeground(anyColor());
            item.setContentAreaFilled(false);
            item.addActionListener(event -> {
                displayLabel.setText(label + " command executed.");
                String[] args = {Utils.toStr.apply(command.get("args"))};
                String wd = Utils.toStr.apply(command.get("wd"));
                List<String> env = Utils.toList.apply(command.get("env"));
                File wdDir = new File(wd == null ? "." : wd);
                execute(command, args, env, wdDir, processListener);
            });
            options.add(item);
        };
        commandRepository.getItems().parallelStream().forEach(bindUI);
        getContentPane().add(options);
        return options;
    }

    private void execute(Map<String, Object> command, String[] args, List<String> env, File wdDir, OnNewProcessListener processListener) {
        Consumer<InputStream> reader = (InputStream is) -> {
            int b = -1;
            try {
                while ((b = is.read()) != -1) {
                    console.append(String.valueOf((char) b));
                }
            } catch (Exception ie) {
                console.append("Read: " + ie.getMessage());
            }
            console.append("\n");
        };
        Runnable runnable = () -> {
            try {
                String joinedArgs = Arrays.asList(args).stream().reduce("", (a, b) -> a + " " + b);
                String[] envParams = env != null ? env.toArray(new String[0]) : new String[0];
                Process process = Runtime.getRuntime().exec(command.get("cmd") + " " + joinedArgs, envParams, wdDir);
                processListener.created(Utils.toStr.apply(command.get("label")), process);
                reader.accept(process.getInputStream());
                reader.accept(process.getErrorStream());
            } catch (Exception e) {
                console.append("exec error " + e.getMessage());
            }
        };
        new Thread(runnable).start();
    }

    interface OnNewProcessListener {
        void created(String name, Process process);
    }

}
