package main.java.com.emergencias.alert;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase encargada de empaquetar, persistir y simular el envío de alertas
 * a servicios de emergencia y servidores centralizados.
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
     * Empaqueta la información del evento, simula el envío y lo persiste localmente y en la nube.
     * @param event El evento de emergencia confirmado y grave.
     */
    public void sendAlert(EmergencyEvent event) {
        if (event == null || !event.esGrave()) {
            System.err.println("Alerta no enviada: El evento es nulo o no fue validado como grave.");
            return;
        }

        System.out.println("\n--- MÓDULO DE NOTIFICACIÓN DE EMERGENCIA ---");

        // 1. Generar paquete de datos completo (incluyendo signos vitales)
        String paqueteDatos = buildAlertPackage(event);
        System.out.println("Paquete de datos de alerta generado:");
        System.out.println(paqueteDatos);

        // 2. Simular envío y persistencia local
        System.out.printf("🚀 Enviando alerta prioritaria a %s...\n", destino);
        persistAlert(event);

        // 3. Simular respaldo en la nube (Viene de v2)
        simulateCloudBackup(event);

        // 4. Notificar a contactos personales
        notifyContacts(event.getDatosUsuario());

        System.out.printf("✅ Alerta enviada con éxito a %s a las %s.\n", destino, LocalDateTime.now().format(FORMATTER));
    }

    /**
     * Construye la cadena de datos clave para el envío, integrando información médica en tiempo real.
     */
    private String buildAlertPackage(EmergencyEvent event) {
        UserData user = event.getDatosUsuario();
        String signosInfo = (event.getVitalSigns() != null) ? event.getVitalSigns().toString() : "No disponibles";

        return String.format(
                "DESTINO: %s | TIPO: %s | ID Evento: %s\n" +
                        "UBICACIÓN: %s\n" +
                        "INFO PACIENTE: %s (Tel: %s)\n" +
                        "INFO MÉDICA: %s\n" +
                        "SIGNOS VITALES: %s\n",
                destino, event.getTipoEmergencia(), event.getIdEvento().substring(0, 8),
                event.getUbicacion(), user.getNombre(), user.getTelefono(), user.getInfoMedica(),
                signosInfo
        );
    }

    /**
     * Persiste la alerta en un archivo de log local.
     * Formato optimizado para trazabilidad y pruebas unitarias.
     */
    private void persistAlert(EmergencyEvent event) {
        try (FileWriter writer = new FileWriter(ALERT_LOG_FILE, true)) {
            UserData user = event.getDatosUsuario();
            String signosInfo = (event.getVitalSigns() != null) ? event.getVitalSigns().toString() : "N/A";

            String logLine = String.format(
                    "[%s] ALERTA GRAVE | Tipo: %s | ID: %s | Vitales: %s | Ubicacion: %s | Usuario: %s\n",
                    LocalDateTime.now().format(FORMATTER),
                    event.getTipoEmergencia(),
                    event.getIdEvento(),
                    signosInfo,
                    event.getUbicacion(),
                    user.getNombre()
            );

            writer.write(logLine);
            System.out.printf("📝 Alerta guardada en el log: %s\n", ALERT_LOG_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error de I/O al escribir el log: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error desconocido al guardar la alerta: " + e.getMessage());
        }
    }

    /**
     * Simula una conexión segura con un servidor central para respaldo de datos.
     */
    private void simulateCloudBackup(EmergencyEvent event) {
        System.out.println("☁️ Iniciando conexión segura con el servidor central...");
        try {
            System.out.print("   Subiendo datos encriptados: [");
            for (int i = 0; i < 10; i++) {
                System.out.print("=");
                Thread.sleep(50);
            }
            System.out.println("] 100%");
            System.out.println("☁️ Respaldo completado. ID: CLOUD-" + Math.abs(event.getIdEvento().hashCode()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Notifica a los contactos de emergencia registrados del usuario.
     */
    public void notifyContacts(UserData user) {
        System.out.printf("📞 Notificando a contactos personales de %s (simulado vía SMS/llamada).\n", user.getNombre());
    }
}