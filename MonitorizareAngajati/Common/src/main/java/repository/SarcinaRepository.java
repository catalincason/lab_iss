package repository;

import domain.Sarcina;
import domain.User;

import java.util.List;

public interface SarcinaRepository extends Repository<Integer, Sarcina> {
    List<Sarcina> getSarciniForUser(User user);
}
