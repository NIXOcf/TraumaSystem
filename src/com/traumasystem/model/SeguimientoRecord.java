package com.traumasystem.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa un registro de seguimiento para un paciente, conteniendo datos
 * de evaluación en una fecha específica.
 */
public class SeguimientoRecord {
    private String id;
    private LocalDate fechaEvaluacion;
    private double qdash;
    private double prwe;
    private int evaReposo;
    private int evaActividad;
    private String rom;
    private double fGrip;
    private double aperturaSL;
    private boolean disi;
    private boolean subluxacionDorsalEscafoides;
    private String observaciones;

    /**
     * Constructor por defecto, requerido por Jackson para la deserialización.
     */
    public SeguimientoRecord() {
    }

    /**
     * Obtiene el identificador único del registro de seguimiento.
     *
     * @return El ID del registro de seguimiento.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador único del registro de seguimiento.
     *
     * @param id El nuevo ID del registro de seguimiento.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene la fecha de la evaluación de este registro.
     *
     * @return La fecha de evaluación.
     */
    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    /**
     * Establece la fecha de la evaluación de este registro.
     *
     * @param fechaEvaluacion La nueva fecha de evaluación.
     */
    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    /**
     * Obtiene la puntuación QDASH (Quick Disabilities of the Arm, Shoulder and Hand).
     *
     * @return La puntuación QDASH.
     */
    public double getQdash() {
        return qdash;
    }

    /**
     * Establece la puntuación QDASH.
     *
     * @param qdash La nueva puntuación QDASH.
     */
    public void setQdash(double qdash) {
        this.qdash = qdash;
    }

    /**
     * Obtiene la puntuación PRWE (Patient-Rated Wrist Evaluation).
     *
     * @return La puntuación PRWE.
     */
    public double getPrwe() {
        return prwe;
    }

    /**
     * Establece la puntuación PRWE.
     *
     * @param prwe La nueva puntuación PRWE.
     */
    public void setPrwe(double prwe) {
        this.prwe = prwe;
    }

    /**
     * Obtiene el valor de la Escala Visual Analógica (EVA) en reposo.
     *
     * @return El valor de EVA en reposo.
     */
    public int getEvaReposo() {
        return evaReposo;
    }

    /**
     * Establece el valor de la Escala Visual Analógica (EVA) en reposo.
     *
     * @param evaReposo El nuevo valor de EVA en reposo.
     */
    public void setEvaReposo(int evaReposo) {
        this.evaReposo = evaReposo;
    }

    /**
     * Obtiene el valor de la Escala Visual Analógica (EVA) en actividad.
     *
     * @return El valor de EVA en actividad.
     */
    public int getEvaActividad() {
        return evaActividad;
    }

    /**
     * Establece el valor de la Escala Visual Analógica (EVA) en actividad.
     *
     * @param evaActividad El nuevo valor de EVA en actividad.
     */
    public void setEvaActividad(int evaActividad) {
        this.evaActividad = evaActividad;
    }

    /**
     * Obtiene el rango de movilidad (ROM).
     *
     * @return El rango de movilidad.
     */
    public String getRom() {
        return rom;
    }

    /**
     * Establece el rango de movilidad (ROM).
     *
     * @param rom El nuevo rango de movilidad.
     */
    public void setRom(String rom) {
        this.rom = rom;
    }

    /**
     * Obtiene la fuerza de agarre (fGrip).
     *
     * @return La fuerza de agarre.
     */
    public double getfGrip() {
        return fGrip;
    }

    /**
     * Establece la fuerza de agarre (fGrip).
     *
     * @param fGrip La nueva fuerza de agarre.
     */
    public void setfGrip(double fGrip) {
        this.fGrip = fGrip;
    }

    /**
     * Obtiene la apertura entre el escafoides y el semilunar (AperturaSL).
     *
     * @return La apertura entre escafoides y semilunar.
     */
    public double getAperturaSL() {
        return aperturaSL;
    }

    /**
     * Establece la apertura entre el escafoides y el semilunar (AperturaSL).
     *
     * @param aperturaSL La nueva apertura entre escafoides y semilunar.
     */
    public void setAperturaSL(double aperturaSL) {
        this.aperturaSL = aperturaSL;
    }

    /**
     * Verifica si hay inestabilidad disociativa del carpo dorsal (DISI).
     *
     * @return true si hay DISI, false en caso contrario.
     */
    public boolean isDisi() {
        return disi;
    }

    /**
     * Establece el estado de la inestabilidad disociativa del carpo dorsal (DISI).
     *
     * @param disi true para indicar DISI, false para no DISI.
     */
    public void setDisi(boolean disi) {
        this.disi = disi;
    }

    /**
     * Verifica si hay subluxación dorsal del escafoides.
     *
     * @return true si hay subluxación dorsal del escafoides, false en caso contrario.
     */
    public boolean isSubluxacionDorsalEscafoides() {
        return subluxacionDorsalEscafoides;
    }

    /**
     * Establece el estado de la subluxación dorsal del escafoides.
     *
     * @param subluxacionDorsalEscafoides true para indicar subluxación, false para no subluxación.
     */
    public void setSubluxacionDorsalEscafoides(boolean subluxacionDorsalEscafoides) {
        this.subluxacionDorsalEscafoides = subluxacionDorsalEscafoides;
    }

    /**
     * Obtiene las observaciones adicionales del seguimiento.
     *
     * @return Las observaciones del seguimiento.
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones adicionales del seguimiento.
     *
     * @param observaciones Las nuevas observaciones del seguimiento.
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna una representación en cadena de este objeto SeguimientoRecord,
     * incluyendo el ID, fecha de evaluación, QDASH y PRWE.
     *
     * @return Una cadena que representa los atributos principales del registro de seguimiento.
     */
    @Override
    public String toString() {
        return "SeguimientoRecord{" +
                "id='" + id + '\'' +
                ", fechaEvaluacion=" + fechaEvaluacion +
                ", qdash=" + qdash +
                ", prwe=" + prwe +
                '}';
    }

    /**
     * Compara este objeto SeguimientoRecord con otro para determinar si son iguales.
     * La igualdad se basa únicamente en el ID único del registro.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si los objetos son iguales por su ID, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeguimientoRecord that = (SeguimientoRecord) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calcula el valor hash para este objeto SeguimientoRecord.
     * El valor hash se calcula a partir del ID único del registro.
     *
     * @return El valor hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}