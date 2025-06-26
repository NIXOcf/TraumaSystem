package com.traumasystem.view;

import com.traumasystem.controller.PatientController;
import com.traumasystem.model.Patient;
import com.traumasystem.util.ChileanValidator;
import com.traumasystem.util.ExcelReportGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * La ventana principal de la aplicación del Sistema de Gestión de Pacientes de Traumatología.
 * Muestra una tabla con la lista de pacientes y proporciona opciones para añadir, editar,
 * ver detalles, cambiar el estado de recuperación, buscar, eliminar y exportar pacientes.
 */
public class MainFrame extends JFrame {
    private final PatientController controller;
    private final Map<String, String> lesionCodes; // Mapa de códigos de lesión para referencia.
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JCheckBox showRecoveredCheckbox;
    private JButton deleteButton; // Botón para eliminar el paciente seleccionado.

    // Formateador de fechas para asegurar un formato consistente en la interfaz.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor principal para el {@code MainFrame}.
     *
     * @param controller El controlador de pacientes que manejará la lógica de negocio.
     * @param lesionCodes Un mapa de códigos de lesión utilizado para la selección y visualización.
     */
    public MainFrame(PatientController controller, Map<String, String> lesionCodes) {
        this.controller = controller;
        this.lesionCodes = lesionCodes;

        setTitle("Sistema de Gestión de Pacientes de Traumatología");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 700)); // Define el tamaño mínimo de la ventana.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana.
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.

        initComponents(); // Inicializa los componentes de la interfaz.
        loadPatients(); // Carga la lista inicial de pacientes en la tabla.
        updateButtonStates(); // Asegura que los botones dependientes de la selección estén deshabilitados al inicio.
    }

    /**
     * Inicializa y organiza todos los componentes de la interfaz de usuario del {@code MainFrame}.
     */
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        // Agrega un borde vacío alrededor del contenido principal para espaciado.
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Panel Superior (NORTH): Búsqueda y Filtro ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        // Establece un borde titulado para el panel superior.
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(66, 133, 244)),
                "Búsqueda y Filtro Rápido",
                SwingConstants.LEFT,
                SwingConstants.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(66, 133, 244)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaciado entre componentes.

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("Buscar por Nombre/RUT/Lesión:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Permite que el campo de texto se expanda.
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(searchField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton searchButton = createStyledButton("Buscar", "/icons/search_icon.png");
        searchButton.addActionListener(e -> searchPatients()); // Asigna la acción de búsqueda.
        topPanel.add(searchButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        JButton clearSearchButton = createStyledButton("Limpiar Búsqueda", "/icons/clear_icon.png");
        clearSearchButton.addActionListener(e -> {
            searchField.setText(""); // Limpia el campo de búsqueda.
            loadPatients(); // Recarga todos los pacientes.
        });
        topPanel.add(clearSearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Ocupa dos columnas.
        gbc.anchor = GridBagConstraints.WEST;
        showRecoveredCheckbox = new JCheckBox("Mostrar Recuperados");
        showRecoveredCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showRecoveredCheckbox.addActionListener(e -> loadPatients()); // Recarga pacientes al cambiar el estado del checkbox.
        topPanel.add(showRecoveredCheckbox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton advancedSearchButton = createStyledButton("Búsqueda Avanzada", "/icons/advanced_search_icon.png");
        advancedSearchButton.addActionListener(e -> openAdvancedSearchDialog()); // Abre el diálogo de búsqueda avanzada.
        topPanel.add(advancedSearchButton, gbc);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabla de Pacientes (CENTER) ---
        String[] columnNames = {"ID", "Nombre", "Edad", "RUT", "Dominancia", "Lesión (Nombre)",
                "Lesión (Código)", "Diagnóstico", "Delay QX", "Fecha Cirugía", "Tipo de CX", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas de la tabla no son editables directamente.
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite seleccionar solo una fila.
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientTable.setRowHeight(30); // Altura de las filas.
        patientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientTable.setAutoCreateRowSorter(true); // Habilita la ordenación de columnas.
        patientTable.getTableHeader().setReorderingAllowed(false); // Impide reordenar las columnas.

        // Listener para actualizar el estado de los botones cuando cambia la selección de la tabla.
        patientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Solo reacciona cuando la selección se ha estabilizado.
                    updateButtonStates();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); // Elimina el borde del scroll pane.
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel de Botones (SOUTH) ---
        JPanel buttonContentPanel = new JPanel(new GridLayout(0, 3, 15, 10)); // Grid de 3 columnas.
        // Establece un borde titulado para el panel de botones.
        buttonContentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(66, 133, 244)),
                "Acciones de Paciente",
                SwingConstants.LEFT,
                SwingConstants.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(66, 133, 244)
        ));

        // Creación y asignación de acciones para los botones de acción.
        JButton addButton = createStyledButton("Añadir Paciente", "/icons/add_icon.png");
        addButton.addActionListener(e -> openAddPatientDialog());

        JButton editButton = createStyledButton("Editar Paciente", "/icons/edit_icon.png");
        editButton.addActionListener(e -> openEditPatientDialog());

        JButton detailsButton = createStyledButton("Ver Detalles / Seguimiento", "/icons/details_icon.png");
        detailsButton.addActionListener(e -> openPatientDetailsDialog());

        JButton toggleRecoveredButton = createStyledButton("Cambiar Estado Recuperado", "/icons/status_icon.png");
        toggleRecoveredButton.addActionListener(e -> togglePatientRecoveredStatus());

        JButton exportExcelButton = createStyledButton("Exportar a Excel", "/icons/excel_icon.png");
        exportExcelButton.addActionListener(e -> exportPatientsToExcel());

        // El botón de eliminar se crea sin icono inicialmente.
        deleteButton = createStyledButton("Eliminar Registro", null);
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        deleteButton.setEnabled(false); // Deshabilitado por defecto hasta que se seleccione una fila.

        // Añade los botones al panel de contenido.
        buttonContentPanel.add(addButton);
        buttonContentPanel.add(editButton);
        buttonContentPanel.add(detailsButton);
        buttonContentPanel.add(toggleRecoveredButton);
        buttonContentPanel.add(exportExcelButton);
        buttonContentPanel.add(deleteButton);

        add(buttonContentPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea y retorna un {@link JButton} con estilos predefinidos (tamaño, fuente, colores)
     * y, opcionalmente, un icono. Incluye un efecto visual al pasar el ratón por encima.
     *
     * @param text El texto que se mostrará en el botón.
     * @param iconPath La ruta al recurso del icono. Puede ser {@code null} o vacío si no se desea un icono.
     * @return Un {@link JButton} estilizado.
     */
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setMinimumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(220, 50));
        button.setMaximumSize(new Dimension(250, 60));

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(66, 133, 244)); // Color de fondo predeterminado.
        button.setOpaque(true);
        button.setBorderPainted(false); // Elimina el borde predeterminado del botón.

        // Añade un efecto visual al pasar el ratón por encima.
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(48, 99, 189)); // Color más oscuro al entrar.
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(66, 133, 244)); // Vuelve al color original al salir.
            }
        });
        return button;
    }

    /**
     * Actualiza el estado de habilitación de los botones de acción
     * (como "Eliminar", "Editar", "Ver Detalles") según si hay una fila
     * seleccionada en la tabla de pacientes.
     */
    private void updateButtonStates() {
        boolean isRowSelected = patientTable.getSelectedRow() != -1;
        deleteButton.setEnabled(isRowSelected);
        // Aquí se pueden agregar otros botones que dependan de la selección, por ejemplo:
        // editButton.setEnabled(isRowSelected);
        // detailsButton.setEnabled(isRowSelected);
        // toggleRecoveredButton.setEnabled(isRowSelected);
    }

    /**
     * Carga y actualiza la tabla de pacientes. Dependiendo del estado del
     * checkbox {@code showRecoveredCheckbox}, mostrará todos los pacientes
     * o solo los pacientes activos.
     */
    private void loadPatients() {
        try {
            List<Patient> patients;
            if (showRecoveredCheckbox.isSelected()) {
                patients = controller.getAllPatients(); // Carga todos los pacientes.
            } else {
                patients = controller.getAllActivePatients(); // Carga solo los pacientes no recuperados.
            }
            updatePatientTable(patients); // Actualiza la tabla con los pacientes obtenidos.
            updateButtonStates(); // Actualiza el estado de los botones después de cargar los datos.
        } catch (IOException e) {
            // Muestra un mensaje de error al usuario y registra la excepción.
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pacientes: " + e.getMessage() + "\nPor favor, contacte a soporte.",
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al cargar pacientes: " + e.getMessage());
        }
    }

    /**
     * Realiza una búsqueda rápida de pacientes basándose en el texto ingresado
     * en el campo de búsqueda. Filtra por nombre, RUT o lesión.
     */
    private void searchPatients() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPatients(); // Si el campo de búsqueda está vacío, recarga todos los pacientes según el filtro de recuperación.
            return;
        }
        try {
            List<Patient> searchResults = controller.searchPatients(searchTerm); // Realiza la búsqueda.
            updatePatientTable(searchResults); // Actualiza la tabla con los resultados de la búsqueda.
            updateButtonStates(); // Actualiza el estado de los botones.
        } catch (IOException e) {
            // Muestra un mensaje de error al usuario y registra la excepción.
            JOptionPane.showMessageDialog(this,
                    "Error al buscar pacientes: " + e.getMessage() + "\nPor favor, intente de nuevo.",
                    "Error de Búsqueda",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al buscar pacientes: " + e.getMessage());
        }
    }

    /**
     * Actualiza el modelo de la tabla de pacientes con los datos de la lista proporcionada.
     * Limpia la tabla actual y la rellena con los nuevos datos.
     *
     * @param patients La {@link List} de objetos {@link Patient} a mostrar en la tabla.
     */
    private void updatePatientTable(List<Patient> patients) {
        tableModel.setRowCount(0); // Elimina todas las filas existentes de la tabla.
        for (Patient patient : patients) {
            // Formatea la fecha de cirugía o usa "N/A" si es nula.
            String fechaCirugiaStr = patient.getFechaCirugia() != null ?
                    patient.getFechaCirugia().format(DATE_FORMATTER) : "N/A";

            // Obtiene la información de la lesión, usando "N/A" si el objeto lesión es nulo.
            String nombreLesion = (patient.getLesion() != null) ? patient.getLesion().getNombreLesion() : "N/A";
            String codigoOficialLesion = (patient.getLesion() != null) ? patient.getLesion().getCodigoOficial() : "N/A";
            String diagnosticoLesion = (patient.getLesion() != null) ? patient.getLesion().getDiagnostico() : "N/A";

            // Determina el estado de recuperación.
            String estado = patient.isRecovered() ? "Recuperado" : "Activo";

            // Añade una nueva fila a la tabla con los datos del paciente.
            tableModel.addRow(new Object[]{
                    patient.getId(),
                    patient.getNombre(),
                    patient.getEdad(),
                    ChileanValidator.formatRut(patient.getRut()), // Formatea el RUT para la visualización.
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

    /**
     * Abre el diálogo {@link PatientFormDialog} para añadir un nuevo paciente.
     * Después de cerrar el diálogo, la tabla de pacientes se recarga.
     */
    private void openAddPatientDialog() {
        PatientFormDialog dialog = new PatientFormDialog(this, controller, lesionCodes);
        dialog.setVisible(true); // Hace visible el diálogo.
        loadPatients(); // Recarga los pacientes para reflejar el nuevo paciente añadido.
    }

    /**
     * Abre el diálogo {@link PatientFormDialog} para editar el paciente seleccionado.
     * Requiere que un paciente esté seleccionado en la tabla.
     */
    private void openEditPatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona un paciente para editar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtiene el ID del paciente de la fila seleccionada (manejando la ordenación de la tabla).
            int modelRow = patientTable.convertRowIndexToModel(selectedRow);
            String patientId = (String) tableModel.getValueAt(modelRow, 0);
            Patient patientToEdit = controller.getPatient(patientId); // Obtiene el objeto Patient completo.

            if (patientToEdit != null) {
                PatientFormDialog dialog = new PatientFormDialog(this, controller, patientToEdit, lesionCodes);
                dialog.setVisible(true); // Hace visible el diálogo de edición.
                loadPatients(); // Recarga los pacientes para reflejar los cambios.
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el paciente seleccionado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("Error: Paciente con ID " + patientId + " no encontrado para edición.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener los datos del paciente para editar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al obtener paciente para edición: " + e.getMessage());
        }
    }

    /**
     * Abre el diálogo {@link PatientDetailsDialog} para ver los detalles y el seguimiento
     * de un paciente seleccionado. Requiere que un paciente esté seleccionado en la tabla.
     */
    private void openPatientDetailsDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona un paciente para ver sus detalles.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtiene el ID del paciente de la fila seleccionada.
            int modelRow = patientTable.convertRowIndexToModel(selectedRow);
            String patientId = (String) tableModel.getValueAt(modelRow, 0);
            Patient patientToShowDetails = controller.getPatient(patientId); // Obtiene el objeto Patient completo.

            if (patientToShowDetails != null) {
                PatientDetailsDialog dialog = new PatientDetailsDialog(this, controller, patientToShowDetails);
                dialog.setVisible(true); // Hace visible el diálogo de detalles.
                loadPatients(); // Recarga los pacientes por si se realizaron cambios en el seguimiento.
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el paciente seleccionado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("Error: Paciente con ID " + patientId + " no encontrado para detalles.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener los datos del paciente para detalles: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al obtener paciente para detalles: " + e.getMessage());
        }
    }

    /**
     * Alterna el estado de recuperación (activo/recuperado) del paciente seleccionado.
     * Requiere que un paciente esté seleccionado en la tabla.
     */
    private void togglePatientRecoveredStatus() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona un paciente para cambiar su estado de recuperación.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtiene el ID del paciente de la fila seleccionada.
            int modelRow = patientTable.convertRowIndexToModel(selectedRow);
            String patientId = (String) tableModel.getValueAt(modelRow, 0);
            Patient patientToToggle = controller.getPatient(patientId); // Obtiene el objeto Patient completo.

            if (patientToToggle != null) {
                if (patientToToggle.isRecovered()) {
                    controller.markPatientAsActive(patientToToggle); // Cambia a estado activo.
                    JOptionPane.showMessageDialog(this, "Paciente marcado como ACTIVO.", "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controller.markPatientAsRecovered(patientToToggle); // Cambia a estado recuperado.
                    JOptionPane.showMessageDialog(this, "Paciente marcado como RECUPERADO.", "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
                }
                loadPatients(); // Recarga los pacientes para reflejar el cambio de estado.
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el paciente seleccionado para cambiar su estado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("Error: Paciente con ID " + patientId + " no encontrado para cambio de estado.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado del paciente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al cambiar estado del paciente: " + e.getMessage());
        }
    }

    /**
     * Elimina el paciente seleccionado de la tabla y del almacenamiento.
     * Requiere que un paciente esté seleccionado y solicita confirmación al usuario.
     */
    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona un registro de paciente para eliminar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtiene el ID y el nombre del paciente de la fila seleccionada para el mensaje de confirmación.
        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String patientId = (String) tableModel.getValueAt(modelRow, 0);
        String patientName = (String) tableModel.getValueAt(modelRow, 1);

        // Pide confirmación al usuario antes de eliminar.
        int confirmResult = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar permanentemente el registro de " + patientName + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                controller.deletePatient(patientId); // Llama al controlador para eliminar el paciente.
                JOptionPane.showMessageDialog(this,
                        "El registro del paciente ha sido eliminado exitosamente.",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                loadPatients(); // Recarga la tabla para reflejar la eliminación.
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el registro del paciente: " + e.getMessage(),
                        "Error de Eliminación",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println("Error de IO al eliminar paciente: " + e.getMessage());
            }
        }
    }

    /**
     * Exporta la lista completa de pacientes a un archivo Excel utilizando {@link ExcelReportGenerator}.
     */
    private void exportPatientsToExcel() {
        try {
            List<Patient> allPatients = controller.getAllPatients(); // Obtiene todos los pacientes.
            if (allPatients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay pacientes para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ExcelReportGenerator.exportPatientsToExcel(allPatients, this); // Llama al generador de reportes.
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener los pacientes para el reporte Excel: " + e.getMessage(),
                    "Error de Datos",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error de IO al exportar pacientes a Excel: " + e.getMessage());
        }
    }

    /**
     * Abre el diálogo de búsqueda avanzada ({@link AdvancedSearchDialog}).
     */
    private void openAdvancedSearchDialog() {
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(this, controller, lesionCodes);
        dialog.setVisible(true); // Hace visible el diálogo de búsqueda avanzada.
    }
}