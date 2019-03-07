package org.byron4j.cookbook.javacore.anno.resolver;

@ByronAnno("类")
public class BusinessLogic {
    @ByronAnno("方法")
    public void sayHello(){
        System.out.println("org.byron4j.cookbook.javacore.anno.resolver.BusinessLogic.sayHello!");
    }

    public static void main(String[] args){
        Class clazz = BusinessLogic.class;
        if( clazz.isAnnotationPresent(ByronAnno.class) ){
            // 存在 ByronAnno 注解的话
            ByronAnno byronAnno = (ByronAnno)clazz.getAnnotation(ByronAnno.class);
            System.out.println("注解值为：" +byronAnno.value());
        }
    }
}
