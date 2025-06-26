package com.traumasystem.view;

import com.traumasystem.controller.PatientController;
import com.traumasystem.model.Patient;
import com.traumasystem.util.ChileanValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Diálogo para mostrar los detalles completos de un paciente específico.
 * Permite visualizar toda la información del paciente de forma organizada.
 */
public class PatientDetailsDialog extends JDialog {
    private final PatientController controller;
    private final Patient patient;

    // Formateador para mostrar las fechas en un formato consistente.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Crea una nueva instancia del diálogo de detalles del paciente.
     *
     * @param parent El {@link JFrame} padre de este diálogo.
     * @param controller El controlador de pacientes, usado para cualquier interacción futura si se extiende la funcionalidad.
     * @param patient El objeto {@link Patient} cuyos detalles se mostrarán.
     */
    public PatientDetailsDialog(JFrame parent, PatientController controller, Patient patient) {
        super(parent, "Detalles del Paciente: " + patient.getNombre(), true);
        this.controller = controller;
        this.patient = patient;

        setSize(600, 600);
        setMinimumSize(new Dimension(550, 550));
        setLocationRelativeTo(parent);
        setResizable(true);

        initComponents();
        // El método populatePatientDetails ya no es necesario ya que los detalles se cargan en initComponents.
        // Si se añade lógica de actualización posterior, este método podría reactivarse.
    }

    /**
     * Inicializa y organiza todos los componentes de la interfaz de usuario del diálogo.
     * Muestra la información del paciente en un formato de dos columnas.
     */
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Diseño de cuadrícula con 2 columnas.
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Información del Paciente"));
        detailsPanel.setBackground(new Color(245, 245, 245));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Añade filas de detalles para la información personal del paciente.
        addDetailRow(detailsPanel, "ID:", patient.getId(), labelFont, valueFont);
        addDetailRow(detailsPanel, "Nombre:", patient.getNombre(), labelFont, valueFont);
        addDetailRow(detailsPanel, "Edad:", String.valueOf(patient.getEdad()), labelFont, valueFont);
        addDetailRow(detailsPanel, "RUT:", ChileanValidator.formatRut(patient.getRut()), labelFont, valueFont);
        addDetailRow(detailsPanel, "Dominancia:", patient.getDominancia(), labelFont, valueFont);

        detailsPanel.add(new JSeparator()); // Separador visual entre secciones.
        detailsPanel.add(new JSeparator());

        // Añade filas de detalles para la información de la lesión y cirugía del paciente.
        // Se utilizan operadores ternarios para manejar posibles valores nulos en el objeto Lesion.
        addDetailRow(detailsPanel, "Nombre Lesión:", patient.getLesion() != null ? patient.getLesion().getNombreLesion() : "N/A", labelFont, valueFont);
        addDetailRow(detailsPanel, "Código Lesión:", patient.getLesion() != null ? patient.getLesion().getCodigoOficial() : "N/A", labelFont, valueFont);
        addDetailRow(detailsPanel, "Diagnóstico:", patient.getLesion() != null ? patient.getLesion().getDiagnostico() : "N/A", labelFont, valueFont);
        addDetailRow(detailsPanel, "Delay QX (días):", String.valueOf(patient.getDelayQx()), labelFont, valueFont);
        addDetailRow(detailsPanel, "Fecha Cirugía:", patient.getFechaCirugia() != null ? patient.getFechaCirugia().format(DATE_FORMATTER) : "N/A", labelFont, valueFont);
        addDetailRow(detailsPanel, "Tipo de CX:", patient.getTipoDeCx(), labelFont, valueFont);
        addDetailRow(detailsPanel, "Estado Recuperación:", patient.isRecovered() ? "Recuperado" : "Activo", labelFont, valueFont);

        add(detailsPanel, BorderLayout.CENTER);

        // Panel para el botón de cerrar.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton closeButton = createStyledButton("Cerrar", "/icons/close_icon.png");
        closeButton.addActionListener(e -> dispose()); // Cierra el diálogo al hacer clic.
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Añade un par de etiquetas (etiqueta y valor) a un panel dado, con fuentes específicas.
     * Este método es un auxiliar para mantener el código de inicialización más limpio.
     *
     * @param panel El {@link JPanel} al que se añadirán las etiquetas.
     * @param labelText El texto de la etiqueta descriptiva (ej. "Nombre:").
     * @param valueText El texto del valor a mostrar (ej. el nombre real del paciente).
     * @param labelFont La fuente a aplicar a la etiqueta descriptiva.
     * @param valueFont La fuente a aplicar al valor.
     */
    private void addDetailRow(JPanel panel, String labelText, String valueText, Font labelFont, Font valueFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        panel.add(label);

        JLabel value = new JLabel(valueText);
        value.setFont(valueFont);
        panel.add(value);
    }

    // Este método ya no realiza ninguna operación de población de datos directa,
    // ya que los campos se llenan durante la inicialización de componentes.
    // Se mantiene por si se desea añadir lógica de carga o actualización dinámica en el futuro.
    private void populatePatientDetails() {
        // Lógica de población futura, si es necesaria.
    }

    /**
     * Crea un {@link JButton} con un estilo uniforme, incluyendo texto e icono.
     *
     * @param text El texto que se mostrará en el botón.
     * @param iconPath La ruta al recurso del icono que se cargará para el botón.
     * @return Un {@link JButton} configurado con el estilo predefinido.
     */
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(66, 133, 244)); // Un tono de azul de Google.
        button.setOpaque(true); // Hace que el fondo se pinte.
        button.setBorderPainted(false); // Elimina el borde nativo del botón.


        return button;
    }
}