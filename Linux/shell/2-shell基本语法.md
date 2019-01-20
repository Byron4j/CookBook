## Shell 基本语法

### shell 变量

#### 定义变量name：

>name="Byron"

**注意：**

- 变量名和=号之间不能有空格

#### 使用变量

在变量名前面加上美元$符号，即可引用已经定义过的变量：

```shell
name="Byron"

# 引用变量

echo $name

echo ${name}
```

花括号是为了帮助解释器识别变量的边界，如：

```shell
name="Byron"

echo "My name is ${name}"
```

#### 重新赋值不能使用$符号

```shell
your_name="tom"
echo $your_name
your_name="alibaba"
echo $your_name

```

变量your_name被二次赋值，是可以的。
**注意：** 但是，第二次赋值的时候不能写 $your_name="alibaba"，因为只有在引用变量的时候采用$符号。

#### 只读变量 readonly

readonly 命令可以将变量定义为只读变量，只读变量的值不能被改变。

```shell
#!/bin/bash
name="Byron"
readonly name
name="Lucy"
```

运行结果为：
```shell
/tmp/839326580/main.sh: line 4: name: readonly variable

exit status 1
```

#### 删除变量 unset

使用 unset 命令可以删除变量：

>unset 变量名

变量被删除后不能再次使用。
unset 命令不能删除只读变量。

```shell
#!/bin/bash
name="Byron"
echo $name
unset name
echo $name
```

只会输出第一次的Byron。第二次不会有任何输出。

### 变量类型

运行shell时，会同时存在三种类型：

- 局部变量：在脚本或命令中定义，仅在当前shell实例中有效，其他shell启动的程序不能访问局部变量。
- 环境变量： 所有的程序包括shell启动程序，都能访问环境变量。
- shell变量： shell变量是由shell程序设置的特殊变量。shell变量中有一部分是环境变量一部分是局部变量，这些保证了shell程序的正常执行。

















