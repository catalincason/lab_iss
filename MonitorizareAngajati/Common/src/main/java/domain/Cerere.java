package domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "Cereri")
public class Cerere extends Entity {
    private int id;
    private Sarcina sarcina;
    private Date deadline;
    private Status status;

    public Cerere() {
    }

    public Cerere(Sarcina sarcina, Date deadline) {
        this.sarcina = sarcina;
        this.deadline = deadline;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sarcina")
    public Sarcina getSarcina() {
        return sarcina;
    }

    public void setSarcina(Sarcina sarcina) {
        this.sarcina = sarcina;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "deadline")
    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
