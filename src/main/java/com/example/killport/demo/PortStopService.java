package com.example.killport.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class PortStopService {

    @Autowired
    private NotificationService notificationService;

    public void stopProcessesOnPort(int port) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            stopProcessesOnPortWindows(port);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            stopProcessesOnPortUnix(port);
        } else {
            System.err.println("Unsupported operating system.");
        }
    }

    private void stopProcessesOnPortUnix(int port) {
        String command = String.format("lsof -i tcp:%d", port);
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String pid = parts[1];
                    System.out.println("Killing process with PID: " + pid);
                    Runtime.getRuntime().exec("kill -9 " + pid);
                }
            }
        } catch (IOException e) {
            System.err.println("Error stopping processes on port " + port + ": " + e.getMessage());
        }
    }

    private void stopProcessesOnPortWindows(int port) {
        String command = String.format("cmd /c netstat -ano | findstr %d", port);
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 6) {
                    String pid = parts[5];
                    System.out.println("Killing process with PID: " + pid);
                    Runtime.getRuntime().exec("taskkill /F /PID " + pid);
                    notificationService.showNotification(String.format("Успешно завершили процесс на порту:    %s", port));
                }
            }
        } catch (IOException e) {
            System.err.println("Error stopping processes on port " + port + ": " + e.getMessage());
        }
    }

}
