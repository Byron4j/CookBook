package org.byron4j.cookbook.designpattern.segregation;

/**
 * 施乐公司系统机器接口
 */
public interface IMachine {
    /**
     * 打印
     */
    public void print();

    /**
     * 装订
     */
    public void staple();

    /**
     * 扫描
     */
    public void scan();

    /**
     * 复印
     */
    public void photoCopy();
}
