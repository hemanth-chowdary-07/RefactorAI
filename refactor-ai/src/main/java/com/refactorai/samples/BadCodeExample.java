package com.refactorai.samples;

public class BadCodeExample {

    // This is a LONG method (>30 lines) - should be detected
    public void processOrder(String orderId) {
        System.out.println("Processing order: " + orderId);

        // Deep nesting example (>3 levels) - should be detected
        if (orderId != null) {
            if (orderId.length() > 0) {
                if (orderId.startsWith("ORD")) {
                    if (orderId.contains("-")) {
                        System.out.println("Valid order format");
                    }
                }
            }
        }

        // Magic numbers (should be constants) - should be detected
        int discount = 100 - 15;
        double taxRate = 0.08;
        int maxRetries = 3;

        for (int i = 0; i < 10; i++) {
            System.out.println("Line " + i);
        }

        for (int i = 0; i < 20; i++) {
            System.out.println("More lines " + i);
        }

        System.out.println("Order processed");
        System.out.println("Thank you");
        System.out.println("Goodbye");
    }

    // Another long method
    public void anotherLongMethod() {
        for (int i = 0; i < 50; i++) {
            System.out.println("Line " + i);
            if (i % 2 == 0) {
                System.out.println("Even");
            } else {
                System.out.println("Odd");
            }
        }
    }
}