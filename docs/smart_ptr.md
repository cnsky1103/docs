---
layout: default
title: Rust：动手实现Cell、RefCell与Rc
nav_order: 9
---

# Rust：动手实现Cell、RefCell与Rc

本文是[Jon的视频](https://www.youtube.com/watch?v=8O0Nt9qY_vo&ab_channel=JonGjengset)的学习笔记

## 从UnsafeCell开始

Cell、RefCell、Rc都是基于UnsafeCell实现了不同的机制。UnsafeCell名字听上去很不妙，但它并不是一个普通的结构，它是被编译器特殊对待的。查看`std::cell::UnsafeCell`的文档可知，UnsafeCell提供了在Rust中被成为interior mutability的功能。通常在Rust中，如果你有一个&T，你是不能修改T的内容的，而且你也不能把&T直接转成&mut T（这是一个UB），而`UnsafeCell<T>`提供了这样一种功能：如果你有一个UnsafeCell的引用，即`&UnsafeCell<T>`，你可以去修改里面的T的内容。此为“内部可变性”，即interior mutability。其他能提供内部可变性的结构，如RefCell，本质上都是UnsafeCell的一层wrapper。

或者就记住一点：在Rust里唯一的可以通过不可变引用来修改值的方法，是通过UnsafeCell。

UnsafeCell提供的api很简单：get返回一个*mut T，get_mut返回一个&mut T。区别在于，get仅仅要求get(&self)，而get_mut要求(&mut self)。通过get，我们可以用`&UnsafeCell<T>`来得到能够修改的内部T。

## Cell

Cell内部使用了UnsafeCell，且其内存排布和UnsafeCell完全一致。Cell最大的特点是它*不会给出内部的T的引用*，你可以得到内部的T、修改T、替换T、得到T的copy，但不能得到一个&T。你可以通过get_mut来得到一个&mut T，但这会要求&mut self，也就意味着我们此时是Cell的唯一引用。

这个性质是很重要的：如果外面没有任何&T，我们能确定，没有任何人拥有指向内部的T的指针，那么修改T是没问题的。实际上约等于我们拥有了T。

作证这一性质的是，Cell是!Sync的，也就是我们不能把&Cell交给另一个线程。否则的话，另一个线程通过&Cell就可以修改内部的T，那么我们的前置条件：没有任何人拥有指向内部的T的指针，就失效了。也就是说，Cell只能用于单线程。

既然只能用于单线程，那我为什么还要在乎可变不可变啥的呢？毕竟在单线程里修改一个值肯定是安全的，也不会有任何竞争。其实答案很简单：Rust有独特的exclusive borrow机制，在其他语言里默认的，引用一个值自然就可以修改它，这种行为在Rust里是不允许的。那如果我非要有多个引用并且都可能要修改内部的T，例如一个二叉树或者一个连通图呢？那Cell就提供了这种功能，你可以有无限多的&Cell，而每个&Cell都可以修改T。

### 实现一个Cell

上文提到，Cell仅仅是UnsafeCell的一层wrapper。

```rust
#[repr(transparent)]
pub struct Cell<T> where T: ?Sized{
    value: UnsafeCell<T>,
}

impl<T> Cell<T> {
    pub fn new(value: T) -> Self {
        Self { value: UnsafeCell::new(value) }
    }
}
```

两个最基本的函数：set和get。回忆一下UnsafeCell，它的get可以直接返回内部的值的可变指针。

```rust
impl<T> Cell<T> {
    pub fn set(&self, value: T) {
        unsafe { *self.value.get() = value };
    }

    pub fn get(&self) -> T where T: Copy{
        unsafe { *self.value.get() }
    }
}
```

如果有多个线程同时set的话会出问题，但UnsafeCell本身就不是Sync的，Cell自然也不是，所以这里的unsafe是没问题的。

## RefCell

RefCell可以动态的检查借用规则，如果能借就借，否则会返回一个None，而不像&和&mut一样是编译时直接定死的

```rust
enum RefState {
    Unshared,
    Shared(usize),
    Exclusive,
}

pub struct RefCell<T> {
    value: UnsafeCell<T>,
    state: RefState,
}

impl<T> RefCell<T> {
    pub fn new(value: T) -> Sekf {
        Self {
            value: UnsafeCell::new(value),
            state: RefState::Unshared,
        }
    }
}
```

直接按照Rust的借用规则就可以实现borrow和borrow_mut了。对borrow来说，如果还没有引用，那就变成有1个引用；如果有n个引用，那么再borrow一下就变n+1个引用；如果有可变引用，那borrow就不能借出去了；对borrow_mut来说，如果还没引用，就借一个可变引用出去，否则都不能借。

```rust
impl<T> RefCell<T> {
    pub fn borrow(&self) -> Option<&T> {
        match self.state {
            RefState::Unshared => {
                self.state = RefState::Shared(1);
                Some(unsafe { & *self.value.get() })
            }
            RefState::Shared(n) => {
                self.state = RefState::Shared(n+1);
                Some(unsafe { & *self.value.get() })
            }
            RefState::Exclusive => {
                None
            }
        }
    }

    pub fn borrow_mut(&self) -> Option<&mut T> {
        if let RefState::Unshared = self.state {
            self.state = RefState::Exclusive;
            Some(unsafe { &mut *self.value.get() })
        } else {
            None
        }
    }
}
```

这两个函数是过不了编译的，不妨请读者脑内编译一下，看看是哪里出了问题？

答案是，它们需要的仅仅是&self，但是却修改了内部的state的值，这违反了借用原则。

等等，通过&self修改内部的值，这不就是我们之前用的Cell吗？很幸运，讲完Cell以后我们立刻就找到了实践的地方：

```rust
pub struct RefCell<T> {
    value: UnsafeCell<T>,
    state: Cell<RefState>,
}
```

再把borrow和borrow_mut里相关的方法都改成set和get，注意get需要T是copy的，而我们的RefState直接derive成Copy和Clone没有任何问题。

但现在的RefCell只能borrow，不能释放，我们的state只能往上升，不能往下降。当一个borrow被drop的时候，理当降级state才对。但我们又不能直接对RefCell实现drop，毕竟我们drop的是borrow来的引用。可我们也不能直接对&T写一个drop，这违反了孤儿原则。咋办呢？还是只能把&T给包一层wrapper，不妨叫Ref和RefMut，其实叫啥都无所谓。

```rust
pub struct Ref<'refcell, T> {
    refcell: &'refcell RefCell<T>,
}
```

这个Ref会被用在borrow的返回值那里，也就是

```rust
pub fn borrow(&self) -> Option<Ref<'_, T>> {
    ...
}
```

既然是一层wrapper，首先需要的自然是deref

```rust
impl<T> std::ops::Deref for Ref<'_, T> {
    type Target = T;
    fn deref(&self) -> &Self::Target {
        unsafe { &*self.refcell.value.get() }
    }
}
```

unsafe是ok的，理由同上。对这个Ref，最重要的是它drop的时候，要对state进行正确的降级处理，分情况讨论：如果此时只有1个引用，那drop掉以后会降级成Unshared，如果有n个引用，会降级成n-1；其他情况都是不可能的。

```rust
impl<T> Drop for Ref<'_, T> {
    fn drop(&mut self) {
        match self.refcell.state.get() {
            RefState::Exclusive | RefState::Unshared => unreachable!(),
            RefState::Shared(1) => {
                self.refcell.state.set(RefState::Unshared);
            }
            RefState::Shared(n) => {
                self.refcell.state.set(RefState::Shared(n - 1));
            }
        }
    }
}
```

同理，可以写出一个RefMut，作为borrow_mut的返回值

```rust
pub struct RefMut<'refcell, T> {
    refcell: &'refcell RefCell<T>,
}

impl<T> std::ops::Deref for RefMut<'_, T> {
    type Target = T;
    fn deref(&self) -> &Self::Target {
        unsafe { &*self.refcell.value.get() }
    }
}

impl<T> std::ops::DerefMut for RefMut<'_, T> {
    fn deref_mut(&mut self) -> &mut Self::Target {
        unsafe { &mut *self.refcell.value.get() }
    }
}

impl<T> Drop for RefMut<'_, T> {
    fn drop(&mut self) {
        match self.refcell.state.get() {
            RefState::Shared(_) | RefState::Unshared => unreachable!(),
            RefState::Exclusive => {
                self.refcell.state.set(RefState::Unshared);
            }
        }
    }
}
```

## Rc

Rc允许你有多个引用指向内部的值，且在最后一个引用消失时会drop掉内部的值。注意，Rc不提供内部可变性，它只提供引用计数，若想得到内部可变性，还需要在Rc里面放一个RefCell。

```rust
struct RcInner<T> {
    value: T,
    refcount: Cell<usize>,
}

pub struct Rc<T> {
    inner: RcInner<T>
}
```

这里首先就有一个问题：既然Rc可以提供很多引用来指向同一个值，那这个值应当在堆上。如果在栈上的话那栈被释放就寄了。要得到一个堆上的static的值，可以用Box然后into_raw，把Box释放掉，得到一个堆上的值的裸指针。

```rust
struct RcInner<T> {
    value: T,
    refcount: Cell<usize>,
}

pub struct Rc<T> {
    inner: *const RcInner<T>,
}

impl<T> Rc<T> {
    pub fn new(v: T) -> Self {
        let inner = Box::new(RcInner {
            value: v,
            refcount: Cell::new(1),
        });

        Rc {
            inner: Box::into_raw(inner),
        }
    }
}
```

作为一个wrapper，需要实现deref，这里的deref要把裸指针转回T返回去，这个操作是unsafe的。但考虑一下Rc的定义：只有在最后一个Rc的引用结束的时候，才会释放掉内部的值。而我们既然有一个需要deref的Rc，说明我们有一个Rc的引用，最后一个引用一定还没有结束，那么堆上的值还没有被释放，所以这个unsafe是ok的。

```rust
impl<T> std::ops::Deref for Rc<T> {
    type Target = T;
    fn deref(&self) -> &Self::Target {
        // SAFETY: self.inner is a Box that is only deallocated when the last Rc goes away.
        // we have an Rc, therefore the Box has not been deallocated, so deref is fine.
        &unsafe { &*self.inner }.value
    }
}
```

clone就是简单的给引用计数+1

```rust
impl<T> Clone for Rc<T> {
    fn clone(&self) -> Self {
        let inner = unsafe { &*self.inner };
        let c = inner.refcount.get();
        inner.refcount.set(c + 1);
        Rc {
            inner: self.inner,
        }
    }
}
```

drop需要考虑如果此时计数为1，要把堆上的值给释放掉。释放的过程是unsafe的，但既然我们是最后一个Rc了，外面肯定也没有内部的值的引用了，drop自然也是安全的。

```rust
impl<T> Drop for Rc<T> {
    fn drop(&mut self) {
        let inner = unsafe { &*self.inner };
        let c = inner.refcount.get();
        if c == 1 {
            drop(inner);
            // SAFETY: we are the _only_ Rc left, and we are being dropped.
            // therefore, after us, there will be no Rc's, and no references to T.
            let _ = Box::from_raw(self.inner);
        } else {
            // there are other Rcs, so don't drop the Box!
            inner.refcount.set(c - 1);
        }
    }
}
```

### PhantomData

这块没听懂，摆了