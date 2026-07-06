package com.obe.evaluation.common;

import lombok.Data;

@Data
public class PageQuery {
    private int page = 1;
    private int size = 20;

    public int getPage() { return Math.max(page, 1); }
    public int getSize() { return Math.min(Math.max(size, 1), 100); }
}
