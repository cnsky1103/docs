---
layout: default
title: Smart Pointers
parent: Crust of Rust
nav_order: 6
---

- Cell被用在small values， like数字或者flags，which会在多个地方被修改，thread-local

- Cell永远不会give out它里面的T的引用，get方法要求T是copy，返回的是T的copy。如果我们能确信外面没有T的引用，那么修改T就是ok的，因为Cell是not sync，不会有其他人来修改。

- For larger and non-copy types, RefCell provides some advantages.

- UnsafeCell是Rust里cast a shared ref to an exclusive ref的唯一方法，UnsafeCell是特殊的

- RefCell相比起Cell，额外多了一个引用状态，来记录给出去了多少T的引用，或者是exclusive引用。

- Values of the Cell<T>, RefCell<T>, and OnceCell<T> types may be mutated through shared references (i.e. the common &T type), whereas most Rust types can only be mutated through unique (&mut T) references. We say these cell types provide ‘interior mutability’ (mutable via &T), in contrast with typical Rust types that exhibit ‘inherited mutability’ (mutable only via &mut T).

- Deref is used when you use . operator, if t.sth and t does not have that member, but deref t does, then deref gets called

- Borrows for RefCell<T>s are tracked at runtime, unlike Rust’s native reference types which are entirely tracked statically, at compile time.

- Rc allows you to have multiple references to a thing, and only deallocate it when the last reference goes away. It does not provide mutability, so we need RefCell inside it.