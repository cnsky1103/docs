---
layout: default
title: Rust：async/await
nav_order: 8
---

# Rust：面试时如何回答async/await？

如果面试被问到了我就这么答。也会记录一下真实面试里问到的

## 什么是async？

如果我们有一个async函数，例如

```rust
async fn foo() -> usize {
    0
}
```

对编译器来说，它会把这个函数转变成这个样子：

```rust
fn foo() -> impl Future<Output = usize> {
    async { 0 }
}
```

也就是说，一个async块返回的是一个Future。

## 什么是Future？

Future类似JavaScript里的Promise，它里面的代码会在未来的某个时刻返回一个Output类型的值，但不是现在，现在这段代码不会被执行。查看Future的源代码可知，Future的定义是

```rust
pub trait Future {
    type Output;

    // Required method
    fn poll(self: Pin<&mut Self>, cx: &mut Context<'_>) -> Poll<Self::Output>;
}
```

一个Output代表返回结果，一个poll方法返回的是Poll结构，会尝试去获取future的最终结果，如果获取失败了也不会block，而是会去睡觉，直到下一次poll的时候。查看Poll的定义立即可知：

```rust
pub enum Poll<T> {
    Ready(T),
    Pending,
}
```

会告诉调用者这个Future到底有没有准备好。

但程序员是不能接触到操作系统线程调度、唤醒这么底层的东西的，poll方法也不是pub的，那么究竟一个Future什么时候会被执行呢？答案是：在对一个Future进行await的时候。

## 什么是await？

await表示我必须要等待这个Future执行完成，并获取它的返回值，才能执行后面的代码。具体是怎么等待的呢？类似如下的伪代码：

```rust
let fut = some_async_func();
let x = loop {
    if let Ready(result) = fut.poll() {
        break result;
    } else {
        fut.try_make_progress();
        yield;
    }
}
```

相当于一个无限循环，每次检查这个future是否完成了，如果完成了就直接返回结果，否则尝试去执行这个future，*并且yield当前线程*。

## select：如何await多个async？

如果我们有多个async函数都要异步执行，但并不在乎它的顺序，只要有一个完成了就可以继续，futures里提供了select宏，用起来类似于：

```rust
loop {
    select!{
        line <- terminal_input().await => {

        }

        stream <- network_input().await => {

        }
    }
}
```

如果用户输入或者网络输入，有一个来了就行。

## 如何取消一个Future？

当你await一个future以后，你就不再对它有任何控制了，它一定会尝试poll，尝试执行代码。那如何主动取消一个Future呢？select可以做到，考虑下面的代码

```rust
select! {
    done <- some_async_func().await => {

    },
    cancel <- cancel.recv().await => {

    }
}
```

这里的cancel是一个channel，例如mpsc的Receiver，当管道里有东西时，说明外部有人发来了取消的消息，select会选择cancel这个await，并且跳过some_async_func，从而达成取消一个async的效果。

## join

```rust
let file0 = files[0].await;
let file1 = files[1].await;
let file2 = files[2].await;
```

这么做是线性的，必须先完成第一个才能往下，join宏提供了并发执行这些的能力

```rust
let (f1, f2, f3) = join!(files[0], files[1], files[2])
```