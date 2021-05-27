package repository.hibernate;

import domain.Cerere;
import domain.Sarcina;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import repository.CerereRepository;

import java.util.List;

public class CerereHBRepository implements CerereRepository {
    private SessionFactory sessionFactory;

    public CerereHBRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Cerere add(Cerere entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(entity);
                Integer id = session.createQuery("select max(id) from Cerere ", Integer.class)
                        .uniqueResult();
                transaction.commit();
                entity.setId(id);
                return entity;
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return null;
    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public Cerere get(Integer integer) {
        return null;
    }

    @Override
    public List<Cerere> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Cerere> cereri = session.createQuery("from Cerere ", Cerere.class)
                        .list();
                transaction.commit();
                return cereri;
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return null;
    }

    @Override
    public void update(Cerere entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.update(entity);
                transaction.commit();
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
    }
}
