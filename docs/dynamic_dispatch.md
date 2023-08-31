---
layout: default
title: Rust：Trait Object、动态绑定与胖指针
nav_order: 5
---

# Rust：Trait Object、dyn与胖指针

准备Rust面试的时候发现能找到的为数不多的面经都问到了trait object，动态绑定，vtable之类的东西，因此决定系统的学一下。本文是[Jon的视频](https://www.youtube.com/watch)的学习笔记。

## 从Monomorphization开始

在Rust book的介绍泛型的一节里，在最后用了一个小节来讲泛型的性能，附上[the book里这一节的链接](https://doc.rust-lang.org/book/ch10-01-syntax.html#performance-of-code-using-generics)。

里面提到，Rust在编译器使用了Monomorphization。它会将泛型的代码全都转化成具体类型的代码。书里举了Option的例子，读者可以自行查看，这里我们再看一个更具体的例子
```rust
fn strlen(s: impl AsRef<str>) -> usize {
    s.as_ref().len()
}

fn strlen2<S>(s: S) -> usize where S: AsRef<str> {
    s.as_ref().len()
}
```

上面两个函数的作用是一样的，接收一个可以转换成str的引用，返回字符串的长度。只不过一个用的是impl，一个用的是泛型。通过Monomorphization，在编译期Rust会为所有需要用到的类型生成对应的`strlen`代码。例如，下面分别调用了`&str`版本的和`String`版本的`strlen`：

```rust
fn foo() {
    strlen("hello world");
    strlen(String::from("hello world"));
}
```

那么在编译期，Rust会生成类似于如下的代码：

```rust
fn strlen_refstr(s: &str) -> usize {
    s.len()
}

fn strlen_string(s: String) -> usize {
    s.len()
}
```

这就是Monomorphization的基本概念，通过Monomorphization， Rust得以在编译期解决掉泛型，避免了在运行时进行额外的类型检查等；但另一方面，Monomorphization使得你很难把Rust的库作为二进制文件发布，因为你没法知道用户会怎么使用你的泛型，所以用户必须用你的源代码重新编译一遍，而且，编译出来的很多泛型代码会拖慢编译速度、增大编译后的体积。尽管Rust有一些inline的机制，但也没法完全解决Monomorphization带来的体积膨胀问题。

## Trait Object

上一节里提到的，在编译期生成不同类型的代码，然后在编译期直接决定应该调用哪个代码，实际上就是static dispatch。但是，静态类型绑定的表达能力是不够的，考虑下面这个例子：

```rust
trait Hello {
    fn hello(&self);
}

impl Hello for &str {
    fn hello(&self) {
        println!("hello {} from &str", self);
    }
}

impl Hello for String {
    fn hello(&self) {
        println!("hello {} from String", self);
    }
}
```

这个例子非常简单，就是调用hello时输出一句话，`&str`和`String`都可以调用它，那么下面这种写法想必也是可行的

```rust
fn bar(s: &[impl Hello]) {
    for h in s {
        h.hello();
    }
}
```

我们接收了一个由Hello组成的slice，给他们一个一个的call hello，那么下面这么调用bar也是可行的

```rust
fn foo() {
    bar(&["J", "Jon"]);
    bar(&[String::from("J"), String::from("Jon")]);
}
```

但这里有个问题，实际上我根本不在乎我slice里的究竟是&str还是String，它只要是个Hello就可以了，那么理论上我应该也能这么写：

```rust
fn foo() {
    bar(&["J", String::from("Jon")]); // compile error
}
```

但不行，因为slice里的变量必须是同一个类型。impl只不过是泛型的某种语法糖而已，bar函数本质上还是个泛型函数，只能接收实现了Hello的一个类型。最好我们能写一个这种函数出来：

```rust
fn bar(s: &[Hello]){}
```

当然，这是不可以的。编译器会让我们加上一个`dyn`。虽然完全不知道这个dyn是什么意思，但是fine，我们加上试试

```rust
fn bar(s: &[dyn Hello]) {}
```

编译器又会告诉我们，`dyn Hello`的Size不能在编译期确定。好吧，至少这个错误提示有点意义了。想来也是，我们要的是随便什么实现了Hello的东西，String和&str的大小是不一样的，我们甚至还可以给一个巨大的结构实现一下Hello，然后也把它传进去。那对编译器来说，它根本就不知道创建变量、调用函数的时候，对应的栈空间应该分配多大，也就没法生成汇编代码了；另一方面，对我们的slice来说，如果它的成员的Size不是确定的话，那你要怎么去获取第3个成员呢？毕竟你不能从开头数2个成员的大小来得到第三个成员的内存地址了。总之，Sized看起来似乎的编译器的某种十分基础的要求，而我们的Hello是不满足的，因为任意大小的结构都可以去实现Hello。

> Sized是一个默认实现的trait，一个类型只要是能Sized的，都会自动是。几乎所有的类型都是Sized的，因为编译期通过类型的定义基本都能确定大小，但也有些例外，比如str，一个字符串的长度是任意的，所以我们大部分时候都要用它的引用&str，一个指针的大小是确定的；又比如[u8]，一个slice的长度是任意的，但&[u8]作为一个引用，它的大小是确定的。

Ok，是时候想办法让我们的Hello有一个确定的大小了。就像上面提到的，我们可以用一个确定大小的类型来把它包起来

```rust
dyn Hello // not Sized
&dyn Hello // Sized
Box<dyn Hello> // Sized
```

这些个Sized的类型，就是所谓的trait object了。

```rust
fn hello_boxdyn(h: Box<dyn Hello>) {
    h.hello();
}

fn hello_refdyn(h: &dyn Hello) {
    h.hello();
}
```

上面的代码是可以编译的。Cool！问题解决了

吗？



## vtable

在动态的类型里，我们并不会在编译期就给每个类型生成对应的代码，那是static dispatch做的事情。那么问题来了，在不知道具体类型，自然也不知道具体类型的对应代码的情况下，Rust是怎么在运行时调用正确的函数的呢？具体一点，比如

```rust
fn say(h: &dyn Hello) {
    h.hello();
}

fn foo() {
    let input = some number from the user;
    if input > 0 {
        say(&"hello");
    } else {
        say(&String::from("world"));
    }
}
```

这个例子里我们甚至都不知道运行时哪个类型会被调用，因为它完全取决于用户的输入。这种情况下Rust究竟要怎么去找到对应的方法呢？

在Rust的reference书里提到过一个[动态大小类型](https://doc.rust-lang.org/reference/dynamically-sized-types.html#dynamically-sized-types)（Dynamically Sized Type，DST），里面提到了：

- Pointer types to DSTs are sized but have twice the size of pointers to sized types
    - Pointers to slices also store the number of elements of the slice.
    - Pointers to trait objects also store a pointer to a vtable.

这里引出了两个重要的概念，胖指针（fat pointer），即DST的指针的大小是普通指针的两倍，对slice来说，一个指针存地址，另一个指针存slice的长度；对trait object来说，一个指针存地址，另一个指针存虚表（vtable）。

看起来我们终于接触到了动态类型系统的核心。

对每一个trait的每一个具体实现，都有一个对应的vtable。在将&str转变为Hello的过程中，实际上发生的是：

```
&str -> &dyn Hello
  1. pointer to the &str
  2. &HelloVtable {
    hello: &<&str as Hello>::hello
    // something else
  }
```

这里虚表的名字是随便起的，虚表里除了存一些基础的东西以外，最重要的是存了所有Hello里的函数的指针，指针指向impl里的部分，比如上面的指针就应该指向

```rust
//impl Hello for &str {
    fn hello(&self) {
        println!("hello {} from &str", self);
    }
//}
```

这个虚表也是编译期生成的。在运行时，每一个&dyn Hello的虚表都可能是不一样的，因为具体类型不一样。

## Limitations

### static方法

如果一个trait里有不接收self的静态方法，那他就不是object safe的，例如，如果我们的Hello里有

```rust
trait Hello {
    fn hello(&self);

    fn weird();
}
```

那么，再想用`&dyn Hello`就是不可以的了，编译器会提示
> the trait `Hello` cannot be made into an object... because associated function `weird` has no `self` parameter

同时，编译器还给了提示，要么给weird函数加上self，要么限制weird函数，使其不再适用于trait object，即

`fn weird() where Self: Sized {}`

此时，我们不再能通过一个`&dyn Hello`类型的变量来调用weird，因为它不在vtable里了，如果你这么做了，编译器会告诉你weird不能被trait object调用，因为它要求Self是Sized的，而显然，dyn不是Sized的。

同样的，如果`trait Hello where Self: Sized`，那么整个trait都不是object safe的。

### 泛型方法

vtable里的方法不能是泛型的，因为上面提到的monomorphization

### return Self

既然Self不是Sized，自然也无法作为返回类型，比如你不能返回一个x.clone(), where x is &dyn Hello

