package repository;

import domain.Entity;

import java.util.List;

public interface Repository<ID, E extends Entity> {
    E add(E entity);

    void delete(ID id);

    E get(ID id);

    List<E> getAll();

    void update(E entity);
}
