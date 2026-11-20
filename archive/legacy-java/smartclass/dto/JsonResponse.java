package com.smartclass.dto;

public class JsonResponse {
    private boolean success;
    private String message;
    private Object data;

    private JsonResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static JsonResponse success(String message) {
        return new JsonResponse(true, message, null);
    }

    public static JsonResponse error(String message) {
        return new JsonResponse(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}