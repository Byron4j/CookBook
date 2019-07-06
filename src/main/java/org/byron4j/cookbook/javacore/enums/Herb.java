package org.byron4j.cookbook.javacore.enums;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Herb {
    A("A", Type.ANNUAL),
    B("B", Type.BIENNIAL),
    AA("AA", Type.ANNUAL),
    BB("BB", Type.BIENNIAL),
    C("C", Type.PERENNIAL),
    ;

    public enum Type{
        ANNUAL, PERENNIAL, BIENNIAL
    }

    private String name;
    private Type type;

    Herb(String name, Type type){
        this.name = name;
        this.type = type;
    }


    @Override
    public String toString() {
        return name;
    }

    public static void main(String[] args){
        Herb[] garden = new Herb[]{Herb.A, Herb.B, Herb.AA, Herb.BB, Herb.C};

        // EnumMap 构造器需要指定 class 作为类型参数
        Map<Herb.Type, Set<Herb>> herbsByType =
                new EnumMap<Type, Set<Herb>>(Type.class);

        for (Type t : Herb.Type.values()){
            // 按类型分类
            herbsByType.put(t, new HashSet<Herb>());
        }

        // 开始对花园处理
        for (Herb h : garden){
            herbsByType.get(h.type).add(h);
        }

        // 输出分类信息
        System.out.println(herbsByType);
    }

}
