package test.java;

import main.java.com.emergencias.alert.AlertSender;
import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase AlertSender (Core 2).
 */
class AlertSenderTest {

    private UserData dummyUser;
    private AlertSender alertSender;
    private EmergencyEvent eventGrave;
    private EmergencyEvent eventNoGrave;
    private final String LOG_FILENAME = "alertas_log.txt";

    @BeforeEach
    void setUp() throws IOException {
        // CORRECCIÓN: Se añaden las coordenadas (latitud y longitud) al constructor de UserData
        // Esto mantiene la coherencia con el nuevo modelo de datos dinámicos [cite: 16, 21]
        dummyUser = new UserData("Test Sender", "987654321", "Test Info", 38.267, -0.716);

        alertSender = new AlertSender("112");

        eventGrave = new EmergencyEvent("Accidente", "Test Loc 1", dummyUser);
        eventGrave.setEsGrave(true);

        eventNoGrave = new EmergencyEvent("Falso Positivo", "Test Loc 2", dummyUser);
        eventNoGrave.setEsGrave(false);

        Files.deleteIfExists(Paths.get(LOG_FILENAME));
    }

    @Test
    void testEnvioAlertaConGravedadConfirmadaYCreacionLog() throws IOException {
        assertDoesNotThrow(() -> alertSender.sendAlert(eventGrave));

        Path logPath = Paths.get(LOG_FILENAME);
        assertTrue(Files.exists(logPath), "El archivo de log debe crearse.");

        List<String> logLines = Files.readAllLines(logPath);
        assertFalse(logLines.isEmpty(), "El archivo de log no debe estar vacío.");

        String logContent = logLines.get(0).trim();
        String expectedType = eventGrave.getTipoEmergencia();

        assertTrue(logContent.contains(expectedType),
                "El log debe contener el tipo de emergencia ('" + expectedType + "'). Log leído: " + logContent);
    }

    @Test
    void testEnvioAlertaSinGravedadConfirmadaNoCreaLog() throws IOException {
        assertDoesNotThrow(() -> alertSender.sendAlert(eventNoGrave));

        Path logPath = Paths.get(LOG_FILENAME);
        assertFalse(Files.exists(logPath), "El archivo de log NO debe crearse para eventos no graves.");
    }
}