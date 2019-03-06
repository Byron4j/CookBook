package org.byron4j.cookbook.javacore.anno;

public class AnnoDemo {
    @Deprecated
    private static void sayHello(){

    }

    @SuppressWarnings("finally")
    public String finallyTest(String str)
    {
        try
        {
            str+=".";
        }
        finally
        {
            return str.toUpperCase();
        }
    }

    public static void main(String[] args){
        AnnoDemo.sayHello();
    }
}
