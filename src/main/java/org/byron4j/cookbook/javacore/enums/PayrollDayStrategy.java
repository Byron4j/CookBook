package org.byron4j.cookbook.javacore.enums;

public enum PayrollDayStrategy {
    MONDAY(PayType.WEEKDAY),
    TUESDAY(PayType.WEEKDAY),
    WEDNESDAY(PayType.WEEKDAY),
    THURSDAY(PayType.WEEKDAY),
    FRIDAY(PayType.WEEKDAY),
    SATURDAY(PayType.WEEKEND),
    SUNDAY(PayType.WEEKEND);

    private final PayType payType;

    PayrollDayStrategy(PayType payType){
        this.payType = payType;
    }

    private enum PayType{
        WEEKDAY{
            @Override
            double overtimePay(double hrs, double payRate) {
                return hrs <= HOUR_PER_SHIFT ?
                        0 : (hrs - HOUR_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            @Override
            double overtimePay(double hrs, double payRate) {
                return hrs * payRate /2;
            }
        };

        private static final int HOUR_PER_SHIFT = 8;

        abstract double overtimePay(double hrs, double payRate);

        double pay( double hoursWork, double payRate ){
            double basePay = hoursWork * payRate;
            return basePay + overtimePay(hoursWork, payRate);
        }
    }
}
