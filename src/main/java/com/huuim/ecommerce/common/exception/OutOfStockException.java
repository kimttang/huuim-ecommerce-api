package com.huuim.ecommerce.common.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
        super("재고 부족");
    }
}