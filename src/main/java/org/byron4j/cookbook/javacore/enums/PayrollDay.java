package org.byron4j.cookbook.javacore.enums;

public enum PayrollDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    private static final int HOUR_PER_SHIFT = 8;

    double pay(double hourseWorked, double payRate){
        double basePay = hourseWorked * payRate;

        double overtimePay;

        switch (this){
            case SATURDAY:
            case SUNDAY:
                overtimePay = hourseWorked * payRate / 2;
             default:
                 overtimePay = hourseWorked <= HOUR_PER_SHIFT
                         ? 0 : (hourseWorked - HOUR_PER_SHIFT) * payRate / 2;
                 break;
        }

        return  basePay + overtimePay;
    }
}
