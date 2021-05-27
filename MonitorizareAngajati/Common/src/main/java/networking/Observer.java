package networking;

import domain.Cerere;
import domain.Sarcina;
import domain.UserDTO;

public interface Observer {
    void userLoggedIn(UserDTO user);

    void userLoggedOut(UserDTO user);

    void sarcinaSent(Sarcina sarcina);

    void cerereSent(Cerere cerere);

    void cerereUpdated(Cerere cerere);
}
