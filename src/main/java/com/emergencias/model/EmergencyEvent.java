package main.java.com.emergencias.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmergencyEvent {
    private final String idEvento;
    private final String tipoEmergencia;
    private final String ubicacion;
    private final LocalDateTime timestamp;
    private final UserData datosUsuario;
    private boolean esGrave;

    // Variable que almacena los signos vitales
    private VitalSigns signosVitales;

    public EmergencyEvent(String tipoEmergencia, String ubicacion, UserData datosUsuario) {
        if (tipoEmergencia == null || tipoEmergencia.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de emergencia no puede ser nulo o vacío.");
        }
        this.idEvento = java.util.UUID.randomUUID().toString();
        this.tipoEmergencia = tipoEmergencia;
        this.ubicacion = ubicacion != null ? ubicacion : "Ubicación desconocida";
        this.timestamp = LocalDateTime.now();
        this.datosUsuario = datosUsuario;
        this.esGrave = false;
        this.signosVitales = null;
    }

    /**
     * El evento se encarga de llamar a la función independiente
     * para obtener los datos médicos en este instante.
     */
    public void leerSignosVitales() {
        this.signosVitales = new VitalSigns();
    }

    // --- GETTERS ---
    public String getIdEvento() { return idEvento; }
    public String getTipoEmergencia() { return tipoEmergencia; }
    public String getUbicacion() { return ubicacion; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public UserData getDatosUsuario() { return datosUsuario; }
    public boolean esGrave() { return esGrave; }
    public void setEsGrave(boolean esGrave) { this.esGrave = esGrave; }

    // --- EL MÉTODO QUE FALTABA PARA EL ERROR ---
    public VitalSigns getVitalSigns() {
        return signosVitales;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String infoVitals = (signosVitales != null) ? signosVitales.toString() : "No registrados";

        return String.format("--- ALERTA GENERADA ---\n" +
                        "ID: %s\n" +
                        "Timestamp: %s\n" +
                        "Tipo: %s\n" +
                        "Ubicación: %s\n" +
                        "Gravedad Confirmada: %s\n" +
                        "Signos Vitales: %s\n" +
                        "Datos Usuario: %s\n" +
                        "-----------------------\n",
                idEvento, timestamp.format(formatter), tipoEmergencia, ubicacion,
                esGrave ? "SÍ" : "NO", infoVitals, datosUsuario.toString());
    }
}