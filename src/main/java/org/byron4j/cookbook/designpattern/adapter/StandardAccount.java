package org.byron4j.cookbook.designpattern.adapter;

/**
 * 普通账户： 不能透支
 */
public class StandardAccount extends AbstractAccount {

    public StandardAccount(final double balance) {
        super(balance);
        // 不能透支
        setOverdraftAvailable(false);
    }
}
