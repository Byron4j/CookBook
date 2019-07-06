package org.byron4j.cookbook.javacore.enums;

import java.util.EnumSet;
import java.util.Set;

public class EnumSetDemo {
    public enum Style{
        BOLD,
        ITALIC,
        UNDERLINE,
        STRIKETHROUGH
    }

    public void applyStyle(Set<Style> styles){
        // TODO
    }

    public static void main(String[] args){
        EnumSetDemo enumSetDemo = new EnumSetDemo();
        enumSetDemo.applyStyle(EnumSet.of(Style.BOLD, Style.ITALIC));
    }
}
