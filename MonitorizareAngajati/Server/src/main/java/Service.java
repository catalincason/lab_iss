import domain.*;
import networking.LoginFailedExeption;
import networking.Observer;
import networking.Services;
import repository.CerereRepository;
import repository.SarcinaRepository;
import repository.UserRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements Services {
    private UserRepository userRepository;
    private SarcinaRepository sarcinaRepository;
    private CerereRepository cerereRepository;

    private List<Observer> loggedClients;

    public Service(UserRepository userRepository, SarcinaRepository sarcinaRepository, CerereRepository cerereRepository) {
        this.userRepository = userRepository;
        this.sarcinaRepository = sarcinaRepository;
        this.cerereRepository = cerereRepository;
        loggedClients = new ArrayList<>();
    }

    @Override
    public synchronized void login(User user, Observer client) throws LoginFailedExeption {
        User user1 = userRepository.get(user.getNume());
        if (user1 == null || !user1.getParola().equals(user.getParola()))
            throw new LoginFailedExeption("Username or password incorrect");
        loggedClients.add(client);
        if (!user.getNume().equals("sef"))
            notifyClientsUserLoggedIn(user);
    }

    private void notifyClientsUserLoggedIn(User user) {
        loggedClients.get(0).userLoggedIn(new UserDTO(user, LocalTime.now()));
    }

    @Override
    public synchronized void logout(User user, Observer client) {
        loggedClients.remove(client);
        if (!user.getNume().equals("sef"))
            notifyClientsUserLoggedOut(user);
    }

    @Override
    public void sendSarcina(Sarcina sarcina) {
        sarcina = sarcinaRepository.add(sarcina);
        notifyClientsSarcinaSent(sarcina);
    }

    @Override
    public List<Sarcina> getSarcini(User user) {
        return sarcinaRepository.getSarciniForUser(user);
    }

    @Override
    public void deleteSarcina(Sarcina sarcina) {
        sarcinaRepository.delete(sarcina.getId());
    }

    @Override
    public void sendCerere(Cerere cerere) {
        cerere = cerereRepository.add(cerere);
        notifyClientsCerereSent(cerere);
    }

    @Override
    public List<Cerere> getCereri() {
        return cerereRepository.getAll();
    }

    @Override
    public void updateCerere(Cerere cerere) {
        cerereRepository.update(cerere);
        if (cerere.getStatus() == Status.ACCEPTAT) {
            Sarcina sarcina = sarcinaRepository.get(cerere.getSarcina().getId());
            sarcina.setDeadline(cerere.getDeadline());
            sarcinaRepository.update(sarcina);
        }
        notifyClientsCerereUpdated(cerere);
    }

    private void notifyClientsCerereUpdated(Cerere cerere) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        loggedClients.forEach(client -> service.execute(() -> client.cerereUpdated(cerere)));
        service.shutdown();
    }

    private void notifyClientsCerereSent(Cerere cerere) {
        loggedClients.get(0).cerereSent(cerere);
    }

    private void notifyClientsSarcinaSent(Sarcina sarcina) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        loggedClients.subList(1, loggedClients.size()).forEach(client -> service.execute(() -> client.sarcinaSent(sarcina)));
        service.shutdown();
    }

    private void notifyClientsUserLoggedOut(User user) {
        loggedClients.get(0).userLoggedOut(new UserDTO(user));
    }
}
