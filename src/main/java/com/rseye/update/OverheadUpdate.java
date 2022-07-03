package com.rseye.update;

import com.rseye.io.RequestHandler;
import com.rseye.util.Postable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.HeadIcon;

public class OverheadUpdate implements Postable {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private HeadIcon overhead;

    public OverheadUpdate(String username, HeadIcon overhead) {
        this.username = username;
        this.overhead = overhead;
    }

    @Override
    public RequestHandler.Endpoint endpoint() {
        return RequestHandler.Endpoint.OVERHEAD_UPDATE;
    }
}
