package main.java.com.emergencias.main;

import main.java.com.emergencias.controller.EmergencyManager;

import java.util.Scanner;

/**
 * Punto de entrada principal del módulo de emergencias.
 * Permite al usuario seleccionar el modo de activación (manual o automático simulado).
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmergencyManager manager = new EmergencyManager();

        System.out.println("\n--- SIMULADOR DE EMERGENCIAS ---");
        System.out.println("Seleccione el modo de activación:");
        System.out.println("1. Manual (Simula la pulsación de un botón)");
        System.out.println("2. Automático (Simula la activación por un umbral de sensor)");

        boolean opcionValida = false;
        boolean isAutomatic = false;

        // BUCLE DE CONTROL DE ERRORES (FIX)
        while (!opcionValida) {
            System.out.print("Opción [1/2]: ");
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                isAutomatic = true;
                System.out.println("Modo seleccionado: AUTOMÁTICO");
                opcionValida = true;
            } else if ("1".equals(choice)) {
                isAutomatic = false;
                System.out.println("Modo seleccionado: MANUAL");
                opcionValida = true;
            } else {
                // Control de error: Mensaje y repetición del bucle
                System.err.println("Error: Entrada no válida. Por favor, introduzca '1' o '2'.");
            }
        }

        manager.startSystem(isAutomatic);
        scanner.close();
    }
}