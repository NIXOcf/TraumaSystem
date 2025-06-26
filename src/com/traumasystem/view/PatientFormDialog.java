package com.traumasystem.view;

import com.traumasystem.controller.PatientController;
import com.traumasystem.model.Lesion;
import com.traumasystem.model.Patient;
import com.traumasystem.util.ChileanValidator;
import com.traumasystem.util.LesionCodeRegistry;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Diálogo para la creación y edición de pacientes.
 * Permite ingresar o modificar la información personal del paciente, datos de la lesión y detalles de la cirugía.
 */
public class PatientFormDialog extends JDialog {
    private final PatientController controller;
    private Patient patientToEdit;
    private final Map<String, String> lesionCodes; // Mapa de códigos de lesión para autocompletado y validación.

    // Campos de entrada de datos del paciente.
    private JTextField nombreField;
    private JTextField edadField;
    private JTextField rutField;
    private JComboBox<String> dominanciaCombo;

    // Campos de entrada de datos de la lesión y cirugía.
    private JTextArea nombreLesionField; // JTextArea para el nombre de la lesión.
    private JTextField txtCodigoOficialLesionParte1;
    private JTextField txtCodigoOficialLesionParte2;
    private JTextField txtCodigoOficialLesionParte3;
    private JTextField diagnosticoField;
    private JSpinner delayQxSpinner;
    private JDateChooser fechaCirugiaChooser;
    private JTextField tipoDeCxField;

    // Formateador de fechas para asegurar un formato consistente.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor para añadir un nuevo paciente.
     *
     * @param parent El {@link JFrame} padre de este diálogo.
     * @param controller El controlador de pacientes que gestionará la creación del paciente.
     * @param lesionCodes Un mapa de códigos de lesión para referencias.
     */
    public PatientFormDialog(JFrame parent, PatientController controller, Map<String, String> lesionCodes) {
        super(parent, "Añadir Nuevo Paciente", true);
        this.controller = controller;
        this.lesionCodes = lesionCodes;
        setupDialog();
        initComponents();
    }

    /**
     * Constructor para editar un paciente existente.
     *
     * @param parent El {@link JFrame} padre de este diálogo.
     * @param controller El controlador de pacientes que gestionará la actualización del paciente.
     * @param patientToEdit El objeto {@link Patient} a editar, cuyos datos se cargarán en el formulario.
     * @param lesionCodes Un mapa de códigos de lesión para referencias.
     */
    public PatientFormDialog(JFrame parent, PatientController controller, Patient patientToEdit, Map<String, String> lesionCodes) {
        super(parent, "Editar Paciente", true);
        this.controller = controller;
        this.patientToEdit = patientToEdit;
        this.lesionCodes = lesionCodes;
        setupDialog();
        initComponents();
        populateForm(); // Rellena el formulario con los datos del paciente a editar.
    }

    /**
     * Configura las propiedades básicas del diálogo, como tamaño y ubicación.
     */
    private void setupDialog() {
        setSize(700, 750);
        setMinimumSize(new Dimension(650, 700));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    /**
     * Inicializa y organiza todos los componentes de la interfaz de usuario del formulario.
     */
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaciado entre componentes.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los componentes se expanden horizontalmente.
        gbc.anchor = GridBagConstraints.WEST; // Alineación a la izquierda.

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Nombre del paciente.
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Nombre:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        nombreField = createTextField(fieldFont);
        formPanel.add(nombreField, gbc);

        // Edad del paciente (con filtro para solo números y máximo 3 dígitos).
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("Edad:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        edadField = createTextField(fieldFont);
        edadField.setColumns(5);
        ((AbstractDocument) edadField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);

                if (newText.matches("\\d*") && newText.length() <= 3) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (newText.matches("\\d*") && newText.length() <= 3) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        formPanel.add(edadField, gbc);

        // RUT del paciente (con formato automático y validación al perder el foco).
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("RUT (ej. 12.345.678-9):", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        rutField = createTextField(fieldFont);
        rutField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String rutText = rutField.getText().trim();
                if (!rutText.isEmpty()) {
                    String cleanRut = ChileanValidator.cleanRut(rutText);
                    String formattedRut = ChileanValidator.formatRut(cleanRut);
                    rutField.setText(formattedRut); // Formatea el RUT en el campo.
                    // Valida el RUT y cambia el color de fondo si es inválido.
                    if (!ChileanValidator.validateRut(cleanRut)) {
                        rutField.setBackground(new Color(255, 220, 220)); // Rojo claro.
                    } else {
                        rutField.setBackground(Color.WHITE);
                    }
                } else {
                    rutField.setBackground(Color.WHITE); // Restablece el color si el campo está vacío.
                }
            }
        });
        formPanel.add(rutField, gbc);

        // Dominancia del paciente (ComboBox).
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createLabel("Dominancia:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        dominanciaCombo = new JComboBox<>(new String[]{"Diestro", "Zurdo", "Ambidiestro"});
        dominanciaCombo.setFont(fieldFont);
        formPanel.add(dominanciaCombo, gbc);

        // Separador para la sección de Lesión.
        gbc.gridwidth = 2; // Ocupa ambas columnas.
        gbc.gridx = 0; gbc.gridy = 4;
        JSeparator separator = new JSeparator();
        separator.setBorder(BorderFactory.createTitledBorder("Datos de Lesión"));
        formPanel.add(separator, gbc);
        gbc.gridwidth = 1; // Restablece a 1 columna.

        // Campos para el Código de Lesión (tres partes).
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(createLabel("Código Lesión (XX XX XXX):", labelFont), gbc);

        JPanel codigoLesionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        txtCodigoOficialLesionParte1 = createTextField(fieldFont);
        txtCodigoOficialLesionParte1.setColumns(2);
        txtCodigoOficialLesionParte2 = createTextField(fieldFont);
        txtCodigoOficialLesionParte2.setColumns(2);
        txtCodigoOficialLesionParte3 = createTextField(fieldFont);
        txtCodigoOficialLesionParte3.setColumns(3);

        codigoLesionPanel.add(txtCodigoOficialLesionParte1);
        codigoLesionPanel.add(new JLabel(" ")); // Espaciadores visuales.
        codigoLesionPanel.add(new JLabel(" "));
        codigoLesionPanel.add(txtCodigoOficialLesionParte2);
        codigoLesionPanel.add(new JLabel(" "));
        codigoLesionPanel.add(new JLabel(" "));
        codigoLesionPanel.add(txtCodigoOficialLesionParte3);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        formPanel.add(codigoLesionPanel, gbc);

        // Listener para autocompletar el nombre de la lesión y el diagnóstico al cambiar el código.
        DocumentListener lesionCodeListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateLesionNameAndDiagnostico(); }
            @Override public void removeUpdate(DocumentEvent e) { updateLesionNameAndDiagnostico(); }
            @Override public void changedUpdate(DocumentEvent e) { updateLesionNameAndDiagnostico(); }
        };
        txtCodigoOficialLesionParte1.getDocument().addDocumentListener(lesionCodeListener);
        txtCodigoOficialLesionParte2.getDocument().addDocumentListener(lesionCodeListener);
        txtCodigoOficialLesionParte3.getDocument().addDocumentListener(lesionCodeListener);

        // Nombre de Lesión (JTextArea no editable con scroll).
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(createLabel("Nombre Lesión:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 1.0;
        gbc.gridheight = 2; // Permite que el JTextArea ocupe más filas verticalmente.
        gbc.fill = GridBagConstraints.BOTH; // Permite que se estire en ambas direcciones.

        nombreLesionField = new JTextArea(3, 20); // 3 filas, 20 columnas (se ajustará dinámicamente).
        nombreLesionField.setFont(fieldFont);
        nombreLesionField.setEditable(false); // No permite edición manual.
        nombreLesionField.setBackground(new Color(230, 230, 230)); // Fondo gris claro.
        nombreLesionField.setLineWrap(true); // Habilita ajuste de línea.
        nombreLesionField.setWrapStyleWord(true); // Ajuste de línea por palabra.

        JScrollPane scrollPaneNombreLesion = new JScrollPane(nombreLesionField);
        scrollPaneNombreLesion.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneNombreLesion.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Sin scroll horizontal.

        formPanel.add(scrollPaneNombreLesion, gbc);

        // Restablece las restricciones para los componentes siguientes.
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 8; // Ajusta la posición de la fila después del JTextArea.

        // Diagnóstico.
        gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Diagnóstico:", labelFont), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        diagnosticoField = createTextField(fieldFont);
        formPanel.add(diagnosticoField, gbc);

        // Delay QX (días).
        gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0;
        formPanel.add(createLabel("Delay QX (días):", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 9; gbc.weightx = 1.0;
        delayQxSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1)); // Rango de 0 a 365 días.
        delayQxSpinner.setFont(fieldFont);
        formPanel.add(delayQxSpinner, gbc);

        // Fecha de Cirugía.
        gbc.gridx = 0; gbc.gridy = 10; gbc.weightx = 0;
        formPanel.add(createLabel("Fecha Cirugía (DD-MM-YYYY):", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 10; gbc.weightx = 1.0;
        fechaCirugiaChooser = new JDateChooser();
        fechaCirugiaChooser.setDateFormatString("dd-MM-yyyy");
        fechaCirugiaChooser.setFont(fieldFont);
        formPanel.add(fechaCirugiaChooser, gbc);

        // Tipo de Cirugía.
        gbc.gridx = 0; gbc.gridy = 11; gbc.weightx = 0;
        formPanel.add(createLabel("Tipo de CX:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 11; gbc.weightx = 1.0;
        tipoDeCxField = createTextField(fieldFont);
        formPanel.add(tipoDeCxField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton saveButton = createStyledButton("Guardar");
        saveButton.addActionListener(e -> savePatient());
        buttonPanel.add(saveButton);

        JButton cancelButton = createStyledButton("Cancelar");
        cancelButton.addActionListener(e -> dispose()); // Cierra el diálogo sin guardar.
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea un {@link JLabel} con el texto y la fuente especificados.
     *
     * @param text El texto del label.
     * @param font La fuente del label.
     * @return Un nuevo {@link JLabel}.
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    /**
     * Crea un {@link JTextField} con la fuente especificada y un ancho predeterminado.
     *
     * @param font La fuente del campo de texto.
     * @return Un nuevo {@link JTextField}.
     */
    private JTextField createTextField(Font font) {
        JTextField textField = new JTextField(20);
        textField.setFont(font);
        return textField;
    }

    /**
     * Crea un {@link JButton} con un estilo uniforme, incluyendo texto e icono.
     *
     * @param text El texto que se mostrará en el botón.
     * @return Un {@link JButton} configurado con el estilo predefinido.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(66, 133, 244)); // Tono de azul de Google.
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    /**
     * Rellena los campos del formulario con los datos de un paciente existente,
     * cuando el diálogo se usa en modo de edición.
     */
    private void populateForm() {
        if (patientToEdit != null) {
            nombreField.setText(patientToEdit.getNombre());
            edadField.setText(String.valueOf(patientToEdit.getEdad()));
            rutField.setText(ChileanValidator.formatRut(patientToEdit.getRut()));
            dominanciaCombo.setSelectedItem(patientToEdit.getDominancia());

            if (patientToEdit.getLesion() != null) {
                String fullCode = patientToEdit.getLesion().getCodigoOficial();
                // Divide el código oficial en sus tres partes para los campos de texto.
                if (fullCode != null && fullCode.matches("\\d{2} \\d{2} \\d{3}")) {
                    String[] parts = fullCode.split(" ");
                    txtCodigoOficialLesionParte1.setText(parts[0]);
                    txtCodigoOficialLesionParte2.setText(parts[1]);
                    txtCodigoOficialLesionParte3.setText(parts[2]);
                } else {
                    txtCodigoOficialLesionParte1.setText("");
                    txtCodigoOficialLesionParte2.setText("");
                    txtCodigoOficialLesionParte3.setText("");
                }
                nombreLesionField.setText(patientToEdit.getLesion().getNombreLesion());
                diagnosticoField.setText(patientToEdit.getLesion().getDiagnostico());
            } else {
                // Limpia los campos si no hay información de lesión.
                txtCodigoOficialLesionParte1.setText("");
                txtCodigoOficialLesionParte2.setText("");
                txtCodigoOficialLesionParte3.setText("");
                nombreLesionField.setText("");
                diagnosticoField.setText("");
            }
            delayQxSpinner.setValue(patientToEdit.getDelayQx());

            // Establece la fecha de cirugía en el JDateChooser.
            if (patientToEdit.getFechaCirugia() != null) {
                fechaCirugiaChooser.setDate(Date.from(patientToEdit.getFechaCirugia().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else {
                fechaCirugiaChooser.setDate(null);
            }
            tipoDeCxField.setText(patientToEdit.getTipoDeCx());
        }
    }

    /**
     * Actualiza automáticamente el campo del nombre de la lesión y el diagnóstico
     * basándose en el código oficial de lesión ingresado. Utiliza {@link LesionCodeRegistry}
     * para buscar la denominación del código.
     */
    private void updateLesionNameAndDiagnostico() {
        String parte1 = txtCodigoOficialLesionParte1.getText().trim();
        String parte2 = txtCodigoOficialLesionParte2.getText().trim();
        String parte3 = txtCodigoOficialLesionParte3.getText().trim();

        // Comprueba si todas las partes del código están completas y son numéricas.
        if (parte1.length() == 2 && parte1.matches("\\d+") &&
                parte2.length() == 2 && parte2.matches("\\d+") &&
                parte3.length() == 3 && parte3.matches("\\d+")) {

            String fullCode = String.format("%s %s %s", parte1, parte2, parte3);
            String lesionName = LesionCodeRegistry.getLesionCodes().get(fullCode); // Busca el nombre en el registro.

            if (lesionName != null) {
                nombreLesionField.setText(lesionName);
            } else {
                nombreLesionField.setText("Código no encontrado");
            }
        } else {
            nombreLesionField.setText(""); // Limpia el campo si el código está incompleto o mal formado.
        }
        // Fuerza el redibujo y ajuste del JTextArea.
        nombreLesionField.revalidate();
        nombreLesionField.repaint();
    }

    /**
     * Guarda el paciente (ya sea nuevo o editado) en el sistema.
     * Realiza validaciones de los campos de entrada antes de proceder con el guardado.
     */
    private void savePatient() {
        // Validaciones de campos obligatorios y formato.
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del paciente es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rutInput = rutField.getText().trim();
        String cleanRutForValidation = ChileanValidator.cleanRut(rutInput);
        if (!ChileanValidator.validateRut(cleanRutForValidation)) {
            JOptionPane.showMessageDialog(this, "RUT inválido. Asegúrese de que sea un RUT válido y correcto (ej. 12.345.678-9).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            rutField.setBackground(new Color(255, 220, 220)); // Resalta el campo si es inválido.
            return;
        } else {
            rutField.setBackground(Color.WHITE); // Restablece el color.
        }

        int edad;
        String edadText = edadField.getText().trim();
        if (edadText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La edad del paciente es obligatoria.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            edadField.setBackground(new Color(255, 220, 220));
            return;
        }
        try {
            edad = Integer.parseInt(edadText);
            if (edad < 0 || edad > 120) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número entre 0 y 120.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                edadField.setBackground(new Color(255, 220, 220));
                return;
            }
            edadField.setBackground(Color.WHITE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero válido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            edadField.setBackground(new Color(255, 220, 220));
            return;
        }

        // Obtiene y valida las partes del código de lesión.
        String parte1 = txtCodigoOficialLesionParte1.getText().trim();
        String parte2 = txtCodigoOficialLesionParte2.getText().trim();
        String parte3 = txtCodigoOficialLesionParte3.getText().trim();
        String codigoOficialLesion = "";

        if (!parte1.isEmpty() || !parte2.isEmpty() || !parte3.isEmpty()) { // Si alguna parte tiene contenido, validar el formato completo
            if (parte1.length() != 2 || !parte1.matches("\\d+") ||
                    parte2.length() != 2 || !parte2.matches("\\d+") ||
                    parte3.length() != 3 || !parte3.matches("\\d+")) {
                JOptionPane.showMessageDialog(this,
                        "El formato del Código de Lesión es incorrecto.\n" +
                                "Parte 1: 2 dígitos (ej. '21')\n" +
                                "Parte 2: 2 dígitos (ej. '04')\n" +
                                "Parte 3: 3 dígitos (ej. '087')\n" +
                                "Todos deben ser numéricos.",
                        "Error de Formato de Código", JOptionPane.ERROR_MESSAGE);
                return;
            }
            codigoOficialLesion = String.format("%s %s %s", parte1, parte2, parte3);
        }

        String nombreLesion = nombreLesionField.getText().trim();
        String diagnostico = diagnosticoField.getText().trim();

        if (nombreLesion.isEmpty() && codigoOficialLesion.isEmpty() && diagnostico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos el nombre, código oficial o diagnóstico de la lesión.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Advertencia si el código de lesión no está en el registro.
        if (!codigoOficialLesion.isEmpty() && !LesionCodeRegistry.getLesionCodes().containsKey(codigoOficialLesion)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "El código de lesión ingresado '" + codigoOficialLesion + "' no se encuentra en el registro oficial.\n" +
                            "¿Desea continuar de todas formas? (Se guardará con el código ingresado)",
                    "Código de Lesión No Registrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.NO_OPTION) {
                return;
            }
        }

        // Obtiene la fecha de cirugía del JDateChooser.
        LocalDate fechaCirugia = null;
        try {
            Date selectedDate = fechaCirugiaChooser.getDate();
            if (selectedDate != null) {
                fechaCirugia = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener la fecha de cirugía: " + e.getMessage() + ". Asegúrese de que el formato sea DD-MM-YYYY.",
                    "Error de Formato de Fecha",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dominancia = (String) Objects.requireNonNull(dominanciaCombo.getSelectedItem());
        int delayQx = (int) delayQxSpinner.getValue();
        String tipoDeCx = tipoDeCxField.getText().trim();

        try {
            if (patientToEdit == null) {
                // Crea un nuevo paciente si el diálogo se abrió en modo de adición.
                controller.createPatient(
                        nombre,
                        edad,
                        cleanRutForValidation, // RUT limpio y validado.
                        dominancia,
                        nombreLesion,
                        codigoOficialLesion,
                        diagnostico,
                        delayQx,
                        fechaCirugia,
                        tipoDeCx
                );
                JOptionPane.showMessageDialog(this, "Paciente agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // Actualiza un paciente existente si el diálogo se abrió en modo de edición.
                patientToEdit.setNombre(nombre);
                patientToEdit.setEdad(edad);
                patientToEdit.setRut(cleanRutForValidation);
                patientToEdit.setDominancia(dominancia);

                // Actualiza la lesión o crea una nueva si no existe.
                Lesion currentLesion = patientToEdit.getLesion();
                if (currentLesion == null) {
                    currentLesion = new Lesion();
                    patientToEdit.setLesion(currentLesion);
                }
                currentLesion.setNombreLesion(nombreLesion);
                currentLesion.setCodigoOficial(codigoOficialLesion);
                currentLesion.setDiagnostico(diagnostico);

                patientToEdit.setDelayQx(delayQx);
                patientToEdit.setFechaCirugia(fechaCirugia);
                patientToEdit.setTipoDeCx(tipoDeCx);

                controller.updatePatient(patientToEdit);
                JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // Cierra el diálogo después de guardar exitosamente.
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar paciente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}