package com.traumasystem.view;

import com.traumasystem.controller.PatientController;
import com.traumasystem.model.Patient;
import com.traumasystem.util.ChileanValidator;


import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Diálogo para realizar búsquedas avanzadas de pacientes en el sistema.
 * Permite buscar pacientes por varios criterios como nombre, RUT, código de lesión,
 * diagnóstico y fecha de cirugía.
 */
public class AdvancedSearchDialog extends JDialog {
    private final PatientController controller;
    private final Map<String, String> lesionCodes; // Se mantiene por si se necesita para futuras funcionalidades.
    private JComboBox<String> searchCriteriaComboBox;
    private JPanel searchInputPanel;
    private JTextField searchTextField; // Usado para Nombre, RUT, Diagnóstico.
    private JFormattedTextField lesionCodeFormattedField; // Campo específico para Código de Lesión con máscara.
    private JDateChooser searchDateChooser; // Campo específico para Fecha de Cirugía.

    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;

    // Formateador para mostrar las fechas en un formato consistente.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor para el diálogo de búsqueda avanzada.
     *
     * @param parent El {@link JFrame} padre del diálogo.
     * @param controller El controlador de pacientes para realizar las operaciones de búsqueda.
     * @param lesionCodes Un mapa de códigos de lesión, aunque no directamente usado para la búsqueda,
     * podría ser útil para mostrar información o futuras ampliaciones.
     */
    public AdvancedSearchDialog(JFrame parent, PatientController controller, Map<String, String> lesionCodes) {
        super(parent, "Búsqueda Avanzada de Pacientes", true);
        this.controller = controller;
        this.lesionCodes = lesionCodes;

        setSize(1200, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(parent);

        initComponents();
    }

    /**
     * Inicializa y organiza todos los componentes de la interfaz de usuario del diálogo.
     */
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Panel de Criterios de Búsqueda ---
        JPanel criteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        criteriaPanel.setBorder(BorderFactory.createTitledBorder("Criterios de Búsqueda"));

        JLabel criteriaLabel = new JLabel("Buscar por:");
        criteriaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        criteriaPanel.add(criteriaLabel);

        // Opciones de criterios de búsqueda disponibles.
        String[] criteriaOptions = {"Nombre del Paciente", "RUT", "Código de Lesión", "Fecha de Cirugía", "Diagnóstico"};
        searchCriteriaComboBox = new JComboBox<>(criteriaOptions);
        searchCriteriaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Agrega un listener para actualizar el campo de entrada de búsqueda al cambiar el criterio.
        searchCriteriaComboBox.addActionListener(e -> updateSearchInputPanel());
        criteriaPanel.add(searchCriteriaComboBox);

        searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        criteriaPanel.add(searchInputPanel);
        updateSearchInputPanel(); // Inicializa el panel de entrada de búsqueda al abrir el diálogo.

        JButton searchButton = createStyledButton("Buscar");
        searchButton.addActionListener(e -> performSearch());
        criteriaPanel.add(searchButton);

        add(criteriaPanel, BorderLayout.NORTH);

        // --- Panel de Resultados ---
        JPanel resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Resultados de la Búsqueda"));

        // Definición de las columnas de la tabla de resultados.
        String[] columnNames = {"ID", "Nombre", "Edad", "RUT", "Dominancia", "Lesión (Nombre)",
                "Lesión (Código)", "Diagnóstico", "Delay QX", "Fecha Cirugía", "Tipo de CX", "Estado"};
        resultsTableModel = new DefaultTableModel(columnNames, 0) {
            // Deshabilita la edición directa de celdas en la tabla.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite seleccionar solo una fila a la vez.

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.CENTER);

        // --- Panel de Botones Inferior ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton closeButton = createStyledButton("Cerrar");
        closeButton.addActionListener(e -> dispose()); // Cierra el diálogo al presionar.
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Actualiza dinámicamente el campo de entrada de búsqueda según el criterio seleccionado
     * en el combo box. Puede ser un JTextField, JFormattedTextField o JDateChooser.
     */
    private void updateSearchInputPanel() {
        searchInputPanel.removeAll(); // Limpia cualquier componente anterior.
        String selectedCriteria = (String) searchCriteriaComboBox.getSelectedItem();
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Reinicia las referencias a los campos para asegurar que solo uno esté activo.
        searchTextField = null;
        lesionCodeFormattedField = null;
        searchDateChooser = null;

        // Configura el campo de entrada según el criterio seleccionado.
        if ("Nombre del Paciente".equals(selectedCriteria) || "RUT".equals(selectedCriteria) || "Diagnóstico".equals(selectedCriteria)) {
            searchTextField = new JTextField(20);
            searchTextField.setFont(fieldFont);
            searchInputPanel.add(searchTextField);
        } else if ("Código de Lesión".equals(selectedCriteria)) {
            try {
                // Aplica una máscara para forzar el formato XX XX XXX.
                MaskFormatter formatter = new MaskFormatter("## ## ###");
                formatter.setPlaceholderCharacter('_');
                lesionCodeFormattedField = new JFormattedTextField(formatter);
                lesionCodeFormattedField.setColumns(10);
                lesionCodeFormattedField.setFont(fieldFont);
                // Confirma el valor al perder el foco, lo cual es útil con MaskFormatter.
                lesionCodeFormattedField.setFocusLostBehavior(JFormattedTextField.COMMIT);
                searchInputPanel.add(lesionCodeFormattedField);
            } catch (ParseException e) {
                System.err.println("Error al crear MaskFormatter para Código de Lesión: " + e.getMessage());
                // En caso de error, se proporciona un JTextField simple como fallback.
                searchTextField = new JTextField(20);
                searchTextField.setFont(fieldFont);
                searchInputPanel.add(searchTextField);
                JOptionPane.showMessageDialog(this, "Error en la configuración del campo de código de lesión. Contacte al soporte.", "Error de Configuración", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("Fecha de Cirugía".equals(selectedCriteria)) {
            searchDateChooser = new JDateChooser();
            searchDateChooser.setDateFormatString("dd-MM-yyyy");
            searchDateChooser.setFont(fieldFont);
            searchDateChooser.setPreferredSize(new Dimension(150, 30));
            searchInputPanel.add(searchDateChooser);
        }
        // Repinta el panel para mostrar el nuevo campo de entrada.
        searchInputPanel.revalidate();
        searchInputPanel.repaint();
    }

    /**
     * Realiza la búsqueda de pacientes basándose en el criterio seleccionado y el término de búsqueda ingresado.
     * Los resultados se muestran en la tabla de resultados.
     */
    private void performSearch() {
        resultsTableModel.setRowCount(0); // Limpia cualquier resultado de búsqueda anterior.

        String selectedCriteria = (String) searchCriteriaComboBox.getSelectedItem();
        List<Patient> patients = null;
        try {
            patients = controller.getAllPatients(); // Carga todos los pacientes para filtrar.
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los pacientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (patients == null || patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pacientes registrados para buscar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String searchTerm = "";
        // Obtiene el término de búsqueda según el tipo de campo de entrada activo.
        if (searchTextField != null) {
            searchTerm = searchTextField.getText().trim().toLowerCase();
        } else if (lesionCodeFormattedField != null) {
            searchTerm = lesionCodeFormattedField.getText().trim();
            // Valida que el campo del código de lesión esté completo.
            if (searchTerm.contains("_") || searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el Código de Lesión completo en el formato XX XX XXX.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            searchTerm = searchTerm.toLowerCase();
        }
        // Para JDateChooser, la lógica de fecha se maneja directamente en el switch.

        // Itera sobre la lista de pacientes y aplica el filtro según el criterio seleccionado.
        for (Patient patient : patients) {
            boolean match = false;

            switch (selectedCriteria) {
                case "Nombre del Paciente":
                    if (patient.getNombre().toLowerCase().contains(searchTerm)) {
                        match = true;
                    }
                    break;
                case "RUT":
                    // Limpia y compara el RUT del paciente y el término de búsqueda.
                    String cleanRutPatient = ChileanValidator.cleanRut(patient.getRut()).toLowerCase();
                    String cleanSearchTermRut = ChileanValidator.cleanRut(searchTerm);
                    if (cleanRutPatient.contains(cleanSearchTermRut)) {
                        match = true;
                    }
                    break;
                case "Código de Lesión":
                    if (patient.getLesion() != null && patient.getLesion().getCodigoOficial() != null && !patient.getLesion().getCodigoOficial().isEmpty()) {
                        // Limpia espacios y convierte a minúsculas para una comparación robusta.
                        String patientCodeClean = patient.getLesion().getCodigoOficial().replace(" ", "").toLowerCase();
                        String searchTermClean = searchTerm.replace(" ", "").toLowerCase();
                        if (patientCodeClean.contains(searchTermClean)) {
                            match = true;
                        }
                    }
                    break;
                case "Diagnóstico":
                    if (patient.getLesion() != null && patient.getLesion().getDiagnostico() != null && patient.getLesion().getDiagnostico().toLowerCase().contains(searchTerm)) {
                        match = true;
                    }
                    break;
                case "Fecha de Cirugía":
                    if (searchDateChooser != null && searchDateChooser.getDate() != null) {
                        // Convierte la fecha seleccionada de Date a LocalDate para la comparación.
                        LocalDate selectedDate = searchDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (patient.getFechaCirugia() != null && patient.getFechaCirugia().isEqual(selectedDate)) {
                            match = true;
                        }
                    }
                    break;
            }

            // Si el paciente coincide con el criterio de búsqueda, se añade a la tabla de resultados.
            if (match) {
                String fechaCirugiaStr = patient.getFechaCirugia() != null ?
                        patient.getFechaCirugia().format(DATE_FORMATTER) : "N/A";
                // Maneja posibles nulos para la información de la lesión.
                String nombreLesion = (patient.getLesion() != null) ? patient.getLesion().getNombreLesion() : "N/A";
                String codigoOficialLesion = (patient.getLesion() != null) ? patient.getLesion().getCodigoOficial() : "N/A";
                String diagnosticoLesion = (patient.getLesion() != null) ? patient.getLesion().getDiagnostico() : "N/A";
                String estado = patient.isRecovered() ? "Recuperado" : "Activo";

                resultsTableModel.addRow(new Object[]{
                        patient.getId(),
                        patient.getNombre(),
                        patient.getEdad(),
                        ChileanValidator.formatRut(patient.getRut()),
                        patient.getDominancia(),
                        nombreLesion,
                        codigoOficialLesion,
                        diagnosticoLesion,
                        patient.getDelayQx(),
                        fechaCirugiaStr,
                        patient.getTipoDeCx(),
                        estado
                });
            }
        }

        // Muestra un mensaje si no se encontraron resultados.
        if (resultsTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron resultados para su búsqueda.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Crea un botón estilizado con texto y un icono.
     *
     * @param text El texto a mostrar en el botón.
     * @return Un objeto {@link JButton} con el estilo aplicado.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(66, 133, 244));
        button.setOpaque(true);
        button.setBorderPainted(false);


        return button;
    }
}