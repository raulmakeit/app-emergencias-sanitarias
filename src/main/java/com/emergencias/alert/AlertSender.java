package main.java.com.emergencias.alert;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase encargada de empaquetar y simular el envío de la alerta a servicios de emergencia.
 * Persiste el evento en un archivo log.
 */
public class AlertSender {
    private static final String ALERT_LOG_FILE = "alertas_log.txt";
    private final String destino;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor.
     * @param destino El servicio de emergencia o contacto de destino (e.g., "112").
     */
    public AlertSender(String destino) {
        this.destino = destino;
    }

    /**
     * Empaqueta la información del evento y simula el envío al destino.
     * @param event El evento de emergencia confirmado.
     */
    public void sendAlert(EmergencyEvent event) {
        if (event == null || !event.esGrave()) {
            System.err.println("Alerta no enviada: El evento es nulo o no fue validado como grave.");
            return;
        }

        System.out.println("\n--- MÓDULO DE NOTIFICACIÓN DE EMERGENCIA ---");

        // 1. Empaquetar datos clave
        String paqueteDatos = buildAlertPackage(event);
        System.out.println("Paquete de datos de alerta generado:");
        System.out.println(paqueteDatos);

        // 2. Simular envío (imprimir y persistir)
        System.out.printf("🚀 Enviando alerta prioritaria a %s...\n", destino);
        persistAlert(event); // Llama al metodo corregido

        // 3. Simular contacto personal
        notifyContacts(event.getDatosUsuario());

        System.out.printf("✅ Alerta enviada con éxito a %s a las %s.\n", destino, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * Construye la cadena de datos clave para el envío.
     * @param event El evento.
     * @return Cadena formateada con la información esencial.
     */
    private String buildAlertPackage(EmergencyEvent event) {
        UserData user = event.getDatosUsuario();
        return String.format(
                "DESTINO: %s | TIPO: %s | ID Evento: %s\n" +
                        "UBICACIÓN: %s\n" +
                        "INFO PACIENTE: %s (Tel: %s)\n" +
                        "INFO MÉDICA: %s\n",
                destino, event.getTipoEmergencia(), event.getIdEvento().substring(0, 8),
                event.getUbicacion(), user.getNombre(), user.getTelefono(), user.getInfoMedica()
        );
    }

    /**
     * Simula la persistencia de la alerta en un archivo de log.
     * CORRECCIÓN: Escribe explícitamente el tipo de emergencia para que la prueba unitaria lo encuentre.
     * @param event El evento a guardar.
     */
    private void persistAlert(EmergencyEvent event) {
        try (FileWriter writer = new FileWriter(ALERT_LOG_FILE, true)) {
            UserData user = event.getDatosUsuario();

            // Log Line que DEBE incluir el tipo de emergencia para que el test pase
            String logLine = String.format(
                    "[%s] ALERTA GRAVE | Tipo: %s | ID: %s | Ubicacion: %s | Usuario: %s\n",
                    LocalDateTime.now().format(FORMATTER),
                    event.getTipoEmergencia(),
                    event.getIdEvento(),
                    event.getUbicacion(),
                    user.getNombre()
            );

            writer.write(logLine);
            System.out.printf("📝 Alerta guardada en el log: %s\n", ALERT_LOG_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error de I/O al escribir el log de alertas: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error desconocido al guardar la alerta: " + e.getMessage());
        }
    }

    /**
     * Simula la notificación a contactos personales del usuario.
     * @param user Datos del usuario.
     */
    public void notifyContacts(UserData user) {
        System.out.printf("📞 Notificando a contactos personales del usuario %s (simulado vía SMS/llamada).\n", user.getNombre());
    }
}