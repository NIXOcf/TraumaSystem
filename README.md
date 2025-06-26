Trauma System - Aplicación de Gestión de Pacientes
Descripción del Proyecto
Trauma System es una aplicación de escritorio robusta y fácil de usar, desarrollada en Java Swing, diseñada para optimizar la gestión de pacientes en servicios de traumatología. Su objetivo principal es ofrecer una solución intuitiva para registrar, consultar, editar y organizar la información detallada de los pacientes, incluyendo datos personales, historial de lesiones y detalles de cirugías.

Características Principales
Registro Completo de Pacientes: Permite añadir nuevos pacientes con campos para nombre, edad, RUT, dominancia, y todos los detalles relevantes sobre su lesión y cirugía.

Gestión Detallada de Lesiones: Incluye campos específicos para el nombre de la lesión, el código oficial de lesión (con autocompletado y validación) y el diagnóstico asociado.

Información de Cirugía: Registra el "Delay Qx" (días hasta la cirugía), la fecha de cirugía y el tipo de procedimiento realizado.

Edición y Actualización Fácil: Funcionalidad para modificar la información de pacientes existentes, asegurando que los datos estén siempre al día.

Búsqueda Avanzada y Flexible: Permite buscar pacientes utilizando múltiples criterios como nombre, RUT, código de lesión, diagnóstico y fecha de cirugía.

Validación de Datos Integrada: Incorpora validación de RUT chileno y control de formato para los códigos de lesión, mejorando la integridad de los datos.

Interfaz de Usuario Moderna: Desarrollada con Java Swing y FlatLaf para ofrecer una experiencia visual limpia y moderna.

Persistencia de Datos: Los datos de los pacientes se guardan localmente para asegurar su disponibilidad y persistencia entre sesiones.

Tecnologías Utilizadas
Lenguaje de Programación: Java (versión 11 o superior).

Interfaz Gráfica (GUI): Java Swing.

Estilo Visual (Look and Feel): FlatLaf.

Selector de Fechas: JCalendar (específicamente JDateChooser).

Validación Específica: Clase ChileanValidator para RUTs.

Registro de Códigos: Clase LesionCodeRegistry para códigos de lesión.

Gestión de Datos: Almacenamiento y recuperación de objetos de paciente (probablemente a través de serialización a archivo, como JSON, aunque no se especifica el método exacto en el código proporcionado).

Control de Versiones: Git & GitHub.

Requisitos del Sistema
Para ejecutar Trauma System, necesitarás:

Java Development Kit (JDK) 11 o superior instalado en tu sistema.

Un sistema operativo compatible con aplicaciones Java (Windows, macOS, Linux).
Estructura del Proyecto
El proyecto sigue una arquitectura de diseño basada en Modelo-Vista-Controlador (MVC) para una mejor organización y mantenibilidad del código:

src/main/java/com/traumasystem/: Paquete raíz de la aplicación.

controller/: Contiene la lógica de negocio, coordina las interacciones entre la vista y el modelo, y maneja la persistencia de datos.

model/: Define las clases de datos principales, como Patient y Lesion.

view/: Alberga todas las clases de la interfaz de usuario, incluyendo ventanas (MainFrame) y diálogos (PatientFormDialog, AdvancedSearchDialog, PatientDetailsDialog).

util/: Contiene clases de utilidad diversas, como ChileanValidator (para validación de RUT), IconLoader (para cargar íconos) y LesionCodeRegistry (para la gestión de códigos de lesión).

src/main/resources/icons/: Directorio donde se almacenan los íconos utilizados en la interfaz gráfica de usuario.
