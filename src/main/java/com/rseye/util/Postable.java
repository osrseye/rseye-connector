package com.rseye.util;

import com.rseye.io.RequestHandler;

public interface Postable {
    RequestHandler.Endpoint endpoint();
}
