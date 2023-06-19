---
layout: default
title: Smart Pointers
parent: crust_of_rust
nav_order: 6
---

- Cell被用在small values， like数字或者flags，which会在多个地方被修改，thread-local

- For larger and non-copy types, RefCell provides some advantages.

- UnsafeCell是Rust里cast a shared ref to an exclusive ref的唯一方法

- Values of the Cell<T>, RefCell<T>, and OnceCell<T> types may be mutated through shared references (i.e. the common &T type), whereas most Rust types can only be mutated through unique (&mut T) references. We say these cell types provide ‘interior mutability’ (mutable via &T), in contrast with typical Rust types that exhibit ‘inherited mutability’ (mutable only via &mut T).

- Deref is used when you use . operator, if t.sth and t does not have that member, but deref t does, then deref gets called

- Borrows for RefCell<T>s are tracked at runtime, unlike Rust’s native reference types which are entirely tracked statically, at compile time.

