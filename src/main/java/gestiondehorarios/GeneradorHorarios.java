package gestiondehorarios;
import java.util.*;

public class GeneradorHorarios {
    private static final int MAX_COMBINATIONS = 25;
    private static final Random RANDOM = new Random();

    public static ArrayList<PlantillaHorario> Backtracking(Set<Grupo> grupos) {
        ArrayList<PlantillaHorario> resultados = new ArrayList<>();
        PlantillaHorario plantillaActual = new PlantillaHorario(new ArrayList<>());
        backtrack(grupos, plantillaActual, resultados);
        return resultados;
    }

    private static void backtrack(Set<Grupo> grupos, PlantillaHorario plantillaActual, ArrayList<PlantillaHorario> resultados) {
        if (resultados.size() >= MAX_COMBINATIONS) {
            return;
        }

        if (grupos.isEmpty()) {
            resultados.add(new PlantillaHorario(new ArrayList<>(plantillaActual.getGrupos())));
            return;
        }

        Grupo grupoSeleccionado = getRandomGrupo(grupos);

        if (esValido(grupoSeleccionado, plantillaActual)) {
            plantillaActual.addGrupos(grupoSeleccionado);
            grupos.remove(grupoSeleccionado);
            backtrack(grupos, plantillaActual, resultados);
            grupos.add(grupoSeleccionado);
            plantillaActual.removeGrupos(grupoSeleccionado);
        }

        grupos.remove(grupoSeleccionado);
        backtrack(grupos, plantillaActual, resultados);
        grupos.add(grupoSeleccionado);
    }
    private static Grupo getRandomGrupo(Set<Grupo> grupos) {
        int index = RANDOM.nextInt(grupos.size());
        return grupos.stream().skip(index).findFirst().orElseThrow();
    }

    private static boolean esValido(Grupo grupo, PlantillaHorario plantilla) {
        for (Grupo existente : plantilla.getGrupos()) {
            if (PlantillaHorario.haySolapamiento(grupo, existente)) {
                return false;
            }
        }
        return true;
    }
}
