package test.java;

import main.java.com.emergencias.detector.EmergencyDetector;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import main.java.com.emergencias.model.CentroSalud;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmergencyDetectorTest {

    private UserData dummyUser;
    private EmergencyDetector detector;
    private InputStream originalSystemIn;
    private List<CentroSalud> dummyCentros;

    @BeforeEach
    void setUp() {
        // CORRECCIÓN: Añadimos coordenadas de prueba (Ej: Elche 38.267, -0.716)
        // Esto permite que el detector tenga una ubicación base para calcular distancias [cite: 12, 16]
        dummyUser = new UserData("Test Detector", "111222333", "Test Info", 38.267, -0.716);

        // Estructura dinámica necesaria para el constructor del detector [cite: 19, 21]
        dummyCentros = new ArrayList<>();

        // El detector ahora recibe tanto el usuario como la lista de centros [cite: 46]
        detector = new EmergencyDetector(dummyUser, dummyCentros);

        originalSystemIn = System.in;
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalSystemIn);
    }

    private void mockUserInput(String inputString) {
        InputStream is = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(is);
    }

    @Test
    void testActivacionManualExitosa() {
        mockUserInput("E\nSanitaria\nS\n");
        EmergencyEvent event = detector.detectEvent(false);

        assertNotNull(event, "El evento no debe ser nulo en una activacion manual confirmada.");
        assertTrue(event.esGrave(), "El evento debe estar marcado como grave.");
    }

    @Test
    void testActivacionManualCanceladaConBusqueda() {
        // Si el usuario cancela, el detector activará la lógica de búsqueda de centros [cite: 48, 49]
        mockUserInput("E\nsanitaria\nN\n");

        EmergencyEvent event = detector.detectEvent(false);

        assertNull(event, "El evento debe ser nulo si la validacion es cancelada.");
    }

    @Test
    void testActivacionAutomaticaConfirmada() {
        mockUserInput("Vehicular\nS\n");
        EmergencyEvent event = detector.detectEvent(true);

        assertNotNull(event, "El evento no debe ser nulo si se confirma la emergencia automatica.");
        assertTrue(event.esGrave(), "El evento automatico confirmado debe ser grave.");
    }
}