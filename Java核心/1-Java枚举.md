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

