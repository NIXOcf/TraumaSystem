package com.traumasystem.util;

import com.traumasystem.model.Patient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase de utilidad para generar reportes de pacientes en formato Excel.
 * Utiliza la librería Apache POI para la manipulación de archivos XLSX.
 */
public class ExcelReportGenerator {

    // Formateador de fechas para asegurar un formato consistente en el reporte Excel.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Exporta una lista de pacientes a un archivo Excel (.xlsx).
     * Permite al usuario seleccionar la ubicación y el nombre del archivo a guardar.
     *
     * @param patients Una lista de objetos {@link Patient} a exportar.
     * @param parentFrame El {@link JFrame} padre para centrar el cuadro de diálogo de guardar archivo
     * y mostrar mensajes de confirmación/error.
     */
    public static void exportPatientsToExcel(List<Patient> patients, JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Pacientes");
        fileChooser.setSelectedFile(new java.io.File("ReportePacientes.xlsx")); // Nombre de archivo sugerido por defecto.
        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            // Asegura que el archivo tenga la extensión .xlsx
            if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            // Bloque try-with-resources para asegurar el cierre automático del workbook y FileOutputStream.
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(fileToSave)) {

                Sheet sheet = workbook.createSheet("Pacientes Trauma");

                // Configuración del estilo para la fila de encabezado.
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());

                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Creación de la fila de encabezado.
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                        "RUT", "Nombre", "Código Lesión", "Nombre Lesión", "Diagnóstico",
                        "Fecha Cirugía", "Delay QX (días)", "Tipo de CX", "Estado Recuperación"
                };
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Configuración del estilo para las celdas de datos.
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setWrapText(true); // Permite que el texto se ajuste dentro de la celda.

                // Rellenar las filas con los datos de los pacientes.
                int rowNum = 1; // La primera fila (índice 0) es para los encabezados.
                for (Patient patient : patients) {
                    Row row = sheet.createRow(rowNum++);

                    // Prepara los datos del paciente, manejando posibles valores nulos para la lesión.
                    String rut = ChileanValidator.formatRut(patient.getRut());
                    String nombreLesion = (patient.getLesion() != null) ? patient.getLesion().getNombreLesion() : "N/A";
                    String codigoLesion = (patient.getLesion() != null) ? patient.getLesion().getCodigoOficial() : "N/A";
                    String diagnostico = (patient.getLesion() != null) ? patient.getLesion().getDiagnostico() : "N/A";
                    String fechaCirugia = patient.getFechaCirugia() != null ? patient.getFechaCirugia().format(DATE_FORMATTER) : "N/A";
                    String estadoRecuperacion = patient.isRecovered() ? "Recuperado" : "Activo";

                    // Asigna los valores a las celdas correspondientes.
                    row.createCell(0).setCellValue(rut);
                    row.createCell(1).setCellValue(patient.getNombre());
                    row.createCell(2).setCellValue(codigoLesion);
                    row.createCell(3).setCellValue(nombreLesion);
                    row.createCell(4).setCellValue(diagnostico);
                    row.createCell(5).setCellValue(fechaCirugia);
                    row.createCell(6).setCellValue(patient.getDelayQx());
                    row.createCell(7).setCellValue(patient.getTipoDeCx());
                    row.createCell(8).setCellValue(estadoRecuperacion);

                    // Aplica el estilo de datos a todas las celdas de la fila actual.
                    for (int i = 0; i < headers.length; i++) {
                        if (row.getCell(i) != null) {
                            row.getCell(i).setCellStyle(dataStyle);
                        }
                    }
                }

                // Ajusta automáticamente el ancho de todas las columnas para que el contenido sea visible.
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fos); // Escribe el libro de trabajo en el archivo.

                // Muestra un mensaje de éxito al usuario.
                JOptionPane.showMessageDialog(parentFrame,
                        "Reporte de pacientes exportado exitosamente a:\n" + fileToSave.getAbsolutePath(),
                        "Exportación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                // Muestra un mensaje de error si ocurre una excepción de E/S.
                JOptionPane.showMessageDialog(parentFrame,
                        "Error al exportar el reporte a Excel: " + ex.getMessage(),
                        "Error de Exportación",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprime la pila de llamadas para depuración.
            }
        }
    }
}