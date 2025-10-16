package com.pm.commonutils.response;

import java.time.Instant;

public record ErrorResponse<T>(
        T data,
        boolean success,
        Instant timeStamp
) {
    public ErrorResponse(T data) {
        this(data, false, Instant.now());
    }
}
