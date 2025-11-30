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
        System.out.print("Opción [1/2]: ");

        String choice = scanner.nextLine().trim();
        boolean isAutomatic;

        if ("2".equals(choice)) {
            isAutomatic = true;
            System.out.println("Modo seleccionado: AUTOMÁTICO");
        } else {
            isAutomatic = false;
            System.out.println("Modo seleccionado: MANUAL");
        }

        manager.startSystem(isAutomatic);
        scanner.close();
    }
}