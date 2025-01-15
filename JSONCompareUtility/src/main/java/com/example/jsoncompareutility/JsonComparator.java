package com.example.jsoncompareutility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonComparator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Render JSON File
    public static JsonNode readJsonFile(String filePath) throws IOException {
        return objectMapper.readTree(new File(filePath));
    }

    // Compare JSON Files
    public static String compareJsonFiles(JsonNode json1, JsonNode json2) {
        return compareJsonNode(json1, json2, "");
    }

    // Compare JSON Nodes
    private static String compareJsonNode(JsonNode json1, JsonNode json2, String path) {
        StringBuilder differences = new StringBuilder();

        if (json1.equals(json2)) {
            return differences.toString();
        }

        if (json1.isObject() && json2.isObject()) {
            differences.append(compareObjects(json1, json2, path));
        } else if (json1.isArray() && json2.isArray()) {
            differences.append(compareArrays(json1, json2, path));
        } else {
            differences.append(compareValues(json1, json2, path));
        }

        return differences.toString();
    }

    // Compare JSON Objects
    private static String compareObjects(JsonNode json1, JsonNode json2, String path) {
        StringBuilder differences = new StringBuilder();

        // Compare json1 fields
        Iterator<Map.Entry<String, JsonNode>> fields1 = json1.fields();
        while (fields1.hasNext()) {
            Map.Entry<String, JsonNode> entry1 = fields1.next();
            JsonNode value1 = entry1.getValue();
            JsonNode value2 = json2.get(entry1.getKey());

            if (value2 == null) {
                differences.append("Key: ").append(path).append(".").append(entry1.getKey())
                        .append(" exists only in the first file.\n");
            } else {
                differences.append(compareJsonNode(value1, value2, path + "." + entry1.getKey()));
            }
        }

        // Comapre json2 fields
        Iterator<Map.Entry<String, JsonNode>> fields2 = json2.fields();
        while (fields2.hasNext()) {
            Map.Entry<String, JsonNode> entry2 = fields2.next();
            JsonNode value2 = entry2.getValue();
            JsonNode value1 = json1.get(entry2.getKey());

            if (value1 == null) {
                differences.append("Key: ").append(path).append(".").append(entry2.getKey())
                        .append(" exists only in the second file.\n");
            }
        }

        return differences.toString();
    }

    // Compare JSON Arrays
    private static String compareArrays(JsonNode json1, JsonNode json2, String path) {
        StringBuilder differences = new StringBuilder();

        int minSize = Math.min(json1.size(), json2.size());
        for (int i = 0; i < minSize; i++) {
            differences.append(compareJsonNode(json1.get(i), json2.get(i), path + "[" + i + "]"));
        }

        if (json1.size() != json2.size()) {
            differences.append("Arrays have different sizes at path: ").append(path).append("\n");
        }

        return differences.toString();
    }

    // compare simple key value
    private static String compareValues(JsonNode json1, JsonNode json2, String path) {
        StringBuilder differences = new StringBuilder();

        differences.append("Value mismatch at path: ").append(path).append("\n");
        differences.append("File 1: ").append(json1.toString()).append("\n");
        differences.append("File 2: ").append(json2.toString()).append("\n");

        return differences.toString();
    }

    public static void main(String[] args) {
        try {
//            String filePath1 = "JSONCompareUtility/src/main/resources/simple.json";
//            String filePath2 = "JSONCompareUtility/src/main/resources/simple-compare.json";
//            String filePath1 = "JSONCompareUtility/src/main/resources/medium.json";
//            String filePath2 = "JSONCompareUtility/src/main/resources/medium-compare.json";
            String filePath1 = "JSONCompareUtility/src/main/resources/array.json";
            String filePath2 = "JSONCompareUtility/src/main/resources/array-compare.json";

            JsonNode json1 = readJsonFile(filePath1);
            JsonNode json2 = readJsonFile(filePath2);

            String differences = compareJsonFiles(json1, json2);

            if (differences.isEmpty()) {
                System.out.println("The JSON files are identical.");
            } else {
                System.out.println("The JSON files are different:");
                System.out.println(differences);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error reading the JSON files.");
        }
    }
}