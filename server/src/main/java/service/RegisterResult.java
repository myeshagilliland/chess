package service;

import java.util.Objects;

public record RegisterResult(String username, String authToken) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterResult that = (RegisterResult) o;
        return Objects.equals(username(), that.username()) && Objects.equals(authToken(), that.authToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username(), authToken());
    }
}
