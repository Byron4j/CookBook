package org.byron4j.cookbook.javacore.enums;

public enum OperationGracefulField {
    PLUS("+"){
        @Override
        double apply(double x, double y) {
            return x + y;
        }
    }, MINUS("-") {
        @Override
        double apply(double x, double y) {
            return x - y;
        }
    }, TIMES("*") {
        @Override
        double apply(double x, double y) {
            return x * y;
        }
    }, DIVIDE("/") {
        @Override
        double apply(double x, double y) {
            return x / y;
        }
    };

    // 属性
    private String symbol;
    OperationGracefulField(String symbol){
        this.symbol = symbol;
    }

    abstract double apply(double x, double y);
}
