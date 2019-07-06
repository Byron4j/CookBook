package org.byron4j.cookbook.javacore.enums;

public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;

    double apply(double x, double y) throws Exception{
        switch (this){
            case PLUS:
                return x + y;
            case MINUS:
                return x - y;
            case TIMES:
                return x * y;
            case DIVIDE:
                return x / y;
        }
        throw new Exception("Unknown op: " + this);
    }
}
