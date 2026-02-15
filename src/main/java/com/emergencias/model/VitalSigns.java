package main.java.com.emergencias.model;

import java.util.Random;

/**
 * Clase independiente que representa la lectura de sensores biométricos.
 * Funciona como una "caja negra" que genera datos médicos al instanciarse.
 */
public class VitalSigns {
    private final int ritmoCardiaco; // BPM
    private final int nivelOxigeno;  // SpO2 %
    private final int presionSistolica;
    private final int presionDiastolica;

    /**
     * El constructor actúa como la función generadora.
     * Al crear el objeto, se simula la lectura inmediata de los sensores.
     */
    public VitalSigns() {
        Random rand = new Random();
        // Simulación de datos alterados por emergencia
        this.ritmoCardiaco = 60 + rand.nextInt(110); // Entre 60 y 170
        this.nivelOxigeno = 88 + rand.nextInt(13);   // Entre 88% y 100%
        this.presionSistolica = 110 + rand.nextInt(50);
        this.presionDiastolica = 70 + rand.nextInt(40);
        System.out.println("💗 [HARDWARE] Sensores biométricos leídos correctamente.");
    }

    @Override
    public String toString() {
        return String.format("[HR: %d bpm | SpO2: %d%% | PA: %d/%d]",
                ritmoCardiaco, nivelOxigeno, presionSistolica, presionDiastolica);
    }
}