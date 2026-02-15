package main.java.com.emergencias.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Clase del modelo que representa un centro de salud.
 * Adaptada para recibir datos estructurados en formato JSON[cite: 9, 15].
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos del JSON que no necesitemos
public class CentroSalud {

    @JsonProperty("Nombre")
    private String nombre;

    @JsonProperty("Dirección")
    private String direccion;

    @JsonProperty("Municipio")
    private String municipio;

    @JsonProperty("Teléfono")
    private String telefono;

    @JsonProperty("Latitud")
    private String latitud;

    @JsonProperty("Longitud")
    private String longitud;

    // Métodos auxiliares para obtener el valor numérico
    public double getLatNumeric() {
        try {
            return Double.parseDouble(latitud);
        } catch (Exception e) { return 0; }
    }

    public double getLonNumeric() {
        try {
            return Double.parseDouble(longitud);
        } catch (Exception e) { return 0; }
    }

    // Constructor vacío obligatorio para la librería Jackson
    public CentroSalud() {}

    // Getters para acceder a la información desde la aplicación
    public String getNombre() { return nombre; }
    public String getMunicipio() { return municipio; }
    public String getDirección() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }

    @Override
    public String toString() {
        return String.format("🏥 %s (%s) - Tel: %s", nombre, municipio, telefono);
    }
}