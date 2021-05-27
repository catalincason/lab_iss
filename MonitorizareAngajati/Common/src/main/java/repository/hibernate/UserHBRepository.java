package repository.hibernate;

import domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import repository.UserRepository;

import java.util.List;

public class UserHBRepository implements UserRepository {

    private SessionFactory sessionFactory;

    public UserHBRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User add(User entity) {
        return null;
    }

    @Override
    public void delete(String s) {

    }

    @Override
    public User get(String s) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = session.createQuery("from User where nume = :nume", User.class)
                        .setParameter("nume", s)
                        .uniqueResult();
                transaction.commit();
                return user;
            }
            catch (RuntimeException e) {
                if (transaction != null)
                    transaction.rollback();
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void update(User entity) {

    }
}
