package main.java.com.emergencias.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa un evento de emergencia detectado.
 * Contiene el tipo, la ubicación, el momento de activación y los datos del usuario.
 */
public class EmergencyEvent {
    private final String idEvento;
    private final String tipoEmergencia;
    private final String ubicacion; // Simulado con String (e.g., "Lat: 40.4, Lon: -3.7")
    private final LocalDateTime timestamp;
    private final UserData datosUsuario;
    private boolean esGrave;

    /**
     * Constructor del evento de emergencia.
     * @param tipoEmergencia Clasificación del evento (e.g., Sanitaria, Tráfico, General).
     * @param ubicacion Ubicación simulada del evento.
     * @param datosUsuario Información personal asociada al evento.
     */
    public EmergencyEvent(String tipoEmergencia, String ubicacion, UserData datosUsuario) {
        if (tipoEmergencia == null || tipoEmergencia.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de emergencia no puede ser nulo o vacío.");
        }
        this.idEvento = java.util.UUID.randomUUID().toString();
        this.tipoEmergencia = tipoEmergencia;
        this.ubicacion = ubicacion != null ? ubicacion : "Ubicación desconocida";
        this.timestamp = LocalDateTime.now();
        this.datosUsuario = datosUsuario;
        this.esGrave = false; // Por defecto no es grave hasta validación
    }

    // Getters y Setters
    public String getIdEvento() {
        return idEvento;
    }

    public String getTipoEmergencia() {
        return tipoEmergencia;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public UserData getDatosUsuario() {
        return datosUsuario;
    }

    public boolean esGrave() {
        return esGrave;
    }

    public void setEsGrave(boolean esGrave) {
        this.esGrave = esGrave;
    }

    /**
     * Genera una cadena formateada del evento para el log de alertas.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("--- ALERTA GENERADA ---\n" +
                        "ID: %s\n" +
                        "Timestamp: %s\n" +
                        "Tipo: %s\n" +
                        "Ubicación: %s\n" +
                        "Gravedad Confirmada: %s\n" +
                        "Datos Usuario: %s\n" +
                        "-----------------------\n",
                idEvento, timestamp.format(formatter), tipoEmergencia, ubicacion,
                esGrave ? "SÍ" : "NO", datosUsuario.toString());
    }
}