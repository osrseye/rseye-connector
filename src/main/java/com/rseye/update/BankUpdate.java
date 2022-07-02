package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;

import java.util.List;

public class BankUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private List<Item> items;

    public BankUpdate(String username, List<Item> items) {
        this.username = username;
        this.items = items;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.BANK_UPDATE;
    }
}
