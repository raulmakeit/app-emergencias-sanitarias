package main.java.com.emergencias.model;

/**
 * Clase que representa los datos personales del usuario.
 * Se utiliza para empaquetar información relevante que
 * se enviará junto con la alerta.
 */
public class UserData {
    private String nombre;
    private String telefono;
    private String infoMedica; // E.g., Alergias, tipo de sangre

    /**
     * Constructor para inicializar los datos del usuario.
     * @param nombre Nombre completo del usuario.
     * @param telefono Número de contacto principal.
     * @param infoMedica Información médica relevante.
     */
    public UserData(String nombre, String telefono, String infoMedica) {
        if (nombre == null || nombre.trim().isEmpty() || telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre y el teléfono son campos obligatorios para el usuario.");
        }
        this.nombre = nombre;
        this.telefono = telefono;
        this.infoMedica = infoMedica != null ? infoMedica : "No especificada";
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getInfoMedica() {
        return infoMedica;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setInfoMedica(String infoMedica) {
        this.infoMedica = infoMedica;
    }

    /**
     * Devuelve una representación en cadena de los datos del usuario.
     * Es útil para la persistencia o el log.
     */
    @Override
    public String toString() {
        return String.format("Usuario: %s | Teléfono: %s | Info Médica: %s", nombre, telefono, infoMedica);
    }
}