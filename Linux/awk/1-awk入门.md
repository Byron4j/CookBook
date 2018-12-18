
[AWK入门指南](https://awk.readthedocs.io/en/latest/chapter-one.html)

### AWK 起步示例

假设存在一个文件 emp.data，其中包含员工的姓名、薪资（美元/小时）以及小时数，一个员工一行数据,其内容如下:

```shell
Beth	4.00	0
Dan	3.75	0
kathy	4.00	10
Mark	5.00	20
Mary	5.50	22
Susie	4.25	18
```

如果想打印出 工作时长超过0小时的员工姓名和工资(薪资乘以时间)，以下命令可以完成: 

```shell
awk '$3>0 {print $1, $2*$3}'  emp.data

```

得到如下输出：

```shell
kathy 40
Mark 100
Mary 121
Susie 76.5
```

**<font color=red>该命令告诉系统执行括号内的awk程序，从输入文件 emp.data 获取所需要的数据。引号内的部分是个完整的awk程序，包含单个 模式-动作 语句。模式 $3>0 用于匹配第三列大于0的输入行，动作： </font>**
```{print $1, $2*$3}```
打印每个匹配行的第一个字段、第二个字段与第三个字段的乘积。

还可以打印没有工作过的员工姓名：
```shell
awk '$3==0 {print $1}'  emp.data
```
将会输出：
```shell
Beth
Dan
```

### AWK程序的结构

回过头来看一下上述命令。**引号之间的部分是awk编程语言写就的程序**。 每个awk程序都是 **一个或多个 模式-动作** 语句的序列：

**pattern {pattern}**
**pattern {pattern}**
**...**

awk 的基本操作是一行一行的扫描输入，搜索匹配任意程序中模式的行。 词语“匹配”的准确意义是视具体的模式而言，对于模式 $3>0 来说，意思是“条件为真”。
每个模式依次测试每个输入行。对于匹配到行的模式，其对应的动作（也许包含多步）得到执行，然后读取下一行并继续匹配，直到所有的输入读取完毕。

| 模式        | 动作           | 
|: ------------- :|:-------------:| 
|  $3==0    |  {print $1} |

模式-动作 语句中的 模式或动作（但不是两者同时省略）都可以省略。如果某个模式没有动作，例如：

```$3==0```

那么模式匹配到的每一行都会被打出来。输出如下：
```
Beth    4.00    0
Dan     3.75    0
```

如果是没有动作的模式，例如：
```{print $1}```
则会打印第一列，输出如下：
```
Beth
Dan
kathy
Mark
Mary
Susie
```

**<font color=red>由于模式和动作两者任一都是可选的，所以需要使用大括号包围动作用以区分其他模式。</font>**

### 执行 awk 程序

执行awk程序有多种，可以输入如下形式的命令行：
```awk 'program codes' inputfiles```
从而在每个指定的输入文件上执行这个program。例如：
```awk '$3==0 {print $1}' file1 file2```
打印 file1 和 file2 文件中第三列为0的每一行的第一个字段。

也可以省略命令行中的输入文件，仅仅输入：
```awk 'program codes' ```
在这种情况下，awk 将会应用于你在终端接着输入的任意数据行，直到你输入一个文件结束信号（Unix系统上为control-d）。示例：
```
awk '$3>0 {print $1}'
Mary 20 1000   #输入该行回车
Mary  # 计算机输出，匹配到了信息
Belly 30 3000  #继续输入改行
Belly #计算机输出
```

**注意事项：** 命令行中的程序是用单引号包围着的。这会防止shell解释程序中$这样的字符，也允许程序的长度超过一行。
**当程序比较长的时候，可以将程序写入到一个文件**，以下命令行：
```awk -f programfile optional list of input files```

其中 [-f](#) 选项指示 awk 从指定文件中获取程序。可以使用任意文件名替换 programfile。

#### awk 的错误提示

如果你的 awk 程序存在错误， awk 会给你一些诊断信息。例如，如果你打错了大括号，如下所示：

```awk '$3==0 [print $1}' emp.data```
会提示如下错误：
```
awk: $3==0 [print $1}
awk:       ^ syntax error
awk: $3==0 [print $1}
awk:                ^ syntax error
```

### 简单输出

<font color=red> awk中仅仅只有两种类型  **数值**、 **字符** 构成的字符串</font>。通常情况下，一个字段是一个不包含任何空格或制表符的连续字符序列。
当前输入的 行中的第一个字段被称作 $1，第二个是 $2，以此类推。 整个行的内容被定义为 $0。 每一行的字段数量可以不同。

大都数情况下，我们仅仅只是打印出其中每一行的某些字段，或者也还需要做一些计算。

#### 打印每一行

如果一个动作没有任何模式，这个动作针对所有输入的行进行操作。 **<font color=red>print </font>** 语句用来打印（输出）当前输入的行。
所以 ```{print}``` 等效于 ```{print $0}```

#### 打印特定行

```{print $1,$3}``` 将输出：

```
Beth 0
Dan 0
kathy 10
Mark 20
Mary 22
Susie 18
```

**在 print 语句中被逗号分隔的表达式，在默认情况下他们将会用一个空格分割来输出。** 每一行print生成的内容都会以一个换行符作为结束。但这些默认行为都可以自定义。

#### NF，字段数量

AWK 会对当前输入的行有多少字段进行计数，并且将当前行的字段数量存储在一个内建的称为 NF 的变量中。因此
```{print NF,$1,$NF}``` 会打印出 每一行的字段数量、第一个字段的值、最后一个字段的值。
输出：
```
3 Beth 0
3 Dan 0
3 kathy 10
3 Mark 20
3 Mary 22
3 Susie 18
```

#### 打印行号

awk 提供了另一个内建变量， NR。他存储了当前已经读取了多少行的计数。可以使用 NR和$0给emp.data的每一行加上行号：
```{print NR,$0}```
输出如下：
```
1 Beth  4.00    0
2 Dan   3.75    0
3 kathy 4.00    10
4 Mark  5.00    20
5 Mary  5.50    22
6 Susie 4.25    18
```

#### 在输出中添加内容

还可以在字段中间或者计算的值中间打印输出想要的内容：
```{print "total pay for", $1, "is", $2*$3}```
输出如下：
```
total pay for Beth is 0
total pay for Dan is 0
total pay for kathy is 40
total pay for Mark is 100
total pay for Mary is 121
total pay for Susie is 76.5
```

### 高级输出

print 语句可用于快速而简单的输出。若要严格按照你所想的格式化输出，则需要使用 **printf** 语句。

#### 字段排队
printf 语句格式如下：

><font color=green>printf(format, value1, value2, ..., valueN)</font>

其中 format 是字符串，包含要逐字打印的文本，穿插在 format 之后的每个值该如何打印的规格。一个规格是一个 % 符，后面跟着一些字符，用来控制一个 value 的格式。因此，有过少个 value 要打印，在 fromat 中就要有多少个 % 规格。
打印每个员工的总薪酬：
```{printf("total pay for %s is $%.2f\n", $1, $2*$3)}```
输出如下：
```shell
awk '{printf("total pay for %s is $%.2f\n", $1, $2*$3)}'  emp.data
total pay for Beth is $0.00
total pay for Dan is $0.00
total pay for kathy is $40.00
total pay for Mark is $100.00
total pay for Mary is $121.00
total pay for Susie is $76.50
```

#### 排序输出

以薪酬递增的方式输出每一行：

```awk '{printf("%6.2f  %s\n", $2*$3, $0)}'  emp.data | sort```

将awk的输出通过管道传给 **<font color=red>sort </font>**命令，输出如下：

```
  0.00  Beth    4.00    0
  0.00  Dan     3.75    0
100.00  Mark    5.00    20
121.00  Mary    5.50    22
 40.00  kathy   4.00    10
 76.50  Susie   4.25    18
```

#### 选择

awk 的模式适用于为进一步的处理从输入中选择相关的数据行。由于不带动作的模式会打印所有匹配的行，所以很多awk程序仅仅包含一个模式。本节将给出一些有用的模式示例。

#####  通过对比选择
使用一个对比模式来选择每小时赚5美元或更多的员工记录，亦即第二个字段大于等于5的行：
```$2>=5```

```shell
awk '$2>=5'  emp.data
Mark    5.00    20
Mary    5.50    22
```

#### 通过计算选择

```shell
awk '$2*$3>50 {printf("$%.2f for %s\n", $2*$3, $1)}'  emp.data
$100.00 for Mark
$121.00 for Mary
$76.50 for Susie
```

#### 通过文本内容选择

除了数值测试，还可以选择包含特定单词或短语的输入行。这个程序会打印所有第一个字段为 Susie 的行：

```$1=="Susie"```

操作符 == 用于测试相等性。 也可以使用正则表达式的模式查找包含任意任意字母组合，单词或短语的文本。如以下可以匹配到任意位置包含Susie的行：
```/Susie/```

```shell
awk '/Susie/'  emp.data
Susie   4.25    18
```

#### 模式组合

可以使用括号和逻辑操作符号与&&、或||，以及 非! 对模式进行组合。
```$2>=4||$3>=20```
会打印第二个字段大于等于4或者第三个字段大于等于20的行：
```shell
awk '$2>=4||$3>=20'  emp.data
Beth    4.00    0
kathy   4.00    10
Mark    5.00    20
Mary    5.50    22
Susie   4.25    18
```

#### BEGIN 与 END

特殊模式 BEGIN 用于匹配第一个输入文件的第一行之前的位置。END 则用于匹配处理过的最后一个文件的最后一行的位置。

这个程序使用 BEGIN 来输出一个标题：
```
BEGIN {print "Name RATE HOURS"; print ""}
    {print}
```

```shell
awk 'BEGIN {print "Name RATE HOURS"; print ""}
{print}' emp.data

## 输出如下：

Name RATE HOURS

Beth    4.00    0
Dan     3.75    0
kathy   4.00    10
Mark    5.00    20
Mary    5.50    22
Susie   4.25    18
```

**注意事项：** 

- >awk 可以在一行上放多个语句，步过要使用分号;进行分隔。

- >普通的 print 是打印当前输入行， print "" 则会打印一个空行。

#### 使用 AWK 进行计算

一个动作就是一个以新行或者分号分隔的语句序列。

##### 计数

```
$3 > 15 {emp = emp + 1}
END {print emp, "employees worked more than 15 hours"}
```

```shell
awk '$3 > 15 {emp = emp + 1}
> END {print emp, "employees worked more than 15 hours"}' emp.data

## 输出结果：
3 employees worked more than 15 hours
```

用作数字的 awk 变量的默认初始值为0， 所以不需要初始化 emp。创建一个变量emp初始值为0，如果读入的那一行的第三个字段大于15，则emp在自身值的基础上自增1，读完最后一行后输出存在多少个员工工作时长超过15个小时的语句。

##### 求和与平均值

为计算员工数目，可以使用内置变量 NR，保存了当前位置读取的行数；在所有输入的结尾它的值就是所读行的总行数。

```END {print NR, "employees"}```

```shell
awk 'END {print NR, "employees"}'  emp.data

## 输出结果为：
6 employees
```

如下是一个使用 NR 来计算薪酬均值的程序：

```shell
awk '{pay = pay + $2*$3}
> END {print NR, "employees"
> print "total pay is", pay
> print "average pay is", pay/NR
> }' emp.data

## 输出结果为：
6 employees
total pay is 337.5
average pay is 56.25
```

#### 处理文本

awk 的优势之一是能像大多数语言处理数字一样方便地处理字符串。 awk 可以保存数字也可以保存字符。找出时薪最高的员工:

```
$2 > maxrate { maxrate = $2; maxemp = $1 }
END { print "highest hourly rate:", maxrate, "for", maxemp }
```

```shell
 awk '$2 > maxrate { maxrate = $2; maxemp = $1 }
> END { print "highest hourly rate:", maxrate, "for", maxemp }' emp.data

## 输出结果为：
highest hourly rate: 5.50 for Mary
```

```shell
awk '{names = names $1 " "}                    
END {print names}' emp.data

## 输出结果：
Beth Dan kathy Mark Mary Susie 
```

#### 打印最后一个输入行

虽然在 END 动作中 NR 还保留着它的值， 但 $0 没有。

```
{last = $0}
END {print last}
```

```shell
awk '
> {last = $0}
> END {print last}' emp.data

## 输出结果：
Susie   4.25    18
```


### AWK 内置函数

前面已经看到 awk 内置变量用来保存某些频繁使用的数量， NF 表示所在行的总列数， NR 表示当前是第多少行...
还有内置函数用来计算其他有用的数值。除了 平方根、对数、随机数此类的算术函数外，还有操作文本的函数。其中之一是 length 用于计算一个字符串的长度。

```shell
awk '{print $1, length($1)}'  emp.data

## 输出结果：
Beth 4
Dan 3
kathy 5
Mark 4
Mary 4
Susie 5
```

#### 行、单词以及字符的计数

使用 length、NF、NR来统计输入中行、单词以及字符的数量。为了简便，将每个字段看作一个单词。

```shell
awk ' { nc = nc + length($0) + 1
>       nw = nw + NF
>     }
> END { print NR, "lines,", nw, "words,", nc, "characters" }' emp.data

## 输出结果为：
6 lines, 18 words, 82 characters
```

因为 $0 不会包含行末的换行符，所以另外加了个1。

#### AWK 控制语句

awk 为选择提供了一个 if-else 语句， 以及为循环提供了几个语句，它们仅在动作中使用。

##### if-else 语句

如下是一个计算时薪超过6美元的员工总薪酬与平均薪酬。它使用一个 if 来防范零除问题。

```shell
$2 > 6 { n = n+1; pay = pay + $2*$3 }
END {
    if(n > 0)
        print n, "employees, total pay is", pay,
               "average pay is", pay/n
    else
        print "no employees are paid more than $6/hour"
        
}

```

```shell
awk '$2 > 6 { n = n+1; pay = pay + $2*$3 }
> END {
>     if(n > 0)
>         print n, "employees, total pay is", pay,
>                "average pay is", pay/n
>     else
>         print "no employees are paid more than $6/hour"
>         
> }' emp.data

## 输出结果为：
no employees are paid more than $6/hour
```

**注意事项：** 我们可以使用一个逗号将一个长语句截断为多行来书写.

##### while 语句

```
{
    i = 1
    while ( i <=3 ){
        # while 循环体(这一行是注释内容)
        printf("\t%.2f\n", $1*(1+$2)^i)
        i = i + 1
    }
}
```

while 后面是圆括号，里面是布尔表达式。 循环体是条件后大括号包围的语句。 ^ 是指数操作符。 # 后面是注释。

演示1000美元，利率为6%与12%，5年的复利分别是如何增长的：

```
awk '
> {
>     i = 1
>     while ( i <=5 ){
>         # while 循环体(这一行是注释内容)
>         printf("\t%.2f\n", $1*(1+$2)^i)
>         i = i + 1
>     }
> }
> ' 
1000 .06 5
        1060.00
        1123.60
        1191.02
        1262.48
        1338.23
1000 .12 5
        1120.00
        1254.40
        1404.93
        1573.52
        1762.34
```

##### for 语句

使用for循环实现上述例子：

```shell
awk '
> {
>     for( i = 1; i <= $3; i = i+1 ){
>         printf("\t%.2f\n", $1*(1+$2)^i)
>     }
> }
> '
1000 .06 5
        1060.00
        1123.60
        1191.02
        1262.48
        1338.23
1000 .12 5
        1120.00
        1254.40
        1404.93
        1573.52
        1762.34
```

#### 数组

awk 为存储一组相关的值提供了数组，虽然数组给予了awk很强的能力，但是在这里我们仅仅展示一个简单的例子。
第一个动作将输入行存为数组 line 的连续元素；
**第一行放在line[1]，第二行放在line[2]**。 END 动作使用一个while语句从后往前打印数组中的输入行：

```awk
# 反转-按行逆序打印输入

{line[NR] = $0}

END {
        i = NR
        while(i > 0){
            print line[i]
            i = i-1
        }
}

```

```awk
awk '
> {line[NR] = $0}
> 
> END {
>         i = NR
>         while(i > 0){
>             print line[i]
>             i = i-1
>         }
> }' emp.data

# 输出结果为：
Susie   4.25    18
Mary    5.50    22
Mark    5.00    20
kathy   4.00    10
Dan     3.75    0
Beth    4.00    0
```
