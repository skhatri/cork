package com.github.skhatri;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CommandRepository {
    private final File file;
    private List<Map<String, Object>> commands = new ArrayList<>();

    public CommandRepository(File f) {
        this.file = f;
        try {
            commands = new ObjectMapper().readValue(file, List.class);
        } catch (Exception e) {
            throw new RuntimeException("file error", e);
        }
    }

    public List<Map<String, Object>> getItems() {
        return commands;
    }
}
