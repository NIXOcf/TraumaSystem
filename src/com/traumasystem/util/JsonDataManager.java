package com.traumasystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traumasystem.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gestiona la persistencia de datos de pacientes en formato JSON.
 * Se encarga de cargar, guardar, actualizar y eliminar registros de pacientes
 * desde y hacia un archivo JSON especificado.
 */
public class JsonDataManager {
    private final File dataFile;
    private final ObjectMapper objectMapper;

    /**
     * Construye un {@code JsonDataManager} para manejar un archivo JSON específico.
     * Configura el {@link ObjectMapper} para serializar y deserializar correctamente
     * objetos {@link Patient}, incluyendo el soporte para {@link java.time.LocalDate}.
     *
     * @param filePath La ruta completa del archivo JSON donde se almacenarán los datos de los pacientes.
     */
    public JsonDataManager(String filePath) {
        this.dataFile = new File(filePath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Habilita el soporte para tipos de fecha de Java 8.
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Formatea el JSON para que sea legible (indentado).
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Guarda fechas como cadenas ISO-8601 en lugar de timestamps.
    }

    /**
     * Carga todos los pacientes desde el archivo JSON.
     * Si el archivo no existe o está vacío, retorna una lista vacía para evitar errores.
     *
     * @return Una {@link List} de objetos {@link Patient} cargados desde el archivo,
     * o una lista vacía si el archivo no existe o no contiene datos.
     * @throws IOException Si ocurre un error de entrada/salida al leer el archivo.
     */
    public List<Patient> loadPatients() throws IOException {
        if (!dataFile.exists() || dataFile.length() == 0) {
            return new ArrayList<>();
        }
        // Lee el contenido del archivo JSON y lo mapea a un array de Patient, luego lo convierte a una lista.
        Patient[] patientsArray = objectMapper.readValue(dataFile, Patient[].class);
        return new ArrayList<>(Arrays.asList(patientsArray));
    }

    /**
     * Guarda la lista completa de pacientes en el archivo JSON, sobrescribiendo el contenido existente.
     * Este es un método auxiliar interno utilizado por otras operaciones de persistencia.
     *
     * @param patients La {@link List} de objetos {@link Patient} a guardar.
     * @throws IOException Si ocurre un error de entrada/salida al escribir en el archivo.
     */
    private void saveAllPatients(List<Patient> patients) throws IOException {
        objectMapper.writeValue(dataFile, patients);
    }

    /**
     * Agrega un nuevo paciente a la colección existente y luego guarda la colección actualizada en el archivo JSON.
     *
     * @param newPatient El objeto {@link Patient} a guardar.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir el archivo.
     */
    public void savePatient(Patient newPatient) throws IOException {
        List<Patient> patients = loadPatients(); // Carga la lista actual de pacientes.
        patients.add(newPatient); // Agrega el nuevo paciente.
        saveAllPatients(patients); // Guarda la lista completa de vuelta en el archivo.
    }

    /**
     * Actualiza un paciente existente en la lista y guarda la colección actualizada.
     * Un paciente es identificado por su ID único. Si el paciente no es encontrado,
     * se imprime una advertencia en la consola de errores.
     *
     * @param updatedPatient El objeto {@link Patient} con los datos actualizados.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir el archivo.
     */
    public void updatePatient(Patient updatedPatient) throws IOException {
        List<Patient> patients = loadPatients(); // Carga la lista actual de pacientes.
        boolean found = false;
        // Itera para encontrar y reemplazar el paciente por su ID.
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(updatedPatient.getId())) {
                patients.set(i, updatedPatient);
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("Advertencia: Paciente con ID " + updatedPatient.getId() + " no encontrado para actualizar.");
        }
        saveAllPatients(patients); // Guarda la lista, incluso si no se encontró el paciente (ya que no se modificó).
    }

    /**
     * Elimina un paciente de la colección por su ID y guarda la lista modificada.
     * Si el paciente no es encontrado, se imprime un mensaje informativo en la consola de errores.
     *
     * @param patientId El ID del paciente a eliminar.
     * @throws IOException Si ocurre un error de entrada/salida al leer o escribir el archivo.
     */
    public void deletePatient(String patientId) throws IOException {
        List<Patient> patients = loadPatients(); // Carga la lista actual de pacientes.
        // Intenta remover el paciente cuyo ID coincide.
        boolean removed = patients.removeIf(p -> p.getId().equals(patientId));
        if (removed) {
            saveAllPatients(patients); // Si se eliminó, guarda la lista modificada.
        } else {
            System.err.println("Paciente con ID " + patientId + " no encontrado para eliminar.");
        }
    }
}