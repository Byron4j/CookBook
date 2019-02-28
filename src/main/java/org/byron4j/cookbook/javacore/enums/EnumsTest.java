package org.byron4j.cookbook.javacore.enums;

import lombok.extern.slf4j.Slf4j;


public class EnumsTest {
    public static void main(String[] args){
        for(LighjtOriginColorEnums ele : LighjtOriginColorEnums.values()){
            System.out.println(ele + " int value is: " + ele.ordinal());
        }

        System.out.println("LighjtOriginColorEnums size is: " + LighjtOriginColorEnums.values().length);


    }
}
