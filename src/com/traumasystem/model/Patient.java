package com.traumasystem.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un paciente dentro del sistema de trauma, incluyendo sus datos personales,
 * información de la lesión, detalles de la cirugía y estado de recuperación.
 */
public class Patient {
    private String id;
    private String nombre;
    private int edad;
    private String rut;
    private String dominancia;
    private Lesion lesion;
    private int delayQx;
    private LocalDate fechaCirugia;
    private String tipoDeCx;
    private boolean recovered;

    /**
     * Constructor por defecto requerido por Jackson para la deserialización.
     */


    /**
     * Constructor para crear una nueva instancia de Patient con todos sus atributos.
     * Genera automáticamente un ID único para cada paciente.
     * Por defecto, el paciente no se considera recuperado.
     *
     * @param nombre El nombre completo del paciente.
     * @param edad La edad del paciente.
     * @param rut El Rol Único Tributario (RUT) del paciente.
     * @param dominancia La mano o pie dominante del paciente (ej. "Derecha", "Izquierda").
     * @param nombreLesion El nombre descriptivo de la lesión.
     * @param codigoOficialLesion El código oficial que identifica la lesión.
     * @param diagnostico El diagnóstico asociado a la lesión.
     * @param delayQx Los días de espera estimados para la cirugía.
     * @param fechaCirugia La fecha programada o realizada de la cirugía.
     * @param tipoDeCx El tipo de cirugía realizada (ej. "Primaria", "Revisión").
     */
    public Patient(String nombre, int edad, String rut, String dominancia,
                   String nombreLesion, String codigoOficialLesion, String diagnostico,
                   int delayQx, LocalDate fechaCirugia, String tipoDeCx) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.edad = edad;
        this.rut = rut;
        this.dominancia = dominancia;
        this.lesion = new Lesion(nombreLesion, codigoOficialLesion, diagnostico);
        this.delayQx = delayQx;
        this.fechaCirugia = fechaCirugia;
        this.tipoDeCx = tipoDeCx;
        this.recovered = false;
    }

    /**
     * Obtiene el identificador único del paciente.
     *
     * @return El ID del paciente.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador único del paciente.
     * Utilizado principalmente por librerías de serialización como Jackson.
     *
     * @param id El nuevo ID del paciente.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre completo del paciente.
     *
     * @return El nombre del paciente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del paciente.
     *
     * @param nombre El nuevo nombre del paciente.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la edad del paciente.
     *
     * @return La edad del paciente.
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Establece la edad del paciente.
     *
     * @param edad La nueva edad del paciente.
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Obtiene el RUT del paciente.
     *
     * @return El RUT del paciente.
     */
    public String getRut() {
        return rut;
    }

    /**
     * Establece el RUT del paciente.
     *
     * @param rut El nuevo RUT del paciente.
     */
    public void setRut(String rut) {
        this.rut = rut;
    }

    /**
     * Obtiene la dominancia del paciente.
     *
     * @return La dominancia del paciente.
     */
    public String getDominancia() {
        return dominancia;
    }

    /**
     * Establece la dominancia del paciente.
     *
     * @param dominancia La nueva dominancia del paciente.
     */
    public void setDominancia(String dominancia) {
        this.dominancia = dominancia;
    }

    /**
     * Obtiene el objeto Lesion asociado al paciente.
     *
     * @return El objeto Lesion del paciente.
     */
    public Lesion getLesion() {
        return lesion;
    }

    /**
     * Establece el objeto Lesion asociado al paciente.
     *
     * @param lesion El nuevo objeto Lesion del paciente.
     */
    public void setLesion(Lesion lesion) {
        this.lesion = lesion;
    }

    /**
     * Obtiene los días de espera para la cirugía (delayQx).
     *
     * @return Los días de espera para la cirugía.
     */
    public int getDelayQx() {
        return delayQx;
    }

    /**
     * Establece los días de espera para la cirugía (delayQx).
     *
     * @param delayQx Los nuevos días de espera para la cirugía.
     */
    public void setDelayQx(int delayQx) {
        this.delayQx = delayQx;
    }

    /**
     * Obtiene la fecha de la cirugía.
     *
     * @return La fecha de la cirugía.
     */
    public LocalDate getFechaCirugia() {
        return fechaCirugia;
    }

    /**
     * Establece la fecha de la cirugía.
     *
     * @param fechaCirugia La nueva fecha de la cirugía.
     */
    public void setFechaCirugia(LocalDate fechaCirugia) {
        this.fechaCirugia = fechaCirugia;
    }

    /**
     * Obtiene el tipo de cirugía realizada (tipoDeCx).
     *
     * @return El tipo de cirugía.
     */
    public String getTipoDeCx() {
        return tipoDeCx;
    }

    /**
     * Establece el tipo de cirugía realizada (tipoDeCx).
     *
     * @param tipoDeCx El nuevo tipo de cirugía.
     */
    public void setTipoDeCx(String tipoDeCx) {
        this.tipoDeCx = tipoDeCx;
    }

    /**
     * Verifica si el paciente ha sido marcado como recuperado.
     *
     * @return true si el paciente está recuperado, false en caso contrario.
     */
    public boolean isRecovered() {
        return recovered;
    }

    /**
     * Establece el estado de recuperación del paciente.
     *
     * @param recovered true para marcar como recuperado, false para no recuperado.
     */
    public void setRecovered(boolean recovered) {
        this.recovered = recovered;
    }

    /**
     * Compara este objeto Patient con otro para determinar si son iguales.
     * La igualdad se basa únicamente en el ID único del paciente.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si los objetos son iguales por su ID, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    /**
     * Calcula el valor hash para este objeto Patient.
     * El valor hash se calcula a partir del ID único del paciente.
     *
     * @return El valor hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna una representación en cadena de este objeto Patient,
     * incluyendo todos sus atributos.
     *
     * @return Una cadena que representa los atributos del paciente.
     */
    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", rut='" + rut + '\'' +
                ", dominancia='" + dominancia + '\'' +
                ", lesion=" + lesion +
                ", delayQx=" + delayQx +
                ", fechaCirugia=" + fechaCirugia +
                ", tipoDeCx='" + tipoDeCx + '\'' +
                ", recovered=" + recovered +
                '}';
    }
}