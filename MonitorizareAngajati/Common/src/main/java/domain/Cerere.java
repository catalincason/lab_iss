package model;

import java.time.LocalDate;

public class Cerere extends Entity<Integer> {
    private int id;
    private LocalDate deadline;
    private Status status;

    public Cerere() {
    }

    public Cerere(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
