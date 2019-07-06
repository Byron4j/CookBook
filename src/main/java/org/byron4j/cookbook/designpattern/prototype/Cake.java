package org.byron4j.cookbook.designpattern.prototype;

/**
 * 蛋糕的接口类型；可以clone
 */
public interface Cake extends  Cloneable{
    /**
     * 准备生产蛋糕
     * @return
     */
    public Cake prepareCake();
}
