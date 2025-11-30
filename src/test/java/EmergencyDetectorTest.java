package test.java;

import main.java.com.emergencias.detector.EmergencyDetector;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para EmergencyDetector (CORE 1).
 * Verifica el flujo de deteccion manual y automatica, simulando la entrada por consola (System.in)
 * para garantizar la ejecucion sin interaccion de usuario.
 */
class EmergencyDetectorTest {

    private UserData dummyUser;
    private EmergencyDetector detector;
    private InputStream originalSystemIn;

    @BeforeEach
    void setUp() {
        dummyUser = new UserData("Test Detector", "111222333", "Test Info");
        detector = new EmergencyDetector(dummyUser);
        // Guarda la referencia original a System.in para restaurarla despues.
        originalSystemIn = System.in;
    }

    @AfterEach
    void tearDown() {
        // Restaura System.in despues de cada prueba.
        System.setIn(originalSystemIn);
    }

    /**
     * Simula la entrada del usuario en la consola para una prueba.
     * @param inputString La cadena que simula la entrada de datos.
     */
    private void mockUserInput(String inputString) {
        // Usa ByteArrayInputStream para reemplazar el flujo de entrada del sistema
        InputStream is = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(is);
    }

    @Test
    void testActivacionManualExitosa() {
        // SIMULACIÓN DE ENTRADA: 'E' (Activar), 'Sanitaria' (Tipo), 'S' (Confirmar)
        mockUserInput("E\nSanitaria\nS\n");

        EmergencyEvent event = detector.detectEvent(false);

        assertNotNull(event, "El evento no debe ser nulo en una activacion manual confirmada.");
        assertTrue(event.esGrave(), "El evento debe estar marcado como grave.");
    }

    @Test
    void testActivacionManualCancelada() {
        // SIMULACIÓN DE ENTRADA: 'E' (Activar), 'Otro' (Tipo), 'N' (Cancelar)
        mockUserInput("E\nOtro\nN\n");

        EmergencyEvent event = detector.detectEvent(false);

        assertNull(event, "El evento debe ser nulo si la validacion es cancelada.");
    }

    @Test
    void testActivacionAutomaticaConfirmada() {
        // SIMULACIÓN DE ENTRADA: 'Vehicular' (Tipo), 'S' (Confirmar).
        // Esto cubre el caso en que la fuerza aleatoria supera el umbral y el sistema pide input.
        mockUserInput("Vehicular\nS\n");

        EmergencyEvent event = detector.detectEvent(true);

        assertNotNull(event, "El evento no debe ser nulo si se confirma la emergencia automatica.");
        assertTrue(event.esGrave(), "El evento automatico confirmado debe ser grave.");
    }
}