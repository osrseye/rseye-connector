package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.QuestState;

import java.util.concurrent.CopyOnWriteArrayList;

public class QuestUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private int questPoints;

    @Getter
    @Setter
    private CopyOnWriteArrayList<Quest> questChanges;

    public QuestUpdate(String username, int questPoints, CopyOnWriteArrayList<Quest> questChanges) {
        this.username = username;
        this.questPoints = questPoints;
        this.questChanges = questChanges;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.QUEST_UPDATE;
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
    }
}
