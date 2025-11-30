package main.java.com.emergencias.detector;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.util.Random;
import java.util.Scanner;

/**
 * Clase principal encargada de simular la detección o activación de una emergencia.
 * Implementa la lógica para disparadores manuales y automáticos, y la validación
 * para evitar falsos positivos.
 */
public class EmergencyDetector {

    // Nueva constante para el temporizador de confirmación en modo automático
    private static final int TIMEOUT_SECONDS = 10;

    private final Scanner scanner;
    private final UserData currentUser;

    /**
     * Constructor. Recibe los datos del usuario para adjuntarlos al evento.
     * @param currentUser Datos del usuario.
     */
    public EmergencyDetector(UserData currentUser) {
        this.currentUser = currentUser;
        // Se recomienda usar un único Scanner que envuelve System.in
        this.scanner = new Scanner(System.in);
    }

    /**
     * Helper para leer input con timeout en modo automático o bloquear en modo manual.
     * Esta implementación usa un Thread separado para aplicar un timeout real
     * a la operación de lectura bloqueante (scanner.nextLine()).
     * * @param timeoutSeconds Segundos de espera máxima.
     * @param isAutomatic Indica si se aplica el timeout.
     * @param prompt Mensaje a mostrar al usuario.
     * @return El input del usuario o una cadena vacía si hay timeout.
     */
    private String readTimedInput(int timeoutSeconds, boolean isAutomatic, String prompt) {
        System.out.println(prompt);

        if (!isAutomatic) {
            // MODO MANUAL: Espera indefinidamente (asume presencia del usuario)
            // Se usa nextLine() que es la operación de lectura bloqueante.
            return scanner.hasNextLine() ? scanner.nextLine() : "";
        }

        // --- MODO AUTOMÁTICO: Lógica robusta de temporizador usando Thread.join() ---

        final String[] result = {""}; // Array para contener el resultado del hilo de lectura

        // 1. Crear un hilo para realizar la lectura de input bloqueante
        Thread inputThread = new Thread(() -> {
            try {
                // Esta llamada bloqueará el hilo, esperando el input (Enter)
                String line = scanner.nextLine();
                synchronized (result) {
                    result[0] = line;
                }
            } catch (Exception e) {
                // En caso de error, se deja el resultado vacío
            }
        });

        inputThread.start(); // Iniciar la espera de input

        try {
            // 2. Esperar al hilo de input, pero con un límite de tiempo
            inputThread.join((long) timeoutSeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Evaluar el resultado
        if (inputThread.isAlive()) {
            // El hilo de input sigue vivo: Ocurrió el timeout.
            // Se interrumpe (aunque nextLine() puede ignorarlo) y se retorna vacío.
            inputThread.interrupt();
            return "";
        } else {
            // El hilo terminó: Se recibió el input.
            return result[0];
        }
    }

    /**
     * Simula la detección de un evento de emergencia, ofreciendo un disparador manual (consola)
     * y un disparador automático (simulación de umbral).
     * @param isAutomatic Indica si se simula un disparo automático (basado en umbral).
     * @return EmergencyEvent si se detecta y confirma la emergencia, o null en caso contrario.
     */
    public EmergencyEvent detectEvent(boolean isAutomatic) {
        System.out.println("\n--- MÓDULO DE DETECCIÓN DE EMERGENCIA ---");

        boolean isHighImpact = false;

        if (!isAutomatic) {
            // Activación manual: Aquí se mantiene el bloqueo, esperando el 'E'
            System.out.println("Activación manual: Pulse 'E' o 'e' para simular una emergencia:");
            String input = scanner.nextLine();
            if (!input.equalsIgnoreCase("E")) {
                System.out.println("Sistema en espera. No se detectó activación manual.");
                return null;
            }
            isHighImpact = true;
        } else {
            // Simulación de detección automática (e.g., sensor de impacto)
            int umbralActivacion = 50;
            int fuerzaDetectada = new Random().nextInt(100);
            System.out.printf("Detección automática simulada: Fuerza detectada: %d (Umbral: %d)\n", fuerzaDetectada, umbralActivacion);
            if (fuerzaDetectada < umbralActivacion) {
                System.out.println("Fuerza por debajo del umbral de activación. Sin emergencia inicial.");
                return null;
            }
            isHighImpact = true;
        }

        if (!isHighImpact) {
            return null;
        }

        String tipo = "General"; // Default inicial
        String ubicacion = getSimulatedLocation();

        // --- LÓGICA DE TIEMPO PARA EL TIPO DE EMERGENCIA ---

        String tipoPrompt;
        if (isAutomatic) {
            tipoPrompt = String.format("\n⚠️ MODO AUTOMÁTICO: Introduzca el tipo de emergencia (Sanitaria/Tráfico/General) en %d segundos...\n", TIMEOUT_SECONDS);
        } else {
            tipoPrompt = "Introduce el tipo de emergencia (Sanitaria/Tráfico/General):";
        }

        // Se llama al helper con el timeout
        String tipoInput = readTimedInput(TIMEOUT_SECONDS, isAutomatic, tipoPrompt);

        if (isAutomatic && tipoInput.isEmpty()) {
            // TIMEOUT en la entrada del tipo de emergencia
            tipo = "Indefinido (TIMEOUT)";
            System.out.printf("\n🚨 TIMEOUT: No se recibió tipo. Activando alerta GRAVE por omisión (%d segundos). Se salta la confirmación de gravedad.\n", TIMEOUT_SECONDS);
            EmergencyEvent newEvent = new EmergencyEvent(tipo, ubicacion, currentUser);
            newEvent.setEsGrave(true);
            // Retornar inmediatamente el evento grave
            return newEvent;
        } else {
            // Se recibió input (o estamos en modo manual)
            tipo = tipoInput.trim().isEmpty() ? "General" : tipoInput.trim();
        }

        EmergencyEvent newEvent = new EmergencyEvent(tipo, ubicacion, currentUser);

        // Validación de gravedad (S/N confirmation)
        if (validateSeverity(newEvent, isAutomatic)) {
            System.out.println("✅ Validación de gravedad exitosa. Evento de emergencia confirmado.");
            return newEvent;
        } else {
            System.out.println("❌ Alerta cancelada. La gravedad no fue confirmada (posible falso positivo).");
            return null;
        }
    }

    /**
     * Simula la validación de gravedad. Requiere confirmación manual para no ser un falso positivo.
     * Incluye la lógica de 'dead man switch' (interruptor de hombre muerto) para el modo automático.
     * @param event El evento a validar.
     * @param isAutomatic Indica si el evento fue disparado automáticamente.
     * @return true si la emergencia es confirmada como grave, false en caso contrario.
     */
    private boolean validateSeverity(EmergencyEvent event, boolean isAutomatic) {
        System.out.println("\n--- VALIDACIÓN DE GRAVEDAD ---");
        System.out.println("El sistema ha detectado una posible emergencia: " + event.getTipoEmergencia());

        String confirmationPrompt;
        if (isAutomatic) {
            confirmationPrompt = String.format("⚠️ MODO AUTOMÁTICO: ¿Confirma la emergencia (S/N)? Debe responder en menos de %d segundos. Si no responde, se confirmará como GRAVE.", TIMEOUT_SECONDS);
        } else {
            confirmationPrompt = "¿Confirma la emergencia (S/N)? Su respuesta (S/N):";
        }

        // Se llama al helper con el timeout
        String confirmation = readTimedInput(TIMEOUT_SECONDS, isAutomatic, confirmationPrompt);

        // --- LÓGICA DE TIMEOUT EN VALIDACIÓN S/N (SOLO MODO AUTOMÁTICO) ---
        if (isAutomatic && confirmation.isEmpty()) {
            System.out.printf("\n🚨 TIMEOUT: Confirmación S/N automática de emergencia grave por 'no-respuesta' (%d segundos).\n", TIMEOUT_SECONDS);
            event.setEsGrave(true);
            return true;
        }

        // --- PROCESAR RESPUESTA (MANUAL O AUTOMÁTICA CON RESPUESTA) ---
        if (confirmation.equalsIgnoreCase("S")) {
            event.setEsGrave(true);
            return true;
        } else if (confirmation.equalsIgnoreCase("N")) {
            return false;
        } else {
            // Respuesta inválida (solo se llega aquí si hubo input inválido, no si hubo timeout)
            System.out.println("Respuesta inválida. Cancelando validación.");
            return false;
        }
    }

    /**
     * Simula la obtención de la ubicación.
     * @return Una cadena que representa la ubicación.
     */
    private String getSimulatedLocation() {
        // Uso de valores hardcodeados o input para simplificar
        return "38°16'47.1\"N 0°42'57.0\"W (Elche, España)";
    }
}