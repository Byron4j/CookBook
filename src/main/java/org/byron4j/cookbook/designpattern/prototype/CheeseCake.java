package org.byron4j.cookbook.designpattern.prototype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CheeseCake implements  Cake{

    /**
     * 糖果
     */
    private String sugar;

    /**
     * 黄油
     */
    private String butter;

    /**
     * 芝士
     */
    private String cheese;

    /**
     * 用户姓名
     */
    private String name;

    @Override
    public Cake prepareCake() {
        Cake cake = null;

        /**
         * 克隆存在的实例
         */
        try {
            cake = (Cake)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cake;
    }
}
