package com.obe.evaluation.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) { return new R<>(200, "success", data); }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> fail(int code, String msg) { return new R<>(code, msg, null); }
}
