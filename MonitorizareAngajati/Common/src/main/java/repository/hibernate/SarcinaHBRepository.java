package repository.hibernate;

import domain.Sarcina;
import domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import repository.SarcinaRepository;

import java.util.List;

public class SarcinaHBRepository implements SarcinaRepository {
    private SessionFactory sessionFactory;

    public SarcinaHBRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Sarcina add(Sarcina entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(entity);
                Integer id = session.createQuery("select max(id) from Sarcina", Integer.class)
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
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Sarcina sarcina = new Sarcina();
                sarcina.setId(integer);
                session.delete(sarcina);
                transaction.commit();
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
    }

    @Override
    public Sarcina get(Integer integer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Sarcina sarcina = session.createQuery("from Sarcina where id=:id", Sarcina.class)
                        .setParameter("id", integer)
                        .uniqueResult();
                transaction.commit();
                return sarcina;
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return null;
    }

    @Override
    public List<Sarcina> getAll() {
        return null;
    }

    @Override
    public void update(Sarcina entity) {
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

    @Override
    public List<Sarcina> getSarciniForUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Sarcina> sarcini = session.createQuery("from Sarcina where angajat=:user", Sarcina.class)
                        .setParameter("user", user)
                        .list();
                transaction.commit();
                return sarcini;
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return null;
    }
}
