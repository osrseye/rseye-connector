package com.rseye.update;

import com.rseye.util.Jsonable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class LoginUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private int combatLevel;

    @Getter
    @Setter
    private String state;

    public LoginUpdate(String username, int combatLevel, String state){
        this.username = username;
        this.combatLevel = combatLevel;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        LoginUpdate loginUpdate = (LoginUpdate) o;
        return Objects.equals(username, loginUpdate.username) && Objects.equals(combatLevel, loginUpdate.combatLevel) && Objects.equals(state, loginUpdate.state) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
