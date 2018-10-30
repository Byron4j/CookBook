package org.byron4j.cookbook.designpattern.adapter;

/**
 * 白金帐户: 可以透支
 */
public class PlatinumAccount extends AbstractAccount {

    public PlatinumAccount(final double balance) {
        super(balance);
        // 可以透支
        setOverdraftAvailable(true);
    }
}
