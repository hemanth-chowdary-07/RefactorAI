package com.refactorai.samples;

public class GoodCodeExample {

    // This is a SHORT method (good practice)
    public void processOrder(String orderId) {
        if (isValidOrder(orderId)) {
            System.out.println("Processing: " + orderId);
        }
    }

    // Small, focused helper method
    private boolean isValidOrder(String orderId) {
        return orderId != null && orderId.length() > 0;
    }
}