package gestiondehorarios;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Materia {
    private String nombreMateria;
    private int noCreditos;

    public Materia(String nombreMateria, int noCreditos) {
        this.nombreMateria = nombreMateria;
        this.noCreditos = noCreditos;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public int getNoCreditos() {
        return noCreditos;
    }

    public void setNoCreditos(int noCreditos) {
        this.noCreditos = noCreditos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Mismo objeto
        if (obj == null || getClass() != obj.getClass()) return false; // Diferente clase
        Materia materia = (Materia) obj; // Convertir a Materia
        return noCreditos == materia.getNoCreditos() && nombreMateria.equals(materia.getNombreMateria()); // Comparar atributos
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreMateria, noCreditos); // Generar un hash basado en los atributos
    }

}
