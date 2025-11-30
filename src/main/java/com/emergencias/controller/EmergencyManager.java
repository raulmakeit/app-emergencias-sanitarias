package main.java.com.emergencias.controller;

import main.java.com.emergencias.alert.AlertSender;
import main.java.com.emergencias.detector.EmergencyDetector;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Clase controladora que orquesta el flujo completo de la detección y gestión de la emergencia.
 * Carga datos iniciales, inicia la detección y, si es exitosa, dispara la alerta.
 */
public class EmergencyManager {
    private final EmergencyDetector detector;
    private final AlertSender sender;
    private UserData userData;

    public EmergencyManager() {
        // 1. Cargar datos de usuario simulados al iniciar el sistema
        this.userData = loadUserDataFromResource();

        // 2. Inicializar EmergencyDetector y AlertSender
        this.detector = new EmergencyDetector(userData);
        this.sender = new AlertSender("112"); // Destino de emergencia hardcodeado como 112
    }

    /**
     * Carga datos de usuario desde el archivo users.txt en resources.
     * @return Una instancia de UserData con los datos cargados.
     */
    private UserData loadUserDataFromResource() {
        try {
            // Se usa getResourceAsStream para acceder al archivo dentro del JAR o classpath
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("users.txt")));

            // Leer la primera línea de datos (ignorando comentarios)
            String dataLine = reader.lines()
                    .filter(line -> !line.startsWith("#"))
                    .collect(Collectors.toList())
                    .get(0);

            String[] data = dataLine.split(",");

            if (data.length >= 3) {
                String nombre = data[0].trim();
                String telefono = data[1].trim();
                String infoMedica = data[2].trim();
                System.out.printf("✅ Datos de usuario cargados para: %s\n", nombre);
                return new UserData(nombre, telefono, infoMedica);
            } else {
                throw new IllegalStateException("Formato de datos de usuario inválido en users.txt.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos de usuario (users.txt). Usando datos por defecto.");
            System.err.println("Detalle: " + e.getMessage());
            // Fallback: usar datos por defecto si la carga falla
            return new UserData("Usuario Demo", "999000111", "Ninguna (Problema en carga)");
        }
    }

    /**
     * Inicia el ciclo de detección y notificación.
     */
    public void startSystem(boolean isAutomatic) {
        System.out.println("\n=======================================================");
        System.out.println("          GESTIÓN DE EMERGENCIAS                         ");
        System.out.println("=======================================================");

        try {
            // Detección y validación
            EmergencyEvent event = detector.detectEvent(isAutomatic);

            if (event != null) {
                // Notificación si la detección y validación fue exitosa
                sender.sendAlert(event);
            } else {
                System.out.println("\n▶️ Sistema finalizado. No se generó alerta.");
            }

        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas de validación
            System.err.println("\n❌ ERROR DE VALIDACIÓN: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción imprevista
            System.err.println("\n❌ ERROR CRÍTICO EN EL SISTEMA: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n=======================================================");
            System.out.println("                Fin de la Ejecución                    ");
            System.out.println("=======================================================");
        }
    }
}