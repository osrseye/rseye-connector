package com.rseye.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class Login extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String state;

    public Login(String username, String state){
        this.username = username;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Login login = (Login) o;
        return Objects.equals(state, login.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
