package com.traumasystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traumasystem.model.Patient;
import com.traumasystem.util.ChileanValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatientController {
    private final String dataDirectory;
    private final ObjectMapper objectMapper;

    /**
     * Constructor de PatientController.
     * Inicializa el directorio de datos y configura el ObjectMapper para la serialización JSON.
     * Crea el directorio de datos si no existe.
     *
     * @param dataDirectory La ruta del directorio donde se almacenarán los datos del paciente.
     */
    public PatientController(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Retorna la ruta del archivo para un ID de paciente dado.
     *
     * @param patientId El ID del paciente.
     * @return El objeto Path que representa el archivo JSON del paciente.
     */
    private Path getPatientFilePath(String patientId) {
        return Paths.get(dataDirectory, patientId + ".json");
    }

    /**
     * Crea y guarda un nuevo paciente.
     *
     * @param nombre El nombre del paciente.
     * @param edad La edad del paciente.
     * @param rut El RUT del paciente (número de identificación chileno).
     * @param dominancia El lado dominante del paciente (ej. "derecho", "izquierdo").
     * @param nombreLesion El nombre de la lesión del paciente.
     * @param codigoOficialLesion El código oficial de la lesión.
     * @param diagnostico El diagnóstico del paciente.
     * @param delayQx El retraso en días hasta la cirugía.
     * @param fechaCirugia La fecha de la cirugía.
     * @param tipoDeCx El tipo de cirugía.
     * @throws IOException Si ocurre un error de E/S durante el guardado.
     */
    public void createPatient(String nombre, int edad, String rut, String dominancia,
                              String nombreLesion, String codigoOficialLesion, String diagnostico, int delayQx,
                              LocalDate fechaCirugia, String tipoDeCx) throws IOException {
        Patient newPatient = new Patient(nombre, edad, rut, dominancia, nombreLesion,
                codigoOficialLesion, diagnostico, delayQx, fechaCirugia, tipoDeCx);
        savePatient(newPatient);
    }

    /**
     * Guarda un objeto paciente en un archivo JSON.
     *
     * @param patient El objeto paciente a guardar.
     * @throws IOException Si ocurre un error de E/S durante el guardado.
     */
    public void savePatient(Patient patient) throws IOException {
        objectMapper.writeValue(getPatientFilePath(patient.getId()).toFile(), patient);
    }

    /**
     * Recupera un paciente por su ID.
     *
     * @param patientId El ID del paciente a recuperar.
     * @return El objeto Patient si se encuentra, de lo contrario null.
     * @throws IOException Si ocurre un error de E/S durante la lectura.
     */
    public Patient getPatient(String patientId) throws IOException {
        Path filePath = getPatientFilePath(patientId);
        if (Files.exists(filePath)) {
            return objectMapper.readValue(filePath.toFile(), Patient.class);
        }
        return null;
    }

    /**
     * Recupera todos los registros de pacientes del directorio de datos.
     *
     * @return Una lista de todos los objetos Patient.
     * @throws IOException Si ocurre un error de E/S durante la lectura.
     */
    public List<Patient> getAllPatients() throws IOException {
        List<Patient> patients = new ArrayList<>();
        try (var stream = Files.list(Paths.get(dataDirectory))) {
            for (Path path : stream.filter(p -> p.toString().endsWith(".json")).collect(Collectors.toList())) {
                try {
                    patients.add(objectMapper.readValue(path.toFile(), Patient.class));
                } catch (Exception e) {
                    System.err.println("Error al leer archivo de paciente " + path.getFileName() + ": " + e.getMessage());
                }
            }
        }
        return patients;
    }

    /**
     * Recupera todos los registros de pacientes activos (no marcados como recuperados).
     *
     * @return Una lista de objetos Patient activos.
     * @throws IOException Si ocurre un error de E/S durante la lectura.
     */
    public List<Patient> getAllActivePatients() throws IOException {
        return getAllPatients().stream()
                .filter(patient -> !patient.isRecovered())
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el registro de un paciente existente.
     *
     * @param patient El objeto paciente a actualizar.
     * @throws IOException Si ocurre un error de E/S durante el guardado.
     */
    public void updatePatient(Patient patient) throws IOException {
        savePatient(patient);
    }

    /**
     * Elimina el registro de un paciente por su ID.
     *
     * @param patientId El ID del paciente a eliminar.
     * @throws IOException Si ocurre un error de E/S durante la eliminación.
     */
    public void deletePatient(String patientId) throws IOException {
        Files.deleteIfExists(getPatientFilePath(patientId));
    }

    /**
     * Marca un paciente como recuperado y actualiza su registro.
     *
     * @param patient El objeto paciente a marcar como recuperado.
     * @throws IOException Si ocurre un error de E/S durante el guardado.
     */
    public void markPatientAsRecovered(Patient patient) throws IOException {
        patient.setRecovered(true);
        updatePatient(patient);
    }

    /**
     * Marca un paciente como activo (no recuperado) y actualiza su registro.
     *
     * @param patient El objeto paciente a marcar como activo.
     * @throws IOException Si ocurre un error de E/S durante el guardado.
     */
    public void markPatientAsActive(Patient patient) throws IOException {
        patient.setRecovered(false);
        updatePatient(patient);
    }

    /**
     * Busca pacientes cuyo nombre, RUT, nombre de lesión o código oficial de lesión
     * contengan el término de búsqueda dado (sin distinción entre mayúsculas y minúsculas).
     *
     * @param searchTerm El término a buscar.
     * @return Una lista de pacientes que coinciden con los criterios de búsqueda.
     * @throws IOException Si ocurre un error de E/S durante la lectura de datos del paciente.
     */
    public List<Patient> searchPatients(String searchTerm) throws IOException {
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return getAllPatients().stream()
                .filter(patient ->
                        patient.getNombre().toLowerCase().contains(lowerCaseSearchTerm) ||
                                ChileanValidator.cleanRut(patient.getRut()).toLowerCase().contains(ChileanValidator.cleanRut(lowerCaseSearchTerm)) ||
                                (patient.getLesion() != null &&
                                        (patient.getLesion().getNombreLesion().toLowerCase().contains(lowerCaseSearchTerm) ||
                                                patient.getLesion().getCodigoOficial().toLowerCase().contains(lowerCaseSearchTerm))))
                .collect(Collectors.toList());
    }
}