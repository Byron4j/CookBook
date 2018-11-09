package org.byron4j.cookbook.designpattern.adapter;


/**
 * 离岸账户
 */
public class OffshoreAccount {

    private double balance;

    /**税率*/
    private static final double TAX_RATE = 0.03;

    public OffshoreAccount(final double balance) {
        this.balance = balance;
    }

    public double getTaxRate() {
        return TAX_RATE;
    }

    public double getOffshoreBalance() {
        return balance;
    }

    public void debit(final double debit) {
        if (balance >= debit) {
            balance -= debit;
        }
    }

    public void credit(final double credit) {
        balance += balance;
    }
}
