package com.pm.commonutils.response;

public record ApiResponse<T>(
        boolean success,
        T data
) {
    public ApiResponse(T data) {
        this(true, data);
    }
}
