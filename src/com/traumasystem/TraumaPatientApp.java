package com.traumasystem;

import com.formdev.flatlaf.FlatLightLaf;
import com.traumasystem.controller.PatientController;
import com.traumasystem.util.LesionCodeRegistry;
import com.traumasystem.view.MainFrame;

import javax.swing.*;
import java.io.File;
import java.util.Map;

/**
 * Clase principal de la aplicación "Trauma Patient App".
 * Se encarga de inicializar la interfaz de usuario, cargar configuraciones
 * y configurar el controlador principal para la gestión de pacientes.
 */
public class TraumaPatientApp {

    /**
     * Método principal de la aplicación.
     *
     * @param args Argumentos de la línea de comandos (no utilizados en esta aplicación).
     */
    public static void main(String[] args) {
        // 1. Inicialización del Look and Feel (FlatLaf).
        // Se intenta configurar un tema claro para la interfaz de usuario.
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            // En caso de fallo, se imprime un error en la consola, pero la aplicación continúa.
            System.err.println("Error al inicializar FlatLaf: " + e);
        }

        // 2. Carga de códigos de lesión.
        // Obtiene el registro de códigos de lesión precargados desde LesionCodeRegistry.
        Map<String, String> lesionCodes = LesionCodeRegistry.getLesionCodes();
        if (lesionCodes.isEmpty()) {
            // Si no se encuentran códigos, muestra una advertencia al usuario.
            JOptionPane.showMessageDialog(null,
                    "Advertencia: No se encontraron códigos de lesión cargados. " +
                            "Asegúrate de que la clase LesionCodeRegistry esté completa.",
                    "Advertencia de Códigos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("Cargados " + lesionCodes.size() + " códigos de lesión del registro.");
        }

        // 3. Configuración del directorio de datos.
        // Define la ruta donde se almacenarán los datos de los pacientes (en el directorio de inicio del usuario).
        String homeDir = System.getProperty("user.home");
        String dataDir = homeDir + File.separator + "TraumaPatientData";

        // 4. Inicialización del controlador de pacientes.
        // El controlador es responsable de la lógica de negocio y la interacción con los datos.
        PatientController controller = new PatientController(dataDir);

        // 5. Lanzamiento de la interfaz gráfica de usuario (GUI).
        // Se utiliza SwingUtilities.invokeLater para asegurar que la creación y
        // visualización de la GUI se realice en el Event Dispatch Thread (EDT),
        // lo cual es una buena práctica en aplicaciones Swing.
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(controller, lesionCodes);
            mainFrame.setVisible(true); // Hace visible la ventana principal de la aplicación.
        });
    }
}