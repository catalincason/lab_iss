package repo;

import domain.Entity;

public interface Repository<ID, E extends Entity> {
    E add(E entity);

    void delete(ID id);

    E get(ID id);

    Iterable<E> getAll();

    void update(ID id);
}
