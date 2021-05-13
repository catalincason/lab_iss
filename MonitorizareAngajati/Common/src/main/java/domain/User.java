package model;

public class User extends Entity<String> {
    private String nume;
    private String parola;

    public User() { }

    public User(String nume, String parola) {
        this.nume = nume;
        this.parola = parola;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
}
