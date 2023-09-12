---
layout: default
title: Rust：动手实现Channel
nav_order: 6
---

# Rust：动手实现Channel

本文是[Jon的视频](https://www.youtube.com/watch?v=b4mS5UPHh20&ab_channel=JonGjengset)的学习笔记，我们来动手实现一个mpsc的channel。

## mpsc

mpsc，Multi-producer, single-consumer FIFO queue。顾名思义，是一个多生产者、单消费者的channel，可以通过channel函数来新建一个生产者和一个消费者。生产者可以clone成多个，并且可以传递给其他线程；消费者不能clone，永远只有最多一个。这个结构常常被用来进行消息传递、线程同步等。

## 背景知识

我们需要用到一些多线程的工具，包括[Mutex](https://doc.rust-lang.org/std/sync/struct.Mutex.html)，[Arc](https://doc.rust-lang.org/std/sync/struct.Arc.html)和[Condvar](https://doc.rust-lang.org/std/sync/struct.Condvar.html)，不熟悉的小伙伴可以先看一看。其中Condvar是条件变量，在操作系统课中大家应该都学过，它可以让一个线程block并等待在一个变量上，直到另一个线程唤醒它。

## 结构定义

根据mpsc的定义，我们需要一个Sender结构，一个Receiver结构，以及用来创建它们的channel函数

```rust
pub struct Sender<T> {}

pub struct Receiver<T> {}

pub fn channel<T> () -> (Sender<T>, Receiver<T>) {}
```

在rust中常用的共享一个结构的方法是一个Inner，这里我们需要的是一个先进先出的队列，直接用collections里的VecDeque即可

```rust
struct Inner<T> {
    queue: Mutex<VecDeque<T>>
}
```

那么我们的结构里应该包含的就是

```rust
pub struct Sender<T> {
    inner: Arc<Inner<T>>,
}

pub struct Receiver<T> {
    inner: Arc<Inner<T>>,
}
```

创建channel的函数应当返回一个Sender和一个Reciever，他们共享一个inner，直接把Arc给clone一下即可

```rust
pub fn channel<T> () -> (Sender<T>, Receiver<T>) {
    let inner = Inner { queue: Mutex::default() };
    let inner = Arc::new(inner);
    (
        Sender {
            inner: inner.clone(),
        },
        Receiver {
            inner: inner.clone(),
        }
    )
}
```

## 实现

显然，Sender需要一个send方法，往队列的尾部添加一个元素

```rust
impl<T> Sender<T> {
    pub fn send(&mut self, t: T) {
        let mut queue = self.inner.queue.lock().unwrap();
        queue.push_back(t);
    }
}
```

那么对应的，Receiver就需要一个recv方法，从队列头获取

```rust
impl<T> Receiver<T> {
    pub fn recv(&mut self) -> T {
        let mut queue = self.inner.queue.lock().unwrap();
        queue.pop_front()
    }
}
```

这里立刻就带来了一个问题，如果队列为空怎么办？VecDeque的默认实现是返回一个Option，如果为空那就是None。我们当然也可以写一个try_recv来得到这样的结果，但我们想要的并不是这样的行为。我们希望的是recv可以block住，直到有新的元素进来再pop并返回。此时我们就需要Condvar，用来唤醒Receiver

```rust
struct Inner<T> {
    queue: Mutex<VecDeque<T>>,
    available: Condvar,
}
```

这里Condvar必须要在Mutex外面。如果把available和VecDeque都用同一个Mutex包起来，正在send的那个Sender拥有queue的lock，但他又要唤醒Receiver去recv，但recv是拿不到锁的，此时send唤醒完了以后释放锁，recv还是不能往前走一步，就形成了deadlock。这也就是为什么Condvar的wait一定要接收一个Mutex的原因：你需要证明你拥有这个锁，然后把这个锁给对方，他才能make progress。

此时的recv需要考虑队列为空的情况。如果不为空当然直接返回就好，否则我们就wait在available上，并且把queue的锁交给他，然后睡觉，直到被唤醒并拿到新的queue的锁。拿到锁以后我们再重新pop一次，拿到新的值（如果是mpsc的话，第二次进来就一定能拿到了，所以这个不是spin wait）

```rust
impl<T> Receiver<T> {
    pub fn recv(&mut self) -> T {
        let mut queue = self.inner.queue.lock().unwrap();
        loop {
            match queue.pop_front() {
                Some(t) => return t,
                None => {
                    queue = self.inner.available.wait(queue).unwrap();
                }
            }
        }
    }
}
```

Sender需要notify这个Condvar，并且在那之前要释放掉queue的锁。

```rust
impl<T> Sender<T> {
    pub fn send(&mut self, t: T) {
        let mut queue = self.inner.queue.lock().unwrap();
        queue.push_back(t);
        drop(queue);
        self.inner.available.notify_one();
    }
}
```

### Clone

直接derive Clone是不行的，因为自动生成的Clone会要求T也是Clone的

```rust
#[derive(Clone)]
struct S<T>;

等价于

impl<T: Clone> Clone for S<T>
...

```

，但我们实际上要的是inner的Arc的Clone，所以还是得自己写

```rust
impl<T> Clone for Sender<T> {
    fn clone(&self) -> Self {
        Self {
            inner: Arc::clone(&self.inner)
        }
    }
}
```

### close channel

#### no Sender

考虑下面这个测试

```rust
#[test]
fn close() {
    let (tx, rx) = channel::<()>();
    drop(tx);
    rx.recv();
}
```

这个测试会永远block在rx这里，因为所有的sender全部都被drop了，这个channel理论上已经被关闭了。如果queue里还有东西那就只能通过recv把它拿出来，如果没有的话rx就应当返回一个None或者Err，告知这个channel已经关闭了。其实也就是数一数现在tx存在几个，如果是0个的话自然就是关闭了。因此，我们的inner需要再包含一个senders的计数。

```rust
struct Inner<T> {
    queue: VecDeque<T>,
    senders: usize,
}

struct Shared<T> {
    inner: Mutex<Inner<T>>,
    available: Condvar,
}

pub struct Sender<T> {
    shared: Arc<Shared<T>>,
}

pub struct Receiver<T> {
    shared: Arc<Shared<T>>,
}
```

考虑到计数和queue都是用同一个锁，不妨把它们再抽象成真正的inner，而Sender和Receiver共享一个shared，里面有Mutex，也有条件变量。

在Sender进行clone的时候，给计数+1

```rust
impl<T> Clone for Sender<T> {
    fn clone(&self) -> Self {
        let mut inner = self.shared.inner.lock().unwrap();
        inner.senders += 1;
        drop(inner);

        Self {
            inner: Arc::clone(&self.inner)
        }
    }
}
```

drop时，计数-1，如果计数归零了，就需要唤醒receiver，让receiver再检查一次队列。

```rust
impl<T> Drop for Sender<T> {
    fn drop(&mut self) {
        let mut inner = self.shared.inner.lock().unwrap();
        inner.senders -= 1;
        let was_last = inner.senders == 0;
        drop(inner);
        if was_last {
            self.shared.available.notify_one();
        }
    }
}
```

在recv时，队列为空就有两种情况：senders为0，channel已经关闭了，返回None；否则，还有senders，那就等他发消息

```rust
impl<T> Receiver<T> {
    pub fn recv(&mut self) -> Option<T> {
        let mut inner = self.shared.queue.lock().unwrap();
        loop {
            match inner.queue.pop_front() {
                Some(t) => return t,
                None if inner.senders == 0 => return None,
                None => {
                    inner = self.shared.available.wait(inner).unwrap();
                }
            }
        }
    }
}
```

此时这个测试就可以跑通了

```rust
#[test]
fn close() {
    let (tx, rx) = channel::<()>();
    drop(tx);
    assert_eq!(rx.recv(), None);
}
```

#### no Receiver

如果唯一的那个receiver寄了怎么办呢？按照我们现在的实现，sender是不会block住的，无非就是往队列里越塞越多永远不会有人消费罢了。可以优化一下，也可以什么都不做。这里摆了。

## 优化

### SyncSender

如果你查阅官方的mpsc的文档，你会发现出了Sender以外，还有一个SyncSender，以及与之对应的sync_channel。channel函数不带任何参数，他的queue是无限的；而sync_channel带一个bound参数，它的queue的长度是有限的，如果queue满了，再来send的话那这个SyncSender就会block，直到Receiver进行了一次recv，队列里有空间了，才会被唤醒。

想实现这个也很简单，只要再来一个Condvar就行了，每次SyncSender都可以wait在这个Condvar上，然后recv调用的时候就唤醒它。由于有多个sender，notify_one是随机唤醒的。

### buffer recv

由于我们实现的是mpsc，一定有且至多只有一个receiver，不会有别人来抢queue里的数据，那么每次recv的时候，可能众多sender已经往队列里塞了很多东西了，我们可以一次性全取出来，先buffer住，下次来取的时候先看buffer里有没有，有的话就不用去竞争锁了，直接从buffer里拿就行。

```rust
pub struct Receiver<T> {
    shared: Arc<Shared<T>>,
    buffer: VecDeque<T>,
}

impl<T> Receiver<T> {
    pub fn recv(&mut self) -> Option<T> {
        // 先从buffer里拿
        if let Some(t) = self.buffer.pop_front() {
            return Some(t);
        }

        let mut inner = self.shared.queue.lock().unwrap();
        loop {
            match inner.queue.pop_front() {
                Some(t) => {
                    // 如果buffer为空，但是queue里有东西，先把queue的头pop出来，这是我们要返回的
                    // queue里可能还剩下点东西（也可能没有），但buffer一定是空的
                    // 那么可以直接交换queue和buffer的内存，把queue清空，东西全部转移给buffer
                    if !inner.queue.is_empty() {
                        std::mem::swap(&mut self.buffer, &mut inner.queue);
                    }
                    return Some(t);
                },
                None if inner.senders == 0 => return None,
                None => {
                    inner = self.shared.available.wait(inner).unwrap();
                }
            }
        }
    }
}
```

这是常用的优化手法，可以不用频繁的竞争锁。

## 其他

### channel的分类

- Synchronous channel: send会block，容量有限
- Asynchronous channel: send不会block，容量无限
- Rendezvouz channel: 容量为0，通常用来线程同步（类似latch？）
- Oneshot channel: send只能被call一次

