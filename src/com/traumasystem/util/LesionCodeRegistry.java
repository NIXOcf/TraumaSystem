package com.traumasystem.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Proporciona un registro estático e inmutable de códigos y nombres de lesiones,
 * que son cargados al iniciar la aplicación.
 */
public class LesionCodeRegistry {

    // Mapa inmutable que almacena los códigos de lesión como clave y sus denominaciones como valor.
    private static final Map<String, String> LESION_CODES;

    // Bloque estático de inicialización para cargar los códigos de lesión.
    static {
        Map<String, String> aMap = new HashMap<>();

        // TODO: Rellenar esta lista con todos los códigos y sus denominaciones
        // obtenidos del PDF "1 Libro Arancel MLE 2024.pdf".
        // Los ejemplos actuales son solo para demostración.

        // Códigos de la sección 21 04 (Rodilla, Pierna, Tobillo y Pie)
        aMap.put("21 04 090", "AMPUTACIÓN PULPEJOS (PLASTÍA KUTLER O SIMILARES)");
        aMap.put("21 04 091", "CONTRACTURA DUPUYTREN, TRAT. QUIR., CADA TIEMPO");
        aMap.put("21 04 092", "CONTUSIÓN-COMPRESIÓN GRAVE MANO, TRAT. QUIR. INCLUYE INCISIONES LIBERADORAS Y/O FASCIOTOMÍA Y/O ESCARECTOMÍA Y/O INJERTOS PIEL INMEDIATOS Y SÍNTESIS PERCUTÁNEA");
        aMap.put("21 04 093", "DEDOS EN GATILLO, TRAT. QUIR., CUALQUIER NÚMERO");
        aMap.put("21 04 094", "FLEGMÓN MANO, TRAT. QUIR.");
        aMap.put("21 04 095", "LUXOFRACTURA METACARPOFALÁNGICA O INTERFALÁNGICA, TRAT. QUIR.");
        aMap.put("21 04 096", "MANO REUMÁTICA EN RÁFAGA: TRASLOCACIONES TENDINOSAS, PLASTÍAS CAPSULARES, TENOTOMÍAS, INMOVILIZACIÓN POSTOPERATORIA");
        aMap.put("21 04 097", "MANO REUMÁTICA: IMPLANT. SILASTIC, CUALQ. NÚMERO (PROC. AUT.)");
        aMap.put("21 04 098", "MUTILACIÓN GRAVE MANO, ASEO. QUIR. COMPLETO C/S OSTEOSÍNTESIS, C/S INJERTOS");
        aMap.put("21 04 099", "OSTEOSÍNTESIS METACARPIANAS O DE FALANGES, CUALQUIER TÉCNICA");
        aMap.put("21 04 100", "PANADIZO, TRAT. QUIR.");
        aMap.put("21 04 101", "PULGARIZACIÓN DEDO (ÍNDICE O ANULAR)");
        aMap.put("21 04 102", "REIMPLANTE MANO O DEDO(S)");
        aMap.put("21 04 103", "REPARACIÓN FLEXORES: PRIMER TIEMPO ESPACIADOR SILASTIC");
        aMap.put("21 04 104", "REPARACIÓN NERVIO DIGITAL CON INJERTO INTERFASCICULAR: CUALQUIER NÚMERO");
        aMap.put("21 04 105", "RUPTURAS CERRADAS CÁPSULO-LIGAMENT. O TENDINOSAS, TRAT. QUIR. MANO");
        aMap.put("21 04 106", "SUTURA NERVIO(S) DIGITAL(ES); MICROCIRUGÍA");
        aMap.put("21 04 107", "TENORRAFIA EXTENSORES MANO");
        aMap.put("21 04 108", "TENORRAFIA O INJERTOS FLEXORES MANO");
        aMap.put("21 04 109", "TENOSINOVITIS SÉPTICA, TRAT. QUIR. MANO");
        aMap.put("21 04 110", "TRASPLANTE MICROQUIRÚRGICO PARA PULGAR");
        aMap.put("21 04 111", "TRANSPOSICIONES TENDINOSAS FLEXORAS O EXTENSORAS MANO");
        aMap.put("21 04 203", "TRATAMIENTO QUIR., DEDOS EN GATILLO, CUALQUIER NÚMERO TÉC. WALANT (ANESTESIA LOCAL SIN TORNIQUETE)");

        // Se hace inmutable el mapa para garantizar que no pueda ser modificado externamente.
        LESION_CODES = Collections.unmodifiableMap(aMap);
    }

    /**
     * Retorna un mapa inmutable de todos los códigos de lesión y sus denominaciones.
     * La clave del mapa es el código de lesión y el valor es el nombre o descripción de la lesión.
     *
     * @return Un {@link Map} inmutable con los códigos y nombres de las lesiones.
     */
    public static Map<String, String> getLesionCodes() {
        return LESION_CODES;
    }

    /**
     * Busca lesiones que coincidan con un término dado, ya sea por su código o por su nombre.
     * La búsqueda no distingue entre mayúsculas y minúsculas.
     * Si el término de búsqueda es nulo o vacío, devuelve una copia de todos los códigos de lesión.
     *
     * @param searchTerm El texto a buscar, que puede ser un código de lesión parcial o parte de un nombre de lesión.
     * @return Un {@link Map} de códigos y nombres de lesiones que coinciden con el término de búsqueda.
     */
    public static Map<String, String> searchLesionCodes(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new HashMap<>(LESION_CODES); // Retorna una copia de todos los códigos si la búsqueda es vacía.
        }
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        Map<String, String> results = new HashMap<>();
        for (Map.Entry<String, String> entry : LESION_CODES.entrySet()) {
            // Compara el término de búsqueda con la clave (código) o el valor (nombre) de cada entrada.
            if (entry.getKey().toLowerCase().contains(lowerCaseSearchTerm) ||
                    entry.getValue().toLowerCase().contains(lowerCaseSearchTerm)) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
}