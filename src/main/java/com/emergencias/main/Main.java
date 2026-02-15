package main.java.com.emergencias.main;

import main.java.com.emergencias.controller.EmergencyManager;
import main.java.com.emergencias.controller.CentroSaludLoader;
import main.java.com.emergencias.model.CentroSalud;

import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada principal del módulo de emergencias.
 * Gestiona la carga de datos externos y la interacción inicial con el usuario
 * mediante un sistema de validación de entrada robusto.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmergencyManager manager = new EmergencyManager();

        // 1. CARGA DE DATOS DINÁMICOS (Unidades 8 y 10)
        // Se carga al inicio para asegurar que la información esté lista antes de cualquier alerta.
        CentroSaludLoader loader = new CentroSaludLoader();
        List<CentroSalud> centrosDisponibles = loader.cargarCentros();

        if (!centrosDisponibles.isEmpty()) {
            System.out.println("✅ Datos de salud cargados correctamente (" + centrosDisponibles.size() + " centros).");
        } else {
            System.out.println("⚠️ No se han podido cargar datos externos. Verifique centros.json.");
        }

        System.out.println("\n--- SIMULADOR DE EMERGENCIAS ---");
        System.out.println("Seleccione el modo de activación:");
        System.out.println("1. Manual (Simula la pulsación de un botón)");
        System.out.println("2. Automático (Simula la activación por un umbral de sensor)");

        boolean opcionValida = false;
        boolean isAutomatic = false;

        // 2. BUCLE DE CONTROL DE ERRORES (FIX INTEGRADO)
        // Asegura que el usuario solo pueda introducir '1' o '2', repitiendo la petición en caso de error.
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
                // Mensaje de error y repetición del bucle
                System.err.println("Error: Entrada no válida. Por favor, introduzca '1' o '2'.");
            }
        }

        // 3. INICIO DEL SISTEMA PRINCIPAL
        manager.startSystem(isAutomatic);

        scanner.close();
    }
}