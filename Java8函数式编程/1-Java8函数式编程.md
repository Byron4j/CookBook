# Java 8 函数式编程

## 1. Lambda 表达式

### 1.1 基本语法

```java
// 传统匿名内部类
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello");
    }
};

// Lambda 表达式
Runnable runnable = () -> System.out.println("Hello");

// 带参数
Comparator<String> comparator = (String s1, String s2) -> {
    return s1.length() - s2.length();
};

// 简写形式
Comparator<String> comparator = (s1, s2) -> s1.length() - s2.length();

// 单参数可省略括号
Consumer<String> consumer = s -> System.out.println(s);

// 方法引用（更简洁）
Consumer<String> consumer = System.out::println;
```

### 1.2 函数式接口

```java
// Java 8 内置函数式接口

// Consumer<T>: 接受T，无返回值
Consumer<String> print = s -> System.out.println(s);
print.accept("Hello");

// Supplier<T>: 无参数，返回T
Supplier<LocalDateTime> now = LocalDateTime::now;
LocalDateTime dateTime = now.get();

// Function<T, R>: 接受T，返回R
Function<String, Integer> length = String::length;
Integer len = length.apply("Hello");

// Predicate<T>: 接受T，返回boolean
Predicate<String> isEmpty = String::isEmpty;
boolean result = isEmpty.test("");

// BiFunction<T, U, R>: 接受T和U，返回R
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

// 自定义函数式接口
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
    
    // 可以有默认方法
    default void print(int result) {
        System.out.println("Result: " + result);
    }
}
```

## 2. Stream API

### 2.1 创建 Stream

```java
// 从集合创建
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();
Stream<String> parallelStream = list.parallelStream();

// 从数组创建
Stream<String> stream = Arrays.stream(new String[]{"a", "b", "c"});

// 从值创建
Stream<String> stream = Stream.of("a", "b", "c");

// 无限流
Stream<Integer> infinite = Stream.iterate(0, n -> n + 2); // 0, 2, 4, 6...
Stream<Double> random = Stream.generate(Math::random);

// 从文件创建
Stream<String> lines = Files.lines(Paths.get("file.txt"));
```

### 2.2 中间操作

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// filter: 过滤
List<Integer> even = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());  // [2, 4, 6, 8, 10]

// map: 映射
List<Integer> squared = numbers.stream()
    .map(n -> n * n)
    .collect(Collectors.toList());  // [1, 4, 9, 16, 25...]

// flatMap: 扁平化
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);
List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());  // [1, 2, 3, 4, 5, 6]

// distinct: 去重
List<Integer> distinct = Arrays.asList(1, 2, 2, 3, 3, 3).stream()
    .distinct()
    .collect(Collectors.toList());  // [1, 2, 3]

// sorted: 排序
List<Integer> sorted = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// peek: 观察（调试）
List<Integer> result = numbers.stream()
    .peek(n -> System.out.println("Processing: " + n))
    .map(n -> n * 2)
    .collect(Collectors.toList());

// limit / skip: 分页
List<Integer> page = numbers.stream()
    .skip(5)      // 跳过前5个
    .limit(3)     // 取3个
    .collect(Collectors.toList());  // [6, 7, 8]
```

### 2.3 终止操作

```java
// collect: 收集
List<String> list = stream.collect(Collectors.toList());
Set<String> set = stream.collect(Collectors.toSet());
Map<String, Integer> map = stream.collect(
    Collectors.toMap(s -> s, String::length)
);

// joining: 连接字符串
String joined = Stream.of("a", "b", "c")
    .collect(Collectors.joining(", ", "[", "]"));  // [a, b, c]

// groupingBy: 分组
Map<Integer, List<String>> grouped = Stream.of("a", "bb", "cc", "ddd")
    .collect(Collectors.groupingBy(String::length));
// {1=[a], 2=[bb, cc], 3=[ddd]}

// partitioningBy: 分区
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n > 5));
// {false=[1, 2, 3, 4, 5], true=[6, 7, 8, 9, 10]}

// reduce: 归约
Integer sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);  // 55

Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);

// forEach: 遍历
numbers.stream().forEach(System.out::println);

// count: 计数
long count = numbers.stream().filter(n -> n > 5).count();

// anyMatch / allMatch / noneMatch
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
boolean allPositive = numbers.stream().allMatch(n -> n > 0);

// findFirst / findAny
Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst();
Optional<Integer> any = numbers.stream().filter(n -> n > 5).findAny();

// min / max
Optional<Integer> min = numbers.stream().min(Integer::compareTo);
```

## 3. Optional

### 3.1 基本用法

```java
// 创建 Optional
Optional<String> optional = Optional.of("Hello");        // 非空
Optional<String> empty = Optional.empty();               // 空
Optional<String> nullable = Optional.ofNullable(null);  // 可为空

// 判断和取值
if (optional.isPresent()) {
    String value = optional.get();
}

// 推荐用法
optional.ifPresent(System.out::println);

// orElse / orElseGet
String result = optional.orElse("Default");
String result = optional.orElseGet(() -> expensiveOperation());

// orElseThrow
String result = optional.orElseThrow(() -> new RuntimeException("Not found"));

// map / flatMap / filter
Optional<Integer> length = optional.map(String::length);
Optional<String> filtered = optional.filter(s -> s.length() > 3);
```

### 3.2 实战场景

```java
// 避免空指针的传统写法
public String getCity(User user) {
    if (user != null) {
        Address address = user.getAddress();
        if (address != null) {
            City city = address.getCity();
            if (city != null) {
                return city.getName();
            }
        }
    }
    return "Unknown";
}

// Optional 链式调用
public String getCity(User user) {
    return Optional.ofNullable(user)
        .map(User::getAddress)
        .map(Address::getCity)
        .map(City::getName)
        .orElse("Unknown");
}
```

## 4. 新的日期时间 API

```java
// LocalDate: 日期
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(1990, 5, 20);
Period period = Period.between(birthday, today);

// LocalTime: 时间
LocalTime now = LocalTime.now();
LocalTime meeting = LocalTime.of(14, 30);

// LocalDateTime: 日期时间
LocalDateTime dateTime = LocalDateTime.now();

// ZonedDateTime: 带时区
ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

// Instant: 时间戳
Instant instant = Instant.now();
long epochMilli = instant.toEpochMilli();

// Duration / Period
Duration duration = Duration.between(start, end);
Period period = Period.between(startDate, endDate);

// DateTimeFormatter
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formatted = dateTime.format(formatter);
LocalDateTime parsed = LocalDateTime.parse("2024-01-01 12:00:00", formatter);

// 时区转换
ZonedDateTime shanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
ZonedDateTime newYork = shanghai.withZoneSameInstant(ZoneId.of("America/New_York"));
```

## 5. 接口默认方法和静态方法

```java
public interface Animal {
    void eat();  // 抽象方法
    
    default void sleep() {  // 默认方法
        System.out.println("Sleeping...");
    }
    
    static void breathe() {  // 静态方法
        System.out.println("Breathing...");
    }
}

// 实现类只需实现抽象方法
public class Dog implements Animal {
    @Override
    public void eat() {
        System.out.println("Dog eating...");
    }
}

Dog dog = new Dog();
dog.eat();      // Dog eating...
dog.sleep();    // Sleeping...（使用默认实现）
Animal.breathe(); // Breathing...（静态方法）
```

## 6. 方法引用

```java
// 1. 静态方法引用
Function<Integer, String> toString = String::valueOf;

// 2. 实例方法引用（特定对象）
String prefix = "Hello ";
Function<String, String> addPrefix = prefix::concat;

// 3. 实例方法引用（任意对象）
Function<String, Integer> length = String::length;

// 4. 构造方法引用
Supplier<List<String>> listSupplier = ArrayList::new;
Function<Integer, Integer> intFactory = Integer::new;  // 已过时，示例

// 5. 数组构造引用
Function<Integer, int[]> arrayFactory = int[]::new;
```

## 7. 并行流与性能陷阱

```java
// 并行流
List<Integer> numbers = IntStream.range(1, 1_000_000)
    .boxed()
    .parallel()  // 转为并行流
    .map(n -> n * n)
    .collect(Collectors.toList());

// 适用场景：
// ✓ 数据量大（> 10,000）
// ✓ 无状态、无副作用
// ✓ 计算密集型操作

// 不适用场景：
// ✗ 数据量小（线程切换开销 > 并行收益）
// ✗ I/O 密集型（阻塞 ForkJoinPool）
// ✗ 需要严格顺序保证
// ✗ 涉及共享可变状态

// 性能陷阱示例
List<Integer> result = new ArrayList<>();
numbers.parallelStream().forEach(result::add);  // 线程不安全，结果混乱

// 正确做法
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());  // 合并结果
```

## 8. 最佳实践

1. **优先使用标准函数式接口**，避免自定义
2. **Stream 链式操作保持简洁**，超过 3 个操作考虑拆分
3. **Optional 不要滥用**，主要用于返回值
4. **避免在 Stream 中修改外部变量**
5. **日期时间API统一使用 java.time**，废弃 Date/Calendar
6. **方法引用比 Lambda 更简洁**，可读性更好时使用
7. **并行流谨慎使用**，默认串行流