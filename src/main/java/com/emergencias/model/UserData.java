package main.java.com.emergencias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Clase que representa los datos personales y de ubicación del usuario.
 * Adaptada para soportar persistencia en JSON y empaquetar información
 * relevante para el envío de alertas.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos desconocidos al leer JSON
public class UserData {
    private String nombre;
    private String telefono;
    private String infoMedica; // E.g., Alergias, tipo de sangre

    // Ubicación simulada usando Double para permitir valores nulos (Wrapper class)
    private Double latitudSimulada;
    private Double longitudSimulada;

    /**
     * Constructor vacío requerido por la librería Jackson para la
     * deserialización de datos.
     */
    public UserData() {}

    /**
     * Constructor completo para inicializar los datos del usuario con validación.
     * @param nombre Nombre completo (obligatorio).
     * @param telefono Número de contacto (obligatorio).
     * @param infoMedica Información médica relevante.
     * @param lat Latitud simulada.
     * @param lon Longitud simulada.
     */
    public UserData(String nombre, String telefono, String infoMedica, Double lat, Double lon) {
        if (nombre == null || nombre.trim().isEmpty() || telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre y el teléfono son campos obligatorios.");
        }
        this.nombre = nombre;
        this.telefono = telefono;
        this.infoMedica = infoMedica != null ? infoMedica : "No especificada";
        this.latitudSimulada = lat;
        this.longitudSimulada = lon;
    }

    // --- GETTERS ---
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getInfoMedica() { return infoMedica; }
    public Double getLatitudSimulada() { return latitudSimulada; }
    public Double getLongitudSimulada() { return longitudSimulada; }

    // --- SETTERS ---
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setInfoMedica(String infoMedica) { this.infoMedica = infoMedica; }
    public void setLatitudSimulada(Double latitudSimulada) { this.latitudSimulada = latitudSimulada; }
    public void setLongitudSimulada(Double longitudSimulada) { this.longitudSimulada = longitudSimulada; }

    /**
     * Devuelve una representación formateada de los datos del usuario,
     * gestionando visualmente la ausencia de coordenadas.
     */
    @Override
    public String toString() {
        String pos = (latitudSimulada == null || longitudSimulada == null)
                ? "Sin ubicación"
                : String.format("%.4f, %.4f", latitudSimulada, longitudSimulada);

        return String.format("👤 %s (Tel: %s) | Info Médica: %s | Posición: %s",
                nombre, telefono, infoMedica, pos);
    }
}