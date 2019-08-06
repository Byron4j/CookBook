package org.byron4j.cookbook.designpattern.wrapper;

import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.junit.Test;

import java.io.FileInputStream;

import static java.lang.System.in;

/**
 * @program: cookbook
 * @author: Byron
 * @create: 2019/07/29
 */
public class WrapperTest {

    @Test
    public void 装饰器模式测试(){
        Car car = new WrapperCar(new QeqCar());
        car.run();
        car.stop();
    }

}
