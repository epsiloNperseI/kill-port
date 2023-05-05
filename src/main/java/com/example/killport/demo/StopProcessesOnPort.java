package com.example.killport.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StopProcessesOnPort {

    public static void stopProcessesOnPort(int port) {
        String command = getCommandToFindProcesses(port);

        try {
            List<String> pids = executeCommand(command);

            if (pids.isEmpty()) {
                System.out.println("No processes found on port " + port);
            } else {
                for (String pid : pids) {
                    executeCommand("kill " + pid);
                    System.out.println("Killed process with PID: " + pid + " running on port " + port);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String getCommandToFindProcesses(int port) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "netstat -aon | findstr /R /C:\"" + port + " \"";
        } else {
            return "lsof -t -i :" + port;
        }
    }

    private static List<String> executeCommand(String command) throws IOException {
        List<String> result = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line.trim());
            }
        }

        return result;
    }
}

