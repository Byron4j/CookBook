# Java单元测试与TDD实践

## 1. 概述

单元测试是保证代码质量的第一道防线。好的单元测试应该：快速、独立、可重复、自验证、及时。

```
测试金字塔:

        /
       / \      E2E 测试（端到端）
      /   \     少量，覆盖主路径
     /_____\
    /       \   集成测试
   /         \  测试组件交互
  /___________\
 /             \ 单元测试
/               \ 大量，覆盖各种场景
─────────────────

比例建议:
- 单元测试: 70%
- 集成测试: 20%
- E2E 测试: 10%
```

## 2. JUnit 5（Jupiter）

### 2.1 与 JUnit 4 对比

| 特性 | JUnit 4 | JUnit 5 |
|------|---------|---------|
| 注解 | @Test, @Before, @After | @Test, @BeforeEach, @AfterEach |
| 扩展机制 | Runner（有限） | Extension API（强大） |
| 参数化测试 | @RunWith(Parameterized.class) | @ParameterizedTest |
| 动态测试 | 不支持 | @TestFactory |
| 嵌套测试 | 不支持 | @Nested |
| 条件执行 | 有限 | @EnabledIf, @DisabledIf |

### 2.2 核心注解

```java
// 基本测试
@Test
void shouldCalculateSum() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 3));
}

// 生命周期
@BeforeAll    // 类级别，所有测试前执行一次（需 static）
@BeforeEach   // 方法级别，每个测试前执行
@AfterEach    // 方法级别，每个测试后执行
@AfterAll     // 类级别，所有测试后执行一次（需 static）

// 禁用与超时
@Disabled("暂跳过此测试")
@Timeout(value = 2, unit = TimeUnit.SECONDS)  // 超时 2 秒
@Test
void slowTest() { }

// 重复测试
@RepeatedTest(5)
void repeatedTest() { }

// 条件执行
@EnabledOnOs(OS.LINUX)
@EnabledIfSystemProperty(named = "env", matches = "dev")
@Test
void linuxOnlyTest() { }
```

### 2.3 参数化测试

```java
// 1. @ValueSource
@ParameterizedTest
@ValueSource(strings = {"racecar", "radar", "level"})
void isPalindrome(String candidate) {
    assertTrue(StringUtils.isPalindrome(candidate));
}

// 2. @CsvSource
@ParameterizedTest
@CsvSource({
    "1, 1, 2",    // a, b, expected
    "2, 3, 5",
    "10, 20, 30"
})
void add(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

// 3. @MethodSource
@ParameterizedTest
@MethodSource("provideStringsForIsBlank")
void isBlank(String input, boolean expected) {
    assertEquals(expected, Strings.isBlank(input));
}

static Stream<Arguments> provideStringsForIsBlank() {
    return Stream.of(
        Arguments.of(null, true),
        Arguments.of("", true),
        Arguments.of("  ", true),
        Arguments.of("not blank", false)
    );
}
```

### 2.4 断言

```java
// 基本断言
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);

// 异常断言
Exception exception = assertThrows(IllegalArgumentException.class, () -> {
    calculator.divide(1, 0);
});
assertEquals("除数不能为0", exception.getMessage());

// 组合断言（全部执行，失败一起报告）
assertAll("用户属性验证",
    () -> assertEquals("张三", user.getName()),
    () -> assertEquals(18, user.getAge()),
    () -> assertNotNull(user.getEmail())
);
```

## 3. Mockito 模拟

### 3.1 核心方法

```java
// 创建模拟对象
List<String> mockedList = mock(List.class);

// 打桩（定义行为）
when(mockedList.get(0)).thenReturn("first");
when(mockedList.get(1)).thenThrow(new RuntimeException());

// 验证交互
mockedList.add("one");
verify(mockedList).add("one");           // 验证调用过
verify(mockedList, never()).add("two");  // 验证未调用
verify(mockedList, times(2)).add(any()); // 验证调用 2 次

// 参数匹配器
when(mockedList.get(anyInt())).thenReturn("element");
when(mockedList.contains(argThat(s -> s.length() > 5))).thenReturn(true);
```

### 3.2 Spring Boot Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUser() throws Exception {
        // Given
        when(userService.findById(1L))
            .thenReturn(new User(1L, "张三"));
        
        // When & Then
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("张三"));
    }
    
    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"张三\",\"age\":18}"))
            .andExpect(status().isCreated());
    }
}
```

### 3.3 切片测试

```java
// 只测试 Web 层
@WebMvcTest(UserController.class)
class UserControllerSliceTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
}

// 只测试 Data 层
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository repository;
    
    @Test
    void shouldFindUserByName() {
        User user = new User("张三");
        entityManager.persist(user);
        
        List<User> found = repository.findByName("张三");
        assertEquals(1, found.size());
    }
}
```

## 4. TDD 测试驱动开发

### 4.1 TDD 循环

```
TDD 三步循环:

1. 红（Red）
   编写一个失败的测试
   测试定义了期望的行为
   
2. 绿（Green）
   编写最少代码让测试通过
   不追求完美，先让测试变绿
   
3. 重构（Refactor）
   在测试保护下优化代码
   消除重复，提高可读性

示例：实现 FizzBuzz

Red:
@Test
void shouldReturnFizzForMultipleOf3() {
    assertEquals("Fizz", fizzBuzz.convert(3));
}
// 编译失败：方法不存在

Green:
String convert(int number) {
    return "Fizz";  // 最简单实现
}

Refactor:
String convert(int number) {
    if (number % 3 == 0) return "Fizz";
    return String.valueOf(number);
}
```

### 4.2 TDD 优势

- **设计更好**：测试迫使代码可测试，自然解耦
- **文档即代码**：测试是活的文档，展示用法
- **安全重构**：有测试保护，大胆重构
- **减少 Bug**：边界条件在编码时就考虑

## 5. 测试最佳实践

### 5.1 FIRST 原则

```
F - Fast（快速）
   单元测试应该在毫秒级完成
   
I - Independent（独立）
   测试之间不应有依赖
   
R - Repeatable（可重复）
   在任何环境、任何时间运行结果一致
   
S - Self-validating（自验证）
   测试应该返回布尔结果，无需人工检查日志
   
T - Timely（及时）
   在写生产代码之前或同时写测试
```

### 5.2 命名规范

```java
// 好的命名：描述行为和期望
@Test
void shouldDecreaseBalanceWhenWithdrawSufficientFunds() { }

@Test
void shouldThrowExceptionWhenWithdrawInsufficientFunds() { }

// 差的命名：只是方法名
@Test
void testWithdraw() { }

// Given-When-Then 结构
@Test
void givenSufficientFunds_whenWithdraw_thenBalanceDecreases() { }
```

### 5.3 测试数据构建

```java
// Builder 模式构建测试数据
User user = User.builder()
    .id(1L)
    .name("张三")
    .age(18)
    .email("zhangsan@example.com")
    .build();

// 使用 Object Mother 模式
public class TestUsers {
    public static User aUser() {
        return new User(1L, "张三", 18);
    }
    
    public static User anAdmin() {
        return new User(2L, "管理员", 30, Role.ADMIN);
    }
}

// 使用
User user = TestUsers.aUser();
```

## 6. 代码覆盖率

```
JaCoCo 配置:

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>

覆盖率指标:
- 指令覆盖（Instruction）: 字节码指令覆盖
- 分支覆盖（Branch）: if/else、switch 分支覆盖
- 行覆盖（Line）: 源代码行覆盖
- 方法覆盖（Method）: 方法调用覆盖
- 类覆盖（Class）: 类加载覆盖

建议目标:
- 行覆盖率 ≥ 80%
- 分支覆盖率 ≥ 70%
```

## 7. 测试反模式

```
1. 虚假测试（Happy Path Only）
   只测试正常路径，不测异常情况
   
2. 测试与实现耦合
   测试过度依赖内部实现细节
   重构时测试频繁失败
   
3. 测试间依赖
   测试 A 的执行影响测试 B
   导致 flaky tests（不稳定测试）
   
4. 过度使用 Mock
   Mock 了所有依赖，测了个寂寞
   应该测试真实行为
   
5. 忽视测试质量
   测试代码也是代码，也需要维护
   糟糕的测试代码会降低整体质量
```

## 8. 总结

```
优秀单元测试 checklist:

□ 测试名清晰描述行为
□ 遵循 Arrange-Act-Assert 结构
□ 一个测试只验证一个概念
□ 使用有意义的断言消息
□ 不依赖外部资源（数据库、网络）
□ 执行速度快（< 100ms）
□ 独立运行，不依赖其他测试
□ 可读性强，像文档一样清晰
```