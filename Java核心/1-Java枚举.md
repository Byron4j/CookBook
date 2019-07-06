# Java核心（一）枚举

Java 1.5 发行版本增加了新的引用类型： **枚举**， 在其之前，我们使用枚举类型值的时候通常是借助常量组成合法值的类型，例如表示光的三原色：红黄蓝的代码表示可能是以下这样的。

```java
    /*******************光的三原色*********************/
    public static final int LIGHT_RED = 1;
    public static final int LIGHT_YELLOW = 2;
    public static final int LIGHT_BLUE = 3;

    /*******************颜料的三原色*********************/
    public static final int PIGMENT_RED = 1;
    public static final int PIGMENT_YELLOW = 2;
    public static final int PIGMENT_BLUE = 3;
```

但是这样使用功能是受限的，比如不能知道对应枚举的个数等。幸好，Java 1.5引入了枚举类型Enum。


## 枚举的特性

### Java 的枚举类型的父类均为 java.lang.Enum

### Java的枚举本质上是int值

使用枚举类型将前面的使用常量方式调整如下：
```java
public enum LighjtOriginColorEnums {
    RED,
    YELLOW,
    BLUE
}

public enum PigmentOriginColorEnums {
    RED,
    YELLOE,
    BLUE;
}

public static void main(String[] args){
    for(LighjtOriginColorEnums ele : LighjtOriginColorEnums.values()){
        System.out.println(ele + " int value is: " + ele.ordinal());
    }
}

```

输出结果为：
```
RED int value is: 0
YELLOW int value is: 1
BLUE int value is: 2
```

枚举类型的 ```ordinal()``` 方法，可以得到枚举的int整型值，该方法在 ```Enum``` 中定义，是一个不可覆盖的方法：

```java
public final int ordinal() {
    return ordinal;
}
```
该方法返回枚举的 ```ordinal``` 属性值， 该值默认是枚举在其定义中的未知的索引值， 从0开始，即 RED.ordinal = 0, YELLOW.ordinal = 1, BLUE.ordinal = 2。ordinal的值大都数情况下是不会用到的。

### 枚举不能被实例化

>**关于枚举的说明：**
>
>枚举是不能实例化的，只能声明后，再使用
>
>枚举不能实例化，所以是单例的
>
>同理，枚举也是线程安全的
>
>所以可以使用枚举实现单例模式


### 枚举提供了编译时的类型安全
  如果声明一个参数类型为 ```LighjtOriginColorEnums```，则可以保证传递到该方法的任何非 null 的参数必须为 LighjtOriginColorEnums 中的三个枚举值之一。
  
### 枚举禁用了 clone 方法
```java
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
```

### 枚举不会被finalize
```java
    /**
     * enum classes cannot have finalize methods.
     */
    protected final void finalize() { }
```

### 枚举禁用了反序列化
```java
    /**
     * prevent default deserialization
     */
    private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize enum");
    }
```

正因为枚举具有这些特性，所以我们可以使用枚举实现友好的单例模式，请见: [【设计模式】你的单例模式真的是生产可用的吗？](https://juejin.im/post/5c5b9315518825620b44fbb3)


## 枚举可以定义方法

看一个简单的示例，表示一个互金公司的金额项：本金、利息、手续费、滞纳金 的枚举：
```acquireOffsetItemByCode(String code)``` 方法可以根据枚举的code属性，获得枚举。 
```java
    public enum OffsetItemEnums {

    /**
     * 手续费
     */
    offsetitem_fee("fee", "手续费"),
    /**
     * 滞纳金
     */
    offsetitem_penalty("penalty", "滞纳金"),
    /**
     * 利息
     */
    offsetitem_int("int", "利息"),
    /**
     * 本金
     */
    offsetitem_principal("principal", "本金"),


    ;

    private String code;
    private String desc;

    private OffsetItemEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OffsetItemEnums acquireOffsetItemByCode(String code) throws Exception{
        for( OffsetItemEnums ele : OffsetItemEnums.values() ){
            if( ele.code.equalsIgnoreCase(code) ){
                return ele;
            }
        }
        throw new Exception("Error code:" + code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

```

## Effective Java 中的场景实例: 枚举中的抽象方法

在 Effective Java 第二版中的第30条定律中，举例了一个场景，如实现四则运算。

```java
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;

    double apply(double x, double y) throws Exception{
        switch (this){
            case PLUS:
                return x + y;
            case MINUS:
                return x - y;
            case TIMES:
                return x * y;
            case DIVIDE:
                return x / y;
        }
        throw new Exception("Unknown op: " + this);
    }
}
```

这段代码可行, 但是需要 throw 一个Exception，如果没有抛出一个异常，就编译不过了，但是实际上是不可能执行到最后一行代码的。还存在一个问题是，如果新增了枚举常量，但是忘记给switch添加相应的条件，枚举仍然可以编译，但是运行时不会得到期望的结果。

我们发现一种更好的实现，是给枚举添加一个抽象的方法 apply:

```java
public enum OperationGraceful {
    PLUS, MINUS, TIMES, DIVIDE;

    abstract double apply(double x, double y);
}
```
此时变异错误：
<font color=red>Class 'OperationGraceful' must either be declared abstract or implement abstract method 'apply(double, double)' in 'OperationGraceful' </font>
意思是每一个枚举常量必须实现声明的抽象方法：

```java
public enum OperationGraceful {
    PLUS{
        @Override
        double apply(double x, double y) {
            return x + y;
        }
    }, MINUS {
        @Override
        double apply(double x, double y) {
            return x - y;
        }
    }, TIMES {
        @Override
        double apply(double x, double y) {
            return x * y;
        }
    }, DIVIDE {
        @Override
        double apply(double x, double y) {
            return x / y;
        }
    };

    abstract double apply(double x, double y);
}
```

如此一来，就不会在新增一个枚举值后，遗漏掉前面示例的switch分支逻辑，即使忘记了，编译器也会提醒，您需要实现抽象方法的规约。

特定于常量的方法实现可以与特定于常量的数据结合起来：
```java
public enum OperationGracefulField {
    PLUS("+"){
        @Override
        double apply(double x, double y) {
            return x + y;
        }
    }, MINUS("-") {
        @Override
        double apply(double x, double y) {
            return x - y;
        }
    }, TIMES("*") {
        @Override
        double apply(double x, double y) {
            return x * y;
        }
    }, DIVIDE("/") {
        @Override
        double apply(double x, double y) {
            return x / y;
        }
    };

    // 属性
    private String symbol;
    OperationGracefulField(String symbol){
        this.symbol = symbol;
    }

    abstract double apply(double x, double y);
}

```

## 计算加班工资的案例

在五个工作日中，正常超过8个小时，就会产生加班工资（当然现实其实是不可能的，万恶的资本主义丿_\）。周末全部算加班。加班费按基本工资的一半计算。
```java
public enum PayrollDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    private static final int HOUR_PER_SHIFT = 8;

    double pay(double hourseWorked, double payRate){
        double basePay = hourseWorked * payRate;

        double overtimePay;

        switch (this){
            case SATURDAY:
            case SUNDAY:
                overtimePay = hourseWorked * payRate / 2;
             default:
                 overtimePay = hourseWorked <= HOUR_PER_SHIFT
                         ? 0 : (hourseWorked - HOUR_PER_SHIFT) * payRate / 2;
                 break;
        }

        return  basePay + overtimePay;
    }
}
```

这个程序可以运行，达到基本的业务需求，但是从维护角度来看，比较危险，假设将一个元素添加到枚举中，或许是一个表示假期天数的特殊值，但是非常不幸，忘记了在switch分支添加代码区分，虽然程序可以编译，但是实际运行时可能会出现尴尬的结果，比如假期也计算了工资，带薪假期忘了计算工资等等。

## 策略枚举的计算加班工资实现

我们可以根据是否工作日、双休日，将加班工资计算移到一个私有的嵌套枚举中，然后将这个 **策略枚举** 的实例传递到 PayrollDay 枚举的构造器中。
之后 PayrollDay 的加班费计算规则委托给策略枚举， PayrollDay 就不需要switch 语句或者特定于常量的方法了。

```java
public enum PayrollDayStrategy {
    MONDAY(PayType.WEEKDAY),
    TUESDAY(PayType.WEEKDAY),
    WEDNESDAY(PayType.WEEKDAY),
    THURSDAY(PayType.WEEKDAY),
    FRIDAY(PayType.WEEKDAY),
    SATURDAY(PayType.WEEKEND),
    SUNDAY(PayType.WEEKEND);

    private final PayType payType;

    PayrollDayStrategy(PayType payType){
        this.payType = payType;
    }

    private enum PayType{
        WEEKDAY{
            @Override
            double overtimePay(double hrs, double payRate) {
                return hrs <= HOUR_PER_SHIFT ?
                        0 : (hrs - HOUR_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            @Override
            double overtimePay(double hrs, double payRate) {
                return hrs * payRate /2;
            }
        };

        private static final int HOUR_PER_SHIFT = 8;

        abstract double overtimePay(double hrs, double payRate);

        double pay( double hoursWork, double payRate ){
            double basePay = hoursWork * payRate;
            return basePay + overtimePay(hoursWork, payRate);
        }
    }
}
```

## 枚举的使用场景

- 需要一组固定常量的时候

  例如： 行星、一周的天数等等
  
- 如果多个枚举常量同时共享相同的行为， 则考使用虑策略枚举

## EnumSet 枚举集合

```EnumSet``` 类用来有效地表示从单个枚举类型中提取的多个值的多个集合，实现了 Set 集合，提供了丰富的功能和类型安全性。

```java
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
```

EnumSet 提供了很多静态方法用于创建集合。

### EnumSet.of(...)  源码

```java
public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
    EnumSet<E> result = noneOf(e1.getDeclaringClass());
    result.add(e1);
    result.add(e2);
    return result;
}
    
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
        throw new ClassCastException(elementType + " not an enum");

    if (universe.length <= 64)
        return new RegularEnumSet<>(elementType, universe);
    else
        return new JumboEnumSet<>(elementType, universe);
}


```

其中 ```RegularEnumSet```、```JumboEnumSet``` 是JDK自带的EnumSet的实现类。
如果底层的枚举类型有64个或者更少的元素————其实大都数如此，整个 EnumSet 就用单个long来表示，性能较好。




## EnumMap 枚举映射使用场景

自从我们知道 Enum 存在一个 ordinal 方法之后，可能对其使用就跃跃欲试了，比如，有一个用来表示一种烹饪的香草：

```java
public enum Herb {
    ,
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

}
```

**场景** ：
    现在假设有一个香草的数组，表示一座花园中的植物，但是想要按照类型进行组织后将这些植物列出来。
**分析**：
    我们发现，需要按类型分类，然后列出来。可以用作映射，刚好有一个 EnumMap 的类可以达到这样的目的。

```java
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
        // 这是一个花园，栽种有各种类型的香草
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
```


## 枚举实现接口

**枚举还可以实现接口**，用于实现扩展功能。

改装前面的四则运算案例：

```java

// 接口
package org.byron4j.cookbook.javacore.enums;

public interface OperationI {
    public double apply(double x, double y);
}



// 实现接口的枚举
package org.byron4j.cookbook.javacore.enums;

public enum BasicOperation implements OperationI{
    PLUS("+"){
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    }, MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    }, TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    }, DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    // 属性
    private String symbol;
    BasicOperation(String symbol){
        this.symbol = symbol;
    }


}

```


参考资料：

- Effective Java（第二版）