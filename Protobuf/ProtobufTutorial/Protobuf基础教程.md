## protobuf 基础教程

示例 **.proto** 文件见 [addressbook.proto](Protobuf/ProtobufTutorial/addressbook.proto)。

```proto
syntax = "proto2";

package tutorial;

option java_package = "com.example.tutorial";
option java_outer_classname = "AddressBookProtos";

message Person {
    required string name = 1;
    required int32 id = 2;
    optional string email = 3;

    enum PhoneType {
        MOBILE = 0;
        HOME = 1;
        WORK = 2;
    }

    message PhoneNumber {
        required string number = 1;
        optional PhoneType type = 2 [default = HOME];
    }

    repeated PhoneNumber phones = 4;
}

message AddressBook {
    repeated Person people = 1;
}
```

**.proto** 文件开始声明包，以免出现命名冲突。
在 JAVA 中，该包名可以作为java中的package，除非您又专门指定了 **java_package**，在[addressbook.proto](Protobuf/ProtobufTutorial/addressbook.proto) 中我们就指定了package。

即使您指定了一个**java_package**，也应该需要定义一个常规的**package**，是为了在Protocol Buffers命名空间中产生冲突。

在包定义声明之后，还看到了两个java规范中的可选项： **java_package**、**java_outer_classname**。
**java_package** 指定了生成的java类需要放在哪个包下面，如果没有指定这个值，则会使用**package**指定的值。
**java_outer_classname** 选项定义了类名，包含这个.proto文件里面的所有类，如果没有明确指定这个值的话，将会把文件名通过驼峰大小写命名方式作为类名，
例如 my_proto.proto 则默认会生成 MyProto 类名。

接下来，就有了消息(message)定义。

一个消息(message)是包含一系列类型字段的聚合。

很多标准的简单数据类型供字段可用，包含：

- bool
- int32
- float
- double
- string

您还可以添加其他的结构类型为您的消息字段类型所用。前面的示例中，**Person** 消息包含了**PhoneNumber**消息，而**AddressBook**消息包含**Person**消息。
您还可以定义枚举类型**enum**，如果你想你的字段可能的值为一个事先预定好的列表中的值，就可以使用**enum**类型。
这个电话簿示例中，phone number有三种类型： **MOBILE**、**HOME**、**WORK**。

**=1**、**=2** 表示每个元素上的标识字段在二进制编码中使用的唯一“标记”。
Tag编号 1-15 编号所需的字节比编号高的要少，所以您可以对常用的或经常重复使用的标记使用这些编号，剩下的16到更高的标记在可选元素中比较少用。
重复字段中的每个元素都需要重新编码标记号tag，所以重复的字段是这种优化的最佳选择。

每个字段必须标注为以下几种修饰符：

- **required** ：该字段必须提供，否则认为消息是"未初始化"的。尝试去构建一个未初始化的消息会抛出一个**RuntimeException**。解析一个未初始化的消息则会抛出一个**IOException**。除此之外，required字段的行为和optional字段完全一样。

- **optional** ：表示可选的字段，该字段可以设值亦可以不设值。如果一个可选字段没有set值，则会使用其默认值进行初始化。对于简单类型，您可以明确指定自己默认的值，就像我们在示例中处理的一样（phoneNumber的type字段）。否则，系统默认值：数值类型默认为0，字符类型默认为空串，boo类型默认为false。对于嵌入的消息，默认值总是"默认实例"或者消息的"原型"，没有设置任何字段。

- **repeated** ：该字段可以重复任意次数使用。重复值的顺序将会保留在protocol buffer中。可以将重复字段看作动态数组。

><font color=red>**Required Is Forever**</font> 您需要非常谨慎地将字段标记为required修饰。如果在某些时候，您希望停止写或发送一个required字段，在将该字段变更为optional时可能会发生问题————旧的reader认为消息没有这个值则会拒绝或者丢弃这个消息。您应该为考虑为缓冲区编写特定于应用程序地校验例程。有一些Google工程师推测使用**required**弊大于利。他们更倾向于使用**opyional**和**repeated**。不管怎样，这种观点并不普遍。

您还可以在[Protocol Buffer 语言指南](https://developers.google.com/protocol-buffers/docs/proto)中了解到完整地教程。
不要尝试去寻找类似继承的工具，protocol buffer不支持这样做。



























