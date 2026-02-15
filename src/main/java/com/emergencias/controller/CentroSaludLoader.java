package main.java.com.emergencias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import main.java.com.emergencias.model.CentroSalud;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la persistencia y carga de datos JSON[cite: 3, 39].
 */
public class CentroSaludLoader {

    /**
     * Carga los centros de salud desde un fichero JSON a una lista dinámica[cite: 22, 39].
     * @return List de centros de salud.
     */
    public List<CentroSalud> cargarCentros() {
        ObjectMapper mapper = new ObjectMapper();
        List<CentroSalud> listaCentros = new ArrayList<>(); // Estructura dinámica [cite: 2]

        try {
            // Se lee el fichero en bloque y se traslada directamente al objeto [cite: 39]
            File archivo = new File("src/main/resources/centros.json");

            // Usamos el operador Diamond para establecer el tipo de elementos [cite: 25]
            listaCentros = mapper.readValue(archivo, new TypeReference<ArrayList<CentroSalud>>() {});

        } catch (IOException e) {
            System.err.println("Error al cargar los datos externos: " + e.getMessage());
        }

        return listaCentros;
    }
}