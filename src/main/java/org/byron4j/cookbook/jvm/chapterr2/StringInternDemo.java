package org.byron4j.cookbook.jvm.chapterr2;

public class StringInternDemo {
    public static void main(String[] args){
        //
        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern() == str1);

//        String str2 = new StringBuilder("Ja").append("va").toString();
//        System.out.println(str2.intern() == str2);

        String str2 = new StringBuilder("a").append("b").toString();
        System.out.println(str2.intern() == str2);


        String str3 = new StringBuilder("FK").append("YOU").toString();
        System.out.println(str3.intern() == str3);
    }
}
