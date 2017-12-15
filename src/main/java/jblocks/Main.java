package jblocks;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Specificy a project to run");
            System.exit(0);
        }
        ObjectMapper m = new ObjectMapper();
        m.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        JsonNode rootNode;
        try {
            rootNode = m.readTree(new File(args[0]));
            Player player = new Player(new Stage(rootNode));
            new java.lang.Thread(player).start();
        } catch (IOException e) {
            System.out.println("Error reading file");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
