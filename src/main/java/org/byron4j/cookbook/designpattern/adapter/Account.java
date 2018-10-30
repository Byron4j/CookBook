package org.byron4j.cookbook.designpattern.adapter;

/**
 * 账户接口类型
 */
public interface Account {
    /**
     * 获取账户余额
     * @return
     */
    public double getBalance();

    /**
     * 是否可以透支
     * @return
     */
    public boolean isOverdraftAvailable();

    /**
     * 贷款; 贷款后账户余额增多
     * @param credit
     */
    public void credit(final double credit);
}
