package com.vn.capstone.domain.response.dtoAuth;

import com.vn.capstone.domain.response.RestResponse;

public class ResponseUtils {
    public static <T> RestResponse<T> success(T data, String message) {
        RestResponse<T> res = new RestResponse<>();
        res.setStatusCode(200);
        res.setMessage(message);
        res.setData(data);
        return res;
    }

    public static <T> RestResponse<T> error(String message, int statusCode) {
        RestResponse<T> res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setError("Error");
        return res;
    }
}
