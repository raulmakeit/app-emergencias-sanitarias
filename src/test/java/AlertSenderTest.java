package test.java; // <--- PAQUETE CORREGIDO

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
 * Verifica que el log se escribe solo para eventos graves.
 */
class AlertSenderTest {

    private UserData dummyUser;
    private AlertSender alertSender;
    private EmergencyEvent eventGrave;
    private EmergencyEvent eventNoGrave;
    private final String LOG_FILENAME = "alertas_log.txt";

    @BeforeEach
    void setUp() throws IOException {
        dummyUser = new UserData("Test Sender", "987654321", "Test Info");
        alertSender = new AlertSender("112");

        // Evento GRAVE: Configuracion para forzar el envio y log.
        eventGrave = new EmergencyEvent("Accidente", "Test Loc 1", dummyUser);
        eventGrave.setEsGrave(true);

        // Evento NO GRAVE: Configuracion para forzar la omision del envio/log.
        eventNoGrave = new EmergencyEvent("Falso Positivo", "Test Loc 2", dummyUser);
        eventNoGrave.setEsGrave(false);

        // Limpiar el log antes de cada prueba para un estado limpio
        Files.deleteIfExists(Paths.get(LOG_FILENAME));
    }

    @Test
    void testEnvioAlertaConGravedadConfirmadaYCreacionLog() throws IOException {
        // WHEN: Enviar la alerta grave
        assertDoesNotThrow(() -> alertSender.sendAlert(eventGrave));

        // THEN: El log debe existir y contener el evento
        Path logPath = Paths.get(LOG_FILENAME);
        assertTrue(Files.exists(logPath), "El archivo de log debe crearse.");

        List<String> logLines = Files.readAllLines(logPath);
        assertFalse(logLines.isEmpty(), "El archivo de log no debe estar vacio.");

        // USO DE trim() para evitar fallos por espacios en blanco y obtener la linea completa
        String logContent = logLines.get(0).trim();
        String expectedType = eventGrave.getTipoEmergencia();

        // Esta aserción es mucho más robusta y produce un mensaje de error más claro si falla.
        assertTrue(logContent.contains(expectedType),
                "El log debe contener el tipo de emergencia ('" + expectedType + "'). Log leido: " + logContent);
    }

    @Test
    void testEnvioAlertaSinGravedadConfirmadaNoCreaLog() throws IOException {
        // WHEN: Enviar la alerta no grave
        assertDoesNotThrow(() -> alertSender.sendAlert(eventNoGrave));

        // THEN: El log NO debe existir.
        Path logPath = Paths.get(LOG_FILENAME);
        assertFalse(Files.exists(logPath), "El archivo de log NO debe crearse para eventos no graves.");
    }
}