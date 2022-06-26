package com.rseye.update;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.QuestState;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuestUpdate extends Jsonable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private CopyOnWriteArrayList<Quest> questChanges;

    public QuestUpdate(String username, CopyOnWriteArrayList<Quest> questChanges) {
        this.username = username;
        this.questChanges = questChanges;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        QuestUpdate s = (QuestUpdate) o;
        return Objects.equals(username, s.username) && Objects.equals(questChanges, s.questChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, questChanges);
    }

    /**
     * QuestChanges inner Quest class used for comparison
     */
    public static class Quest {
        @Getter
        @Setter
        private Integer id;

        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private QuestState state;

        public Quest(Integer id, String name, QuestState state) {
            this.id = id;
            this.name = name;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Quest object = (Quest) o;
            return Objects.equals(id, object.id) && Objects.equals(name, object.name) && state == object.state;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, state);
        }
    }
}
