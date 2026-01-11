package main.java.com.emergencias.alert;

import main.java.com.emergencias.model.EmergencyEvent;
import main.java.com.emergencias.model.UserData;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertSender {
    private static final String ALERT_LOG_FILE = "alertas_log.txt";
    private final String destino;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlertSender(String destino) {
        this.destino = destino;
    }

    public void sendAlert(EmergencyEvent event) {
        if (event == null || !event.esGrave()) {
            System.err.println("Alerta no enviada: El evento es nulo o no fue validado como grave.");
            return;
        }

        System.out.println("\n--- MÓDULO DE NOTIFICACIÓN DE EMERGENCIA ---");

        String paqueteDatos = buildAlertPackage(event);
        System.out.println("Paquete de datos de alerta generado:");
        System.out.println(paqueteDatos);

        System.out.printf("🚀 Enviando alerta prioritaria a %s...\n", destino);
        persistAlert(event);
        simulateCloudBackup(event);
        notifyContacts(event.getDatosUsuario());

        System.out.printf("✅ Alerta enviada con éxito a %s a las %s.\n", destino, LocalDateTime.now().format(FORMATTER));
    }


    private String buildAlertPackage(EmergencyEvent event) {
        UserData user = event.getDatosUsuario();

        // Recuperamos los signos vitales del evento. Si es null, ponemos texto por defecto.
        String signosInfo = (event.getVitalSigns() != null) ? event.getVitalSigns().toString() : "No disponibles";

        return String.format(
                "DESTINO: %s | TIPO: %s | ID Evento: %s\n" +
                        "UBICACIÓN: %s\n" +
                        "INFO PACIENTE: %s (Tel: %s)\n" +
                        "INFO MÉDICA: %s\n" +
                        "SIGNOS VITALES: %s\n", // <--- NUEVA LÍNEA AÑADIDA
                destino, event.getTipoEmergencia(), event.getIdEvento().substring(0, 8),
                event.getUbicacion(), user.getNombre(), user.getTelefono(), user.getInfoMedica(),
                signosInfo // <--- PASAMOS EL DATO
        );
    }

    private void persistAlert(EmergencyEvent event) {
        try (FileWriter writer = new FileWriter(ALERT_LOG_FILE, true)) {
            UserData user = event.getDatosUsuario();

            // También lo añadimos al log del fichero
            String signosInfo = (event.getVitalSigns() != null) ? event.getVitalSigns().toString() : "N/A";

            String logLine = String.format(
                    "[%s] ALERTA GRAVE | Tipo: %s | ID: %s | Vitales: %s | Usuario: %s\n",
                    LocalDateTime.now().format(FORMATTER),
                    event.getTipoEmergencia(),
                    event.getIdEvento(),
                    signosInfo,
                    user.getNombre()
            );
            writer.write(logLine);
            System.out.printf("📝 Alerta guardada en el log: %s\n", ALERT_LOG_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error de I/O: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error desconocido: " + e.getMessage());
        }
    }

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

    public void notifyContacts(UserData user) {
        System.out.printf("📞 Notificando a contactos de %s.\n", user.getNombre());
    }
}