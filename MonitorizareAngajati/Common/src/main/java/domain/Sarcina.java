package domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "Sarcini")
public class Sarcina extends Entity {
    private int id;
    private String descriere;
    private Date deadline;
    private User angajat;

    public Sarcina() {
    }

    public Sarcina(User angajat, String descriere, Date deadline) {
        this.descriere = descriere;
        this.deadline = deadline;
        this.angajat = angajat;
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

    @Column(name = "descriere")
    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "deadline")
    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "angajat")
    public User getAngajat() {
        return angajat;
    }

    public void setAngajat(User angajat) {
        this.angajat = angajat;
    }
}
