package test.java;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase EmergencyEvent (Modelo).
 * Verifica la creacion del evento y las validaciones de datos.
 */
class EmergencyEventTest {

    private UserData dummyUser;
    private String ubicacionSimulada = "40.0, -3.0";

    @BeforeEach
    void setUp() {
        dummyUser = new UserData("Test User", "123456789", "Alergia Nuez");
    }

    @Test
    void testCreacionEventoValido() {
        // GIVEN: Datos validos
        String tipo = "Sanitaria";

        // WHEN: Se crea el evento
        EmergencyEvent event = new EmergencyEvent(tipo, ubicacionSimulada, dummyUser);

        // THEN: Se verifica que se haya creado correctamente
        assertNotNull(event.getIdEvento(), "El ID del evento no debe ser nulo.");
        assertEquals(tipo, event.getTipoEmergencia(), "El tipo de emergencia no coincide.");
        assertFalse(event.esGrave(), "La gravedad debe ser falsa por defecto.");
    }

    @Test
    void testEventoSinTipoLanzaExcepcion() {
        // GIVEN: Tipo de emergencia nulo
        String tipoNulo = null;

        // THEN: Se espera que el constructor lance una IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            new EmergencyEvent(tipoNulo, ubicacionSimulada, dummyUser);
        }, "Debe lanzar excepcion si el tipo es nulo.");
    }

    @Test
    void testSetterEsGrave() {
        // GIVEN: Un evento inicial
        EmergencyEvent event = new EmergencyEvent("Test", ubicacionSimulada, dummyUser);

        // WHEN: Se cambia la gravedad
        event.setEsGrave(true);

        // THEN: Se verifica el cambio
        assertTrue(event.esGrave(), "La gravedad debe ser verdadera despues de la actualizacion.");
    }
}