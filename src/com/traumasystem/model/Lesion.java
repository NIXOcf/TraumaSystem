package com.traumasystem.model;

import java.util.Objects;

/**
 * Representa la información detallada de una lesión, incluyendo su nombre,
 * código oficial y diagnóstico.
 */
public class Lesion {
    private String nombreLesion;
    private String codigoOficial;
    private String diagnostico;

    /**
     * Constructor por defecto requerido por Jackson para la deserialización.
     */
    public Lesion() {
    }

    /**
     * Constructor para crear una instancia de Lesion con todos sus atributos.
     *
     * @param nombreLesion El nombre descriptivo de la lesión.
     * @param codigoOficial El código oficial que identifica la lesión.
     * @param diagnostico El diagnóstico asociado a la lesión.
     */
    public Lesion(String nombreLesion, String codigoOficial, String diagnostico) {
        this.nombreLesion = nombreLesion;
        this.codigoOficial = codigoOficial;
        this.diagnostico = diagnostico;
    }

    /**
     * Obtiene el nombre de la lesión.
     *
     * @return El nombre de la lesión.
     */
    public String getNombreLesion() {
        return nombreLesion;
    }

    /**
     * Establece el nombre de la lesión.
     *
     * @param nombreLesion El nuevo nombre de la lesión.
     */
    public void setNombreLesion(String nombreLesion) {
        this.nombreLesion = nombreLesion;
    }

    /**
     * Obtiene el código oficial de la lesión.
     *
     * @return El código oficial de la lesión.
     */
    public String getCodigoOficial() {
        return codigoOficial;
    }

    /**
     * Establece el código oficial de la lesión.
     *
     * @param codigoOficial El nuevo código oficial de la lesión.
     */
    public void setCodigoOficial(String codigoOficial) {
        this.codigoOficial = codigoOficial;
    }

    /**
     * Obtiene el diagnóstico de la lesión.
     *
     * @return El diagnóstico de la lesión.
     */
    public String getDiagnostico() {
        return diagnostico;
    }

    /**
     * Establece el diagnóstico de la lesión.
     *
     * @param diagnostico El nuevo diagnóstico de la lesión.
     */
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    /**
     * Compara este objeto Lesion con otro para determinar si son iguales.
     * La igualdad se basa en el nombre de la lesión, el código oficial y el diagnóstico.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesion lesion = (Lesion) o;
        return Objects.equals(nombreLesion, lesion.nombreLesion) &&
                Objects.equals(codigoOficial, lesion.codigoOficial) &&
                Objects.equals(diagnostico, lesion.diagnostico);
    }

    /**
     * Calcula el valor hash para este objeto Lesion.
     * El valor hash se calcula a partir del nombre de la lesión, el código oficial y el diagnóstico.
     *
     * @return El valor hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombreLesion, codigoOficial, diagnostico);
    }

    /**
     * Retorna una representación en cadena de este objeto Lesion.
     *
     * @return Una cadena que representa los atributos de la lesión.
     */
    @Override
    public String toString() {
        return "Lesion{" +
                "nombreLesion='" + nombreLesion + '\'' +
                ", codigoOficial='" + codigoOficial + '\'' +
                ", diagnostico='" + diagnostico + '\'' +
                '}';
    }
}