package model;

import java.time.LocalDate;

public class Sarcina extends Entity<Integer> {
    private int id;
    private String descriere;
    private LocalDate deadline;

    public Sarcina() {
    }

    public Sarcina(String descriere, LocalDate deadline) {
        this.descriere = descriere;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
