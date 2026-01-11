package main.java.com.emergencias.controller;

import main.java.com.emergencias.alert.AlertSender;
import main.java.com.emergencias.detector.EmergencyDetector;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class EmergencyManager {
    private final EmergencyDetector detector;
    private final AlertSender sender;
    private UserData userData;

    public EmergencyManager() {
        this.userData = loadUserDataFromResource();
        this.detector = new EmergencyDetector(userData);
        this.sender = new AlertSender("112");
    }

    private UserData loadUserDataFromResource() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("users.txt")));
            String dataLine = reader.lines()
                    .filter(line -> !line.startsWith("#"))
                    .collect(Collectors.toList())
                    .get(0);
            String[] data = dataLine.split(",");
            if (data.length >= 3) {
                return new UserData(data[0].trim(), data[1].trim(), data[2].trim());
            } else {
                throw new IllegalStateException("Formato inválido");
            }
        } catch (Exception e) {
            return new UserData("Usuario Demo", "999000111", "Sin datos");
        }
    }

    public void startSystem(boolean isAutomatic) {
        System.out.println("\n=======================================================");
        System.out.println("          GESTIÓN DE EMERGENCIAS                         ");
        System.out.println("=======================================================");

        try {
            EmergencyEvent event = detector.detectEvent(isAutomatic);

            if (event != null) {
                // --- CAMBIO AQUÍ: Llamada limpia ---
                // El manager solo ordena al evento que capture los datos
                System.out.println("Soliciando lectura de constantes vitales...");
                event.leerSignosVitales();
                // -----------------------------------

                sender.sendAlert(event);
            } else {
                System.out.println("\n▶️ Sistema finalizado. No se generó alerta.");
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERROR CRÍTICO EN EL SISTEMA: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n=======================================================");
            System.out.println("                Fin de la Ejecución                    ");
            System.out.println("=======================================================");
        }
    }
}