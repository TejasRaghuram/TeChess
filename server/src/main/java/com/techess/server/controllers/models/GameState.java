package com.techess.server.controllers.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    public GameState(String state) {

    }

    public GameState() {
        this(start());
    }

    private static String start() {
        return "";
    }

    public void move(String start, String end) {

    }

    public Map<String, List<String>> getMoves() {
        return new HashMap<>();
    }

    public String toString() {
        return "";
    }
}
