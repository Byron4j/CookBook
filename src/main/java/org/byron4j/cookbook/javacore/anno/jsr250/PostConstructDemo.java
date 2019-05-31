package org.byron4j.cookbook.javacore.anno.jsr250;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.PostConstruct;

@Setter
@Getter
@ToString
public class PostConstructDemo {

    private int age;

    @PostConstruct
    public void setAge(){
        age = 10;
    }

    public static void main(String[] args){
        System.out.println(new PostConstructDemo());
    }
}
