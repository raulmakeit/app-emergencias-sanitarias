package main.java.com.emergencias.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Clase que representa un evento de emergencia detectado.
 * Contiene el tipo, la ubicación, el momento de activación, los datos del usuario
 * y el registro de signos vitales en el momento de la alerta.
 */
public class EmergencyEvent {
    private final String idEvento;
    private final String tipoEmergencia;
    private final String ubicacion; // Simulado con String (e.g., "Lat: 40.4, Lon: -3.7")
    private final LocalDateTime timestamp;
    private final UserData datosUsuario;
    private boolean esGrave;

    // Variable que almacena los signos vitales del usuario (Integrado de v2)
    private VitalSigns signosVitales;

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
        this.idEvento = UUID.randomUUID().toString();
        this.tipoEmergencia = tipoEmergencia;
        this.ubicacion = ubicacion != null ? ubicacion : "Ubicación desconocida";
        this.timestamp = LocalDateTime.now();
        this.datosUsuario = datosUsuario;
        this.esGrave = false; // Por defecto no es grave hasta validación
        this.signosVitales = null; // Se inicializa vacío hasta que se lean los signos
    }

    /**
     * El evento se encarga de llamar a la función independiente
     * para obtener los datos médicos en este instante.
     */
    public void leerSignosVitales() {
        this.signosVitales = new VitalSigns();
    }

    // --- GETTERS Y SETTERS ---

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
     * Obtiene los signos vitales registrados en el evento.
     * @return Objeto VitalSigns o null si no se han registrado.
     */
    public VitalSigns getVitalSigns() {
        return signosVitales;
    }

    /**
     * Genera una cadena formateada del evento para el log de alertas,
     * incluyendo la información médica si está disponible.
     */
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
                idEvento,
                timestamp.format(formatter),
                tipoEmergencia,
                ubicacion,
                esGrave ? "SÍ" : "NO",
                infoVitals,
                datosUsuario.toString());
    }
}