package com.traumasystem.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Proporciona métodos de utilidad para la validación y formato de RUT chilenos.
 */
public class ChileanValidator {

    // Patrón de expresión regular para validar el formato básico de un RUT chileno (ej: XX.XXX.XXX-X o X.XXX.XXX-X).
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}[-][0-9kK]$");

    /**
     * Valida un RUT chileno, incluyendo el formato y el dígito verificador.
     * Este método puede procesar RUTs con o sin puntos y con o sin guion, intentando limpiarlos primero.
     *
     * @param rut El RUT a validar (ej: "12345678-9", "12.345.678-9", "12345678K").
     * @return {@code true} si el RUT es válido, {@code false} en caso contrario.
     */
    public static boolean validateRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        String cleanRut = rut.trim().toUpperCase();
        cleanRut = cleanRut.replace(".", "");

        // Si no tiene guion, intenta añadirlo antes de validar el formato general, asumiendo el último es el DV.
        if (!cleanRut.contains("-")) {
            if (cleanRut.length() > 1) {
                cleanRut = cleanRut.substring(0, cleanRut.length() - 1) + "-" + cleanRut.substring(cleanRut.length() - 1);
            } else {
                return false; // Muy corto para ser un RUT válido sin guion.
            }
        }

        // Verifica el formato básico con la expresión regular.
        if (!RUT_PATTERN.matcher(cleanRut).matches()) {
            return false;
        }

        // Separa el cuerpo numérico del dígito verificador.
        int rutNum;
        char dv;
        try {
            int guionIndex = cleanRut.lastIndexOf('-');
            rutNum = Integer.parseInt(cleanRut.substring(0, guionIndex));
            dv = cleanRut.charAt(guionIndex + 1);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return false; // Error si la parte numérica no es válida o el formato es inesperado.
        }

        // Calcula el dígito verificador esperado usando el algoritmo de Módulo 11.
        int m = 0, s = 1;
        for (; rutNum != 0; rutNum /= 10) {
            s = (s + rutNum % 10 * (9 - m++ % 6)) % 11;
        }
        char calculatedDv = (char) (s != 0 ? s + 47 : 75); // Convierte el resultado a su carácter ASCII ('0'-'9' o 'K').

        // Compara el dígito verificador ingresado con el calculado.
        return dv == calculatedDv;
    }

    /**
     * Formatea un RUT chileno agregando puntos y guion en el formato estándar (ej: "XX.XXX.XXX-X").
     * Este método no valida la corrección del RUT; solo su presentación.
     *
     * @param rut El RUT a formatear (ej: "123456789", "12345678-9", "1.234.567-8").
     * @return El RUT formateado (ej: "12.345.678-9") o el mismo RUT si no se pudo formatear
     * debido a un formato de entrada muy irregular.
     */
    public static String formatRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return "";
        }

        String cleanRut = rut.trim().toUpperCase().replace(".", "");
        String rutBody;
        String dv;

        int guionIndex = cleanRut.lastIndexOf('-');
        if (guionIndex != -1) {
            rutBody = cleanRut.substring(0, guionIndex);
            dv = cleanRut.substring(guionIndex + 1);
        } else { // Si no tiene guion, asume que el último caracter es el dígito verificador.
            if (cleanRut.length() < 2) {
                return rut; // Demasiado corto para formatear adecuadamente.
            }
            rutBody = cleanRut.substring(0, cleanRut.length() - 1);
            dv = cleanRut.substring(cleanRut.length() - 1);
        }

        // Formatea el cuerpo numérico del RUT con puntos.
        StringBuilder formattedBody = new StringBuilder(rutBody);
        int len = formattedBody.length();
        if (len > 3) {
            formattedBody.insert(len - 3, '.');
        }
        if (len > 6) {
            formattedBody.insert(len - 6, '.');
        }

        return formattedBody.toString() + "-" + dv;
    }

    /**
     * Limpia un RUT de puntos y guiones, retornando solo los números y el dígito verificador
     * en un formato estandarizado (ej: "12345678-9" o "12345678K").
     * Los guiones intermedios son eliminados, manteniendo solo el guion antes del dígito verificador final.
     *
     * @param rut El RUT a limpiar (ej: "12.345.678-9", "12345678-9", "12345678K").
     * @return El RUT limpio y estandarizado, o una cadena vacía si la entrada es nula.
     */
    public static String cleanRut(String rut) {
        if (rut == null) {
            return "";
        }
        String cleaned = rut.trim().toUpperCase().replace(".", "");

        // Si el RUT ya tiene un guion en la posición esperada (antes del DV), lo mantenemos.
        // De lo contrario, se asegura que el formato final tenga el DV correctamente separado por un guion.
        if (cleaned.length() > 1) {
            int lastGuion = cleaned.lastIndexOf('-');
            if (lastGuion == cleaned.length() - 2) { // Guion antes del último caracter (DV).
                return cleaned;
            } else { // Si hay guiones en otro lugar o no hay.
                String numPart = cleaned.replaceAll("[^0-9K]", ""); // Extrae solo números y 'K'.
                if (numPart.length() > 1) {
                    // Reconstruye el RUT con el guion antes del último caracter (DV).
                    return numPart.substring(0, numPart.length() - 1) + "-" + numPart.substring(numPart.length() - 1);
                }
                return numPart; // Si es solo 1 caracter (ej. 'K'), no tiene DV separado.
            }
        }
        return cleaned.replace("-", ""); // Remueve cualquier otro guion si es muy corto o no tiene DV.
    }
}