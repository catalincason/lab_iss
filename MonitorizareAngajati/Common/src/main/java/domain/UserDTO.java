package domain;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

public class UserDTO implements Serializable {
    private User user;
    private LocalTime time;

    public UserDTO(User user) {
        this.user = user;
    }

    public UserDTO(User user, LocalTime time) {
        this.user = user;
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(getUser(), userDTO.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser());
    }
}
