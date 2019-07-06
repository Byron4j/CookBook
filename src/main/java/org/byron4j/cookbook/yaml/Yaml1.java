package org.byron4j.cookbook.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Map;

public class Yaml1 {
    public static void main(String[] args)  {
        try {
            Yaml yaml = new Yaml();
            URL url = Yaml1.class.getClassLoader().getResource("/application.yml");
            if (url != null) {
                //获取test.yaml文件中的配置数据，然后转换为obj，
                Object obj =yaml.load(new FileInputStream("/application.yml"));
                System.out.println(obj);
                //也可以将值转换为Map
                Map map =(Map)yaml.load(new FileInputStream(url.getFile()));
                System.out.println(map);
                //通过map我们取值就可以了.

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
