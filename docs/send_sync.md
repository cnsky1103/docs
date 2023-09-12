---
layout: default
title: Rust：Send与Sync
nav_order: 7
---

# Rust：Send与Sync

本文是[Jon的视频](https://www.youtube.com/watch?v=yOezcP-XaIw&ab_channel=JonGjengset)的学习笔记。

Send和Sync都是Rust的marker trait，他们没有任何方法，仅仅作为标记，被编译器自动实现，即任何可以是Send/Sync的类型都会自动是。

## Send

Send表明一个类型可以跨线程传递（例如，spawn的时候move）。如果一个类型不是Send的，通常意味着如果你把他传给另一个线程，会违反Rust底层的一些规则或者逻辑上的一些规则。两个典型例子是Mutex和Rc。

操作系统规定了释放锁的线程必须是获取锁的线程，这就导致了Mutex是不能Send给另一个线程的，因为你不能让另一个线程去释放你自己的锁。或者更一般的，如果一个类型的drop会尝试去修改一些thread local的值，那如果从A线程Send到B线程，那么drop时修改的其实是B线程的thread local的值，但实际上这些值是A线程的。

而Rc的内部有一个引用计数，每次Rc在clone的时候计数+1，drop的时候计数-1。这个操作不是原子的，所以如果Rc也可以Send，多个线程修改计数器显然会产生竞争。而Arc也就是原子操作版的Rc，他就可以Send。

## Sync

Sync表明一个类型可以安全地给其他线程共享它的引用。Sync和Send的关系极其密切。Sync的严格定义是：A type T is Sync if and only if &T is Send，一个类型的*不可变*引用如果可以传给其他线程，那它就可以合法地被多个线程操作。例如，Mutex虽然不是Send，但传一个不可变引用给其他线程是ok的，反正lock、unlock这些操作需要的都是可变引用，不可变引用什么也做不了，所以Mutex是Sync的。而Rc即便传不可变引用出去，它还是可以调用clone，因此&Rc不是Send的，从而Rc也不能Sync。

## Send + !Sync

有些类型是可以Send但不能Sync的，最典型的例子是Cell/RefCell。这些类型拥有内部可变性（interior mutability）。Cell允许你在只有Cell的不可变引用的时候可变的修改内部的T的值。

Cell永远不会把内部的T的引用给出去，即外面一定没有T的引用，那么在单线程的任何时刻，Cell都是T的唯一owner，那么自然也可以安全地修改T的值，即使你只有Cell的不可变引用。那么显然的，你也可以把T的Cell给Send出去；但是不能Sync，也就是不能把Cell的引用给送出去，否则Cell是T的唯一owner这一条件就不存在了，此时别的线程也可以通过Cell的不可变引用来修改内部的T的值。

## 裸指针

任何裸指针都是!Send且!Sync的，即

```rust
impl<T> !Send for *const T
where
    T: ?Sized,

impl<T> !Send for *mut T
where
    T: ?Sized,

impl<T> !Sync for *const T
where
    T: ?Sized,

impl<T> !Sync for *mut T
where
    T: ?Sized,
```

这并不是说裸指针就一定不能Send或者Sync，这相当于编译器的一种预警，以防程序员在不知道的情况下把某些带着裸指针的结构进行了Send和Sync。程序员如果想要Send和Sync的话就得unsafe的手动实现一下，这就迫使程序员来思考这么做是否正确。