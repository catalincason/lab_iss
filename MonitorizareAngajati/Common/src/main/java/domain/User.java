package domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "Users")
public class User extends Entity {
    private String nume;
    private String parola;

    public User() { }

    public User(String nume, String parola) {
        this.nume = nume;
        this.parola = parola;
    }

    @Id
    @Column(name = "nume")
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    @Column(name = "parola")
    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getNume(), user.getNume()) &&
                Objects.equals(getParola(), user.getParola());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNume(), getParola());
    }
}
