package org.byron4j.cookbook.designpattern.adapter;

/**
 * 账户适配器； 适配器继承于目标账户。
 * 适配器（转接头）的目标是将离岸账户(会议室中的Mac电脑)转为 AbstractAccount(可以连接投影仪)
 */
public class AccountAdapter extends AbstractAccount {

    /**需要被适应的账户--适配者*/
    private OffshoreAccount offshoreAccount;

    /**
     *
     * @param offshoreAccount  适配者--会议室中的 mac 电脑
     */
    public AccountAdapter(final OffshoreAccount offshoreAccount) {
        super(offshoreAccount.getOffshoreBalance());

        // 适配器持有适配者的引用
        this.offshoreAccount = offshoreAccount;
    }

    /**
     * 计算扣除税款后的离岸账户余额
     * @return
     */
    @Override
    public double getBalance() {
        // 离岸税率
        final double taxRate = offshoreAccount.getTaxRate();

        // 离岸账户余额
        final double grossBalance = offshoreAccount.getOffshoreBalance();

        // 需要扣除的税款
        final double taxableBalance = grossBalance * taxRate;

        // 扣除离岸税款后的账户余额
        final double balanceAfterTax = grossBalance - taxableBalance;

        return balanceAfterTax;
    }
}
