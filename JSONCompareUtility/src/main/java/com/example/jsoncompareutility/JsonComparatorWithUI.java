package com.example.jsoncompareutility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonComparatorWithUI {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 读取JSON文件并返回JsonNode对象
    public static JsonNode readJsonFile(String filePath) throws IOException {
        return objectMapper.readTree(new File(filePath));
    }

    // 比较两个JsonNode对象，返回不同的地方
    public static String compareJson(JsonNode json1, JsonNode json2, String path) {
        StringBuilder differences = new StringBuilder();

        // 如果两个JsonNode不相等
        if (!json1.equals(json2)) {
            if (json1.isObject() && json2.isObject()) {
                // 对比对象类型
                Iterator<Map.Entry<String, JsonNode>> fields1 = json1.fields();
                while (fields1.hasNext()) {
                    Map.Entry<String, JsonNode> entry1 = fields1.next();
                    JsonNode value1 = entry1.getValue();
                    JsonNode value2 = json2.get(entry1.getKey());

                    if (value2 == null) {
                        // 如果只在第一个文件中有这个键
                        differences.append("Key: " + path + "." + entry1.getKey() + " exists only in the first file.\n");
                    } else {
                        // 如果键在两个文件都有，递归比较值
                        differences.append(compareJson(value1, value2, path + "." + entry1.getKey()));
                    }
                }

                // 查找第二个文件中有，而第一个文件没有的键
                Iterator<Map.Entry<String, JsonNode>> fields2 = json2.fields();
                while (fields2.hasNext()) {
                    Map.Entry<String, JsonNode> entry2 = fields2.next();
                    JsonNode value2 = entry2.getValue();
                    JsonNode value1 = json1.get(entry2.getKey());

                    if (value1 == null) {
                        // 如果只在第二个文件中有这个键
                        differences.append("Key: " + path + "." + entry2.getKey() + " exists only in the second file.\n");
                    }
                }
            } else if (json1.isArray() && json2.isArray()) {
                // 对比数组类型
                for (int i = 0; i < Math.min(json1.size(), json2.size()); i++) {
                    differences.append(compareJson(json1.get(i), json2.get(i), path + "[" + i + "]"));
                }
                if (json1.size() != json2.size()) {
                    differences.append("Arrays have different sizes at path: " + path + "\n");
                }
            } else {
                // 对比基本类型（例如String, Number等）
                differences.append("Value mismatch at path: " + path + "\n");
                differences.append("File 1: " + json1.toString() + "\n");
                differences.append("File 2: " + json2.toString() + "\n");
            }
        }
        return differences.toString();
    }

    // 显示对比结果的弹窗
    private static void showComparisonResult(String result) {
        JFrame frame = new JFrame("JSON Comparison Result");
        JTextArea textArea = new JTextArea(result);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // 主程序
    public static void main(String[] args) {
        // 设置文件路径
//        String filePath1 = "JSONCompareUtility/src/main/resources/simple.json";
//        String filePath2 = "JSONCompareUtility/src/main/resources/simple-compare.json";

        String filePath1 = "JSONCompareUtility/src/main/resources/medium.json";
        String filePath2 = "JSONCompareUtility/src/main/resources/medium-compare.json";

        try {
            // 读取两个 JSON 文件
            JsonNode json1 = readJsonFile(filePath1);
            JsonNode json2 = readJsonFile(filePath2);

            // 对比 JSON
            String result = compareJson(json1, json2, "");

            // 判断是否有不同点
            if (result.isEmpty()) {
                result = "The JSON files are identical.";
            }

            // 显示弹窗
            showComparisonResult(result);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading the JSON files.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}