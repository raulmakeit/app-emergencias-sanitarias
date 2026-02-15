package main.java.com.emergencias.detector;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import main.java.com.emergencias.model.CentroSalud;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Clase avanzada encargada de la detección de emergencias.
 * Combina el cálculo de proximidad geográfica con un sistema de confirmación
 * basado en temporizadores (Dead Man Switch) para mayor seguridad.
 */
public class EmergencyDetector {

    private static final int TIMEOUT_SECONDS = 10;
    private final Scanner scanner;
    private final UserData currentUser;
    private final List<CentroSalud> centrosSalud;

    /**
     * Constructor del detector.
     * @param currentUser Datos del usuario actual para geolocalización.
     * @param centrosSalud Lista de centros cargados desde el JSON.
     */
    public EmergencyDetector(UserData currentUser, List<CentroSalud> centrosSalud) {
        this.currentUser = currentUser;
        this.centrosSalud = centrosSalud;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Punto de entrada principal para detectar un evento.
     * @param isAutomatic Indica si la activación es por sensores o manual.
     * @return EmergencyEvent validado o null si se cancela o falla.
     */
    public EmergencyEvent detectEvent(boolean isAutomatic) {
        System.out.println("\n--- MÓDULO DE DETECCIÓN DE EMERGENCIA ---");

        // 1. Confirmación de activación inicial
        if (!confirmarActivacion(isAutomatic)) return null;

        // 2. Determinación del tipo de emergencia con Timeout
        String tipoPrompt = isAutomatic
                ? String.format("\n⚠️ MODO AUTO: Introduzca tipo (Sanitaria/Tráfico/General) en %d seg:", TIMEOUT_SECONDS)
                : "Introduce tipo de emergencia (Sanitaria/Tráfico/General):";

        String tipoInput = readTimedInput(TIMEOUT_SECONDS, isAutomatic, tipoPrompt);
        String tipo = tipoInput.trim().isEmpty() ? "General" : tipoInput.trim();

        // 3. Gestión de Ubicación (Viene de v1)
        String locStr = (currentUser.getLatitudSimulada() == null || currentUser.getLongitudSimulada() == null)
                ? "Ubicación desconocida (No disponible en JSON)"
                : String.format("%.4f, %.4f", currentUser.getLatitudSimulada(), currentUser.getLongitudSimulada());

        EmergencyEvent newEvent = new EmergencyEvent(tipo, locStr, currentUser);

        // 4. Validación de gravedad con lógica de "Interruptor de Hombre Muerto" (Viene de v2)
        if (validateSeverity(newEvent, isAutomatic)) {
            System.out.println("✅ Validación exitosa. Evento confirmado.");
            return newEvent;
        } else {
            System.out.println("❌ Alerta cancelada (posible falso positivo).");
            // Si es sanitaria y se cancela, sugerimos el centro más cercano (Viene de v1)
            if (tipo.equalsIgnoreCase("sanitaria")) recomendarCentroCercano();
            return null;
        }
    }

    private boolean validateSeverity(EmergencyEvent event, boolean isAutomatic) {
        System.out.println("\n--- VALIDACIÓN DE GRAVEDAD ---");
        String prompt = isAutomatic
                ? String.format("⚠️ MODO AUTO: ¿Confirma gravedad (S/N)? Sin respuesta se activará en %d seg.", TIMEOUT_SECONDS)
                : "¿Confirma la gravedad (S/N)?:";

        String confirmation = readTimedInput(TIMEOUT_SECONDS, isAutomatic, prompt);

        // Lógica de Timeout: Si no responde en automático, asumimos que está inconsciente y es GRAVE
        if (isAutomatic && confirmation.isEmpty()) {
            System.out.println("\n🚨 TIMEOUT: El usuario no responde. Activando protocolo de EMERGENCIA GRAVE.");
            event.setEsGrave(true);
            return true;
        }

        if (confirmation.equalsIgnoreCase("S")) {
            event.setEsGrave(true);
            return true;
        }
        return false;
    }

    // --- LÓGICA GEOGRÁFICA (Integrada de v1) ---

    private void recomendarCentroCercano() {
        if (currentUser.getLatitudSimulada() == null || currentUser.getLongitudSimulada() == null) {
            System.out.println("\n⚠️ No se puede recomendar centro: Sin coordenadas del usuario.");
            return;
        }

        double miLat = currentUser.getLatitudSimulada();
        double miLon = currentUser.getLongitudSimulada();
        CentroSalud masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        System.out.println("\n--- 📍 BUSCANDO AYUDA CERCANA ---");
        for (CentroSalud centro : centrosSalud) {
            try {
                double cLat = Double.parseDouble(centro.getLatitud().replace(",", "."));
                double cLon = Double.parseDouble(centro.getLongitud().replace(",", "."));
                double dist = calcularHaversine(miLat, miLon, cLat, cLon);

                if (dist < menorDistancia) {
                    menorDistancia = dist;
                    masCercano = centro;
                }
            } catch (Exception e) { /* Error en formato de coordenadas del JSON */ }
        }

        if (masCercano != null) {
            System.out.println("Sugerencia: " + masCercano.getNombre() + " (" + masCercano.getMunicipio() + ")");
            System.out.printf("Distancia estimada: %.2f km\n", menorDistancia);
        }
    }

    private double calcularHaversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // --- LÓGICA DE INPUT CON TIEMPO (Integrada de v2) ---

    private String readTimedInput(int timeoutSeconds, boolean isAutomatic, String prompt) {
        System.out.println(prompt);
        if (!isAutomatic) return scanner.hasNextLine() ? scanner.nextLine() : "";

        final String[] result = {""};
        Thread inputThread = new Thread(() -> {
            try { if (scanner.hasNextLine()) result[0] = scanner.nextLine(); } catch (Exception e) {}
        });

        inputThread.start();
        try { inputThread.join((long) timeoutSeconds * 1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        if (inputThread.isAlive()) {
            inputThread.interrupt();
            return "";
        }
        return result[0];
    }

    private boolean confirmarActivacion(boolean isAuto) {
        if (!isAuto) {
            System.out.println("Activación manual: Pulse 'E' para simular emergencia:");
            return scanner.nextLine().equalsIgnoreCase("E");
        }
        int fuerza = new Random().nextInt(100);
        System.out.printf("Sensor detectado: %d (Umbral: 50)\n", fuerza);
        return fuerza >= 50;
    }
}