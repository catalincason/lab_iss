package networking;

import domain.Cerere;
import domain.Sarcina;
import domain.Status;
import domain.User;

import java.util.List;

public interface Services {
    void login(User user, Observer client) throws LoginFailedExeption;

    void logout(User user, Observer client);

    void sendSarcina(Sarcina sarcina);

    List<Sarcina> getSarcini(User user);

    void deleteSarcina(Sarcina sarcina);

    void sendCerere(Cerere cerere);

    List<Cerere> getCereri();

    void updateCerere(Cerere cerere);
}
