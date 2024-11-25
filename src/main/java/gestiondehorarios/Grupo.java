package gestiondehorarios;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Grupo {
    private String nombreProfesor;
    private Map<DayOfWeek, Horario> diasyHoras;
    private Materia materia;

    public Grupo(String nombreProfesor, Materia materia, Map<DayOfWeek, Horario> diasyHoras) {
        this.nombreProfesor = nombreProfesor;
        if (diasyHoras == null) {
            this.diasyHoras = new HashMap<>();
        } else {
            this.diasyHoras = diasyHoras;
        }
        this.materia = materia;
    }

    public void agregarHorario(DayOfWeek dia, Horario nuevoHorario){
        if (diasyHoras == null) {
            diasyHoras = new HashMap<>();
        }
        // Verificar si ya existe un horario para el día dado
        if (diasyHoras.containsKey(dia)) {
            Horario horarioExistente = diasyHoras.get(dia);
        }
        // Agregar o reemplazar el horario para el día dado
        diasyHoras.put(dia, nuevoHorario);
    }

    public Map<DayOfWeek, Horario> obtenerHorario()
    {
        return this.diasyHoras;
    }

    public String getNombreProfesor() {
        return nombreProfesor;
    }

    public void setNombreProfesor(String nombreProfesor) {
        this.nombreProfesor = nombreProfesor;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public static class Horario{
        private LocalTime horaInicio;
        private LocalTime horaFin;

        public Horario(LocalTime horaInicio, LocalTime horaFin) {
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }

        public LocalTime getHoraInicio() {
            return horaInicio;
        }

        public LocalTime getHoraFin() {
            return horaFin;
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Grupo)) return false;
        Grupo other = (Grupo) obj;
        return nombreProfesor.equals(other.nombreProfesor) &&
                diasyHoras.equals(other.diasyHoras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreProfesor, diasyHoras);
    }
}
