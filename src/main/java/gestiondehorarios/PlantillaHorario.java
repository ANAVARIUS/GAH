package gestiondehorarios;

import java.util.List;
import  java.util.ArrayList;
import java.time.DayOfWeek;
import java.util.Objects;

public class PlantillaHorario {
    private List<Grupo> grupos;

    public PlantillaHorario(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    public void addGrupos(Grupo grupo){
        if (grupos.contains(grupo)) {
            throw new IllegalArgumentException("El grupo " + grupo + " ya está en la plantilla.");
        }
        grupos.add(grupo);
    }

    public void removeGrupos(Grupo grupo){
        if (!grupos.remove(grupo)) {
            throw new IllegalArgumentException("El grupo " + grupo + " no está en la plantilla, no se puede eliminar.");
        }
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public static boolean haySolapamiento(Grupo grupo1, Grupo grupo2) {
        for (DayOfWeek dia : grupo1.obtenerHorario().keySet()) {
            if (grupo2.obtenerHorario().containsKey(dia)) {
                Grupo.Horario horarioGrupo1 = grupo1.obtenerHorario().get(dia);
                Grupo.Horario horarioGrupo2 = grupo2.obtenerHorario().get(dia);

                // Verificar solapamiento entre horarios
                if (horarioGrupo1.getHoraInicio().isBefore(horarioGrupo2.getHoraFin()) &&
                        horarioGrupo1.getHoraFin().isAfter(horarioGrupo2.getHoraInicio())) {
                    return true; // Hay solapamiento
                }
            }
        }
        return false; // No hay solapamiento
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantillaHorario that = (PlantillaHorario) o;
        return Objects.equals(grupos, that.grupos); // Compare relevant fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(grupos); // Include relevant fields
    }
}
