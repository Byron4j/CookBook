package org.byron4j.cookbook.javacore.enums;

public enum BasicOperation implements OperationI{
    PLUS("+"){
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    }, MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    }, TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    }, DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    // 属性
    private String symbol;
    BasicOperation(String symbol){
        this.symbol = symbol;
    }


}
