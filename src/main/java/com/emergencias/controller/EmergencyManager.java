package main.java.com.emergencias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import main.java.com.emergencias.alert.AlertSender;
import main.java.com.emergencias.detector.EmergencyDetector;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import main.java.com.emergencias.model.CentroSalud;

import java.util.List;

/**
 * Controlador principal que orquestra el sistema de emergencias.
 * Gestiona la carga de datos de usuario, la inicialización de módulos
 * y el flujo desde la detección hasta el envío de la alerta.
 */
public class EmergencyManager {
    private final EmergencyDetector detector;
    private final AlertSender sender;
    private UserData userData;
    private List<CentroSalud> listaCentros;

    /**
     * Constructor del Manager.
     * Carga dinámicamente el perfil del usuario y la red de centros de salud.
     */
    public EmergencyManager() {
        // 1. CARGA DE DATOS (Modernizada a JSON - v1)
        this.userData = loadUserDataFromJson();

        CentroSaludLoader loader = new CentroSaludLoader();
        this.listaCentros = loader.cargarCentros();

        // 2. INICIALIZACIÓN DE MÓDULOS
        // El detector ahora recibe tanto al usuario como la red de centros (v1)
        this.detector = new EmergencyDetector(userData, listaCentros);
        this.sender = new AlertSender("112");
    }

    /**
     * Carga el perfil del usuario desde un archivo JSON en recursos.
     * @return El primer objeto UserData encontrado o un usuario de fallback en caso de error.
     */
    private UserData loadUserDataFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<UserData> usuarios = mapper.readValue(
                    getClass().getClassLoader().getResourceAsStream("users.json"),
                    new TypeReference<List<UserData>>() {}
            );

            if (usuarios != null && !usuarios.isEmpty()) {
                return usuarios.get(0);
            }
            throw new Exception("El archivo JSON está vacío.");

        } catch (Exception e) {
            System.err.println("❌ ERROR DE CARGA DE USUARIO: " + e.getMessage());
            // Fallback reconocible para evitar que el sistema se detenga
            return new UserData("Usuario de Emergencia", "000", "Sin datos médicos", null, null);
        }
    }

    /**
     * Inicia el ciclo de vida del sistema de gestión de emergencias.
     * @param isAutomatic Determina si el disparador es manual o por sensores.
     */
    public void startSystem(boolean isAutomatic) {
        System.out.println("\n=======================================================");
        System.out.println("          SISTEMA DE GESTIÓN DE EMERGENCIAS            ");
        System.out.println("=======================================================");

        try {
            // 1. Fase de detección y validación
            EmergencyEvent event = detector.detectEvent(isAutomatic);

            if (event != null) {
                // 2. Fase de captura de datos médicos (Integrado de v2)
                System.out.println("▶️ Solicitando lectura de constantes vitales del usuario...");
                event.leerSignosVitales();

                // 3. Fase de comunicación
                sender.sendAlert(event);
            } else {
                System.out.println("\n▶️ Sistema finalizado. No se generó alerta (Posible cancelación o falso positivo).");
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERROR CRÍTICO EN EL SISTEMA: " + e.getMessage());
        } finally {
            System.out.println("\n=======================================================");
            System.out.println("                Fin de la Ejecución                    ");
            System.out.println("=======================================================");
        }
    }
}