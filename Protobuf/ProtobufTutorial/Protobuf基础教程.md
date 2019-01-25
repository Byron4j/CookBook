## protobuf 基础教程

### 示例开始：定义协议格式 Protocol Format

示例：一个简单的通讯簿， **.proto** 文件见 [addressbook.proto](Protobuf/ProtobufTutorial/addressbook.proto)。

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


### 编译你的Protocol Buffers

现在您有了一个 **.proto** 文件了，下一步要做的事，就是生成一个您将要读、写的AddressBook类。因此，您需要运行potocol buffer编译器 **protoc** 处理 **.proto**:

- 如果您没有安装编译器，[下载安装包](https://developers.google.com/protocol-buffers/docs/downloads.html)，根据README安装。


#### 协议编译安装

Protocol 编译器是C++编写的。如果您使用C++，请根据[C++安装指导](https://github.com/protocolbuffers/protobuf/blob/master/src/README.md)安装protoc。
对于非C++用户，最简单的安装protocol编译器的方式是从release页下载预构建的二进制：https://github.com/protocolbuffers/protobuf/releases。

下载好的 protoc-$VERSION-$PLATFORM.zip。包含了二进制protoc，还有一系列与protobuf发布的标准的.proto文件。
如果您还想找旧版本，可以在https://repo1.maven.org/maven2/com/google/protobuf/protoc/找到。

这些预构建的二进制文件只会在发行版本中提供。

#### Protobuf 运行时安装

Protobuf 支持几种不同的编程语言。针对于每一种语言，你可以参考源码中的各种语言的说明指导。

- 现在运行编译器，需要指明**源目录**(应用源代码所在-如果您没有提供一个值的话默认使用当前目录)、**目标目录**（您想要代码生成的目的目录，通常类似于 **$SRC_DIR**）、还有 **.proto** 的路径。在这种情况下，您可以如下操作：

**protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/addressbook.proto**

因为您想生成Java类，您看到 **--java_out** 选项，类似的选项也提供了其他编程语言。

这将会在您指定的目标目录生成**com/example/tutorial/AddressBookProtos.java**。

### Protocol Buffer API

让我们来看看一些生成的代码，并查看一些由编译器为您创建的类与方法。如果您看一下**AddressBookProtos.java** 类，可以看到定义了一个叫做**AddressBookProtos**的类，内嵌了您在**addressbooproto**文件中为每个消息指定的类。每个类都拥有自身的**Builder**类，您可以使用它来创建一个对应的类实例。您可以在下面的 Builders vs. Messages可以看到更多细节。

messages 和 builders 拥有针对消息每个字段的自动生成的访问方法。message仅仅拥有getters方法、builders拥有getters、setters方法。这里有一些关于**Person**类的访问方式(为了简洁，忽略了实现)：

```java
// required string name = 1;
public boolean hasName();
public String getName();

// required int32 id = 2;
public boolean hasId();
public int getId();

// optional string email = 3;
public boolean hasEmail();
public String getEmail();

// repeated .tutorial.Person.PhoneNumber phones = 4;
public List<PhoneNumber> getPhonesList();
public int getPhonesCount();
public PhoneNumber getPhones(int index);
```

同时， **Person.Builder** 内部类则拥有getters、setters：

```java
// required string name = 1;
public boolean hasName();
public java.lang.String getName();
public Builder setName(String value);
public Builder clearName();

// required int32 id = 2;
public boolean hasId();
public int getId();
public Builder setId(int value);
public Builder clearId();

// optional string email = 3;
public boolean hasEmail();
public String getEmail();
public Builder setEmail(String value);
public Builder clearEmail();

// repeated .tutorial.Person.PhoneNumber phones = 4;
public List<PhoneNumber> getPhonesList();
public int getPhonesCount();
public PhoneNumber getPhones(int index);
public Builder setPhones(int index, PhoneNumber value);
public Builder addPhones(PhoneNumber value);
public Builder addAllPhones(Iterable<PhoneNumber> value);
public Builder clearPhones();
```

正如您所看到的一样，每个字段都有简单的 Java Bean 风格的getter、setter 方法。
每个字段也有其**has**方法，如果设置了字段值，则会返回true。
最后，每个字段还有**clear**方法，可以将该字段回归到其空白状态。

**repeated** 字段由一些额外的方法：

- **getXXXCount** 方法用于获取泪飙大小； 
- 增加了根据元素索引下标获取元素的get、set方法（public PhoneNumber getPhones(int index); 和 public Builder setPhones(int index, PhoneNumber value);）。
- **add、addAll* 方法追加新元素（列表）到列表中。

注意到所有这些访问方法都使用了驼峰命名方式，即使 **.proto** 文件使用了小写和下划线。这个转换是由protocol编译器自动完成的，所以生成的类符合Java风格标准规范。在 **.proto** 中您应该总是将字段名用小写和下划线来命名。
可以参考[风格指南](https://developers.google.com/protocol-buffers/docs/style)获取更多良好的 **.proto** 命名风格。
更多关于编译器为字段生成的特定的详细信息可以参考 [Java生成代码参考指南](https://developers.google.com/protocol-buffers/docs/reference/java-generated)

#### 枚举和内部类

生成的代码包含了一个枚举 **PhoneType** ，嵌套在 **Person** 类中：

```java
public static enum PhoneType {
  MOBILE(0, 0),
  HOME(1, 1),
  WORK(2, 2),
  ;
  ...
}
```

内部类**Person.PhoneNumber**也生成了，正如您期望的一样，它是作为**Person**的一个内部类的。

#### Builders vs. Messages

protocol buffer编译器生成的类都是不可变的。一旦一个message对象被创建后，就不能再被更改，类似于Java的String类。要构建一个message，您必须构建一个builder，并设置任何您想设置的字段的值，再调用builders's 的**builder** 方法。（使用过lombok的朋友，这很类似其@Builder注解的用法）

您可能还注意到每个builder的方法返回的是另一个builder。返回的对象其实和您调用方法的builder是同一个。这种处理方式很方便，您可以将多个setter在一行代码中串写。

这里有一个示例，创建一个**Person**的实例：

```java
Person john =
  Person.newBuilder()
    .setId(1234)
    .setName("John Doe")
    .setEmail("jdoe@example.com")
    .addPhones(
      Person.PhoneNumber.newBuilder()
        .setNumber("555-4321")
        .setType(Person.PhoneType.HOME)
        .build())
    .build();
```

#### 标准的message方法

每个message和builder类也包含一系列其他的方法，可以让您检查与操作message：
- **isInitialized()** ： 检查是否所有的required 字段都已经set过值了/
- **toString()** ： 返回一个可读性良好的message表示，通常对调试特别有用。
- **mergeFrom(Message other)** ： （只在builder有）将other合并到该message中
- **clear()** ： （只在builder有）清除所有的字段，时其恢复到最初的空状态

#### 解析和序列化

最后，每个 proto buffer 类都有一些用于读和写二进制的方法。

- **byte[] toByteArray()** ： 序列化message，返回一个byte数组。
- **static Person parseFrom(byte[] data)** ： 将给定的byte数组解析成一个message。
- **void writeTo(OutputStream output)** ： 序列化message，并将其写入一个输出流。
- **static Person parseFrom(InputStream input)** ： 读取一个输入流并从其解析处一个message。

这些只是为序列化和解析（反序列化）而提供的两组操作方法。更多完整列表可以参考 [Message API帮助文档](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/Message)。

>**<font color=red>Protocol Buffers 和 面向对象</font>** Protocol buffer 类基本上是哑数据持有者（类似C中的struct）；在对象模型中，它们不是一等公民。如果您想向生成类中添加更丰富的行为，最好的方式就是在一个特定于应用程序中的类中包装protocol buffer生成的类。如果您不能控制.proto文件的设计(例如，如果您正在重用来自另一个项目的一个文件)，那么包装协议缓冲区也是一个好主意。在这种情况下，您可以使用包装器类来创建更适合您的应用程序的独特环境的接口:隐藏一些数据和方法，公开方便的函数，等等。这将破坏内部机制，而且无论如何都不是良好的面向对象实践。


### 写一个Message
现在尝试使用protocol buffer 类吧。第一件事，希望通讯簿可以把个人的信息详情写道通讯簿文件中。为了做到这一点，您需要创建和填入protocol buffer的类实例，并将它们写入一个输出流中。

这里有一个程序，从一个文件中读取了一个**AddressBook**，根据用户的输入还添加了一个新的**Person**到通讯簿中，并将新的**AddressBook**重新写入文件中。

```java

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

class AddPerson {
  // This function fills in a Person message based on user input.
  static Person PromptForAddress(BufferedReader stdin,
                                 PrintStream stdout) throws IOException {
    Person.Builder person = Person.newBuilder();

    stdout.print("Enter person ID: ");
    person.setId(Integer.valueOf(stdin.readLine()));

    stdout.print("Enter name: ");
    person.setName(stdin.readLine());

    stdout.print("Enter email address (blank for none): ");
    String email = stdin.readLine();
    if (email.length() > 0) {
      person.setEmail(email);
    }

    while (true) {
      stdout.print("Enter a phone number (or leave blank to finish): ");
      String number = stdin.readLine();
      if (number.length() == 0) {
        break;
      }

      Person.PhoneNumber.Builder phoneNumber =
        Person.PhoneNumber.newBuilder().setNumber(number);

      stdout.print("Is this a mobile, home, or work phone? ");
      String type = stdin.readLine();
      if (type.equals("mobile")) {
        phoneNumber.setType(Person.PhoneType.MOBILE);
      } else if (type.equals("home")) {
        phoneNumber.setType(Person.PhoneType.HOME);
      } else if (type.equals("work")) {
        phoneNumber.setType(Person.PhoneType.WORK);
      } else {
        stdout.println("Unknown phone type.  Using default.");
      }

      person.addPhones(phoneNumber);
    }

    return person.build();
  }

  // Main function:  Reads the entire address book from a file,
  //   adds one person based on user input, then writes it back out to the same
  //   file.
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage:  AddPerson ADDRESS_BOOK_FILE");
      System.exit(-1);
    }

    AddressBook.Builder addressBook = AddressBook.newBuilder();

    // Read the existing address book.
    try {
      addressBook.mergeFrom(new FileInputStream(args[0]));
    } catch (FileNotFoundException e) {
      System.out.println(args[0] + ": File not found.  Creating a new file.");
    }

    // Add an address.
    addressBook.addPeople(
      PromptForAddress(new BufferedReader(new InputStreamReader(System.in)),
                       System.out));

    // Write the new address book back to disk.
    FileOutputStream output = new FileOutputStream(args[0]);
    addressBook.build().writeTo(output);
    output.close();
  }
}
```



### 读一个Message

当然，如果您不能从通讯簿中就获取任何信息，那也就没什么用了。这个示例展示了读取前面一个示例创建的文件并输出全部信息：

```java
import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

class ListPeople {
  // Iterates though all people in the AddressBook and prints info about them.
  static void Print(AddressBook addressBook) {
    for (Person person: addressBook.getPeopleList()) {
      System.out.println("Person ID: " + person.getId());
      System.out.println("  Name: " + person.getName());
      if (person.hasEmail()) {
        System.out.println("  E-mail address: " + person.getEmail());
      }

      for (Person.PhoneNumber phoneNumber : person.getPhonesList()) {
        switch (phoneNumber.getType()) {
          case MOBILE:
            System.out.print("  Mobile phone #: ");
            break;
          case HOME:
            System.out.print("  Home phone #: ");
            break;
          case WORK:
            System.out.print("  Work phone #: ");
            break;
        }
        System.out.println(phoneNumber.getNumber());
      }
    }
  }

  // Main function:  Reads the entire address book from a file and prints all
  //   the information inside.
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage:  ListPeople ADDRESS_BOOK_FILE");
      System.exit(-1);
    }

    // Read the existing address book.
    AddressBook addressBook =
      AddressBook.parseFrom(new FileInputStream(args[0]));

    Print(addressBook);
  }
}
```


### 扩展 Protocol Buffer

当您使用protocol buffer发布代码后，毋庸置疑，您会希望改善protocol buffer的定义。如果您想您的新buffer可以向后兼容，并且旧的buffer可以向前兼容——您肯定想要这个——这需要遵循一些规则。在新版本的protocol buffer中需要遵循：
- 您不能改变已经存在的字段的tag的编号
- 您不能添加或者删除任何required字段
- 您可以删除optional或者repeated字段
- 您可以添加新的optional或者repeated字段，但那时您必须使用新的tag编号（即该tag编号没有在这个protocol buffer文件中使用，或者已经被删除了）

如果您遵循这些规则，旧代码可以友好地读取新的message，并且忽略新的字段。对于旧代码，那些删除地optional字段会使用它们默认的值，而repeated字段则为空。
新代码显然会读取旧的message。不论如何，切记新的optional字段在旧的message中是不会出现的，所以您需要检查它们是否设置了值，可以使用 **has_**，或者在您的 **.proto**文件中在该字段的tab编号后面使用 **[default = value]** 为其提供一个default值。
如果optional字段没有指定default值，则会根据该类型自动为其赋值：string类型则赋值空串，对于布尔类型则赋值为false，对于数值类型则赋值为0.
请注意，假如您添加了一个repeated字段，您的新代码将无从知晓该字段是空的(新代码)，还是从来没有设置过值(旧代码)，因为它没有 **has_** 方法。

### 高级用法

Protocol buffers 不仅仅只是提供了简单的访问和序列化功能。可以访问 **[Java API帮助文档](https://developers.google.com/protocol-buffers/docs/reference/java/)**.

protocol message类体哦概念股了一个关键的特性——反射。您可以遍历message的所有字段和操作它们的值而不需要编写指定的message类型的代码。
反射的一个有用的使用方式是在各种编码之间转换协议消息，例如XML或者JSON。
一个关于反射的更高级的用法是从两个相同类型的消息message中找出不同之处，或者开发出一种协议消息的“正则表达式”，是的您可以编写这种表达式去匹配某些message内容。
如果您充分发挥您的想象，使用protocol Buffers可以应用在更广泛的问题上，正如您所期望的那样。















































