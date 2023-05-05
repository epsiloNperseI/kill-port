package com.example.killport.demo;

import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class NotificationService {

    public void showNotification(String message) {
        try{
            JOptionPane.showMessageDialog(null, message);
        } catch (Exception e) {
            System.out.println("Текст ошибки от swing" + e.getMessage() + e.getLocalizedMessage());
        }
    }
}
