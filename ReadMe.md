<h1>GESTOR DE ALTERNATIVAS HORARIAS</h1>
<body>
    # Gestor de Alternativas Horarias

Gestor de Alternativas Horarias es una aplicación de escritorio desarrollada en JavaFX que permite a los usuarios organizar sus horarios académicos de manera eficiente. Con esta herramienta, los usuarios pueden registrar grupos y materias de interés, generar combinaciones de horarios sin conflictos y exportar las opciones seleccionadas en diferentes formatos.

## Características

1. **Gestión de Materias y Grupos**:
   - Agregar, editar y eliminar grupos o materias desde una interfaz simple.
   - Validación de entradas para evitar duplicados o datos inválidos.

2. **Generación de Horarios**:
   - Generación automática de combinaciones de horarios utilizando un algoritmo de retroceso (backtracking).
   - Filtrado de horarios según grupos llenos u otras preferencias.
   - Optimización del rendimiento con poda del árbol de búsqueda y limitación a las 10 mejores combinaciones en caso de baja velocidad.

3. **Visualización y Edición**:
   - Interfaz intuitiva para visualizar los horarios generados.
   - Botón para guardar cambios y regenerar horarios en función de las modificaciones realizadas.

4. **Exportación de Horarios** (en proceso):
   - Exporta los horarios generados en formatos como JPEG, PNG o PDF.

5. **Almacenamiento Persistente**:
   - Utiliza SQLite para garantizar que los datos se guarden incluso después de cerrar la aplicación.

6. **Manejo de Errores y Validaciones**:
   - Validación avanzada para evitar horarios duplicados o conflictos.
   - Gestión de excepciones para asegurar una experiencia de usuario fluida.

## Requisitos del Sistema

- **Java**: JDK 11 o superior.
- **Maven**: Para gestionar las dependencias del proyecto.
- **JavaFX**: Framework para la interfaz gráfica.
- **SQLite**: Base de datos utilizada para almacenamiento persistente.

## Instalación y Configuración

1. Clona este repositorio.
2. Asegúrate de tener configurado Maven y JavaFX en tu entorno.
3. Ejecuta el proyecto:
   mvn javafx:run
4. (Opcional) Si deseas construir un archivo ejecutable:
	mvn package

## Tecnologías Utilizadas

JavaFX: Interfaz gráfica.
SQLite: Base de datos para persistencia de datos.
Maven: Gestión de dependencias y construcción del proyecto.
SceneBuilder: Diseño visual de la interfaz de usuario.
</body>
