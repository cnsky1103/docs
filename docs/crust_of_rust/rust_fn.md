---
layout: default
title: Fn
parent: Crust of Rust
nav_order: 2
---

```rust
fn bar() {}

fn main() {
    let x = bar; // x is a fn item, instead of a fn pointer (similar but subtlely diff)
    // x is a zero-sized value
}
```

```rust
fn bar<T>() {}

fn main() {
    let mut x = bar::<i32>;
    // x = bar::<u32>; // error! if it is a fn pointer it should be ok, but x has a specific type
    assert_eq!(std::mem::size_of_val(&x), 0);
}
```

```rust
fn bar<T>(_: u32) -> u32 {}

fn baz(f: fn(u32) -> u32) {
    // it takes any function pointer (has the size of a pointer) which has the right signature
    assert_eq!(std::mem::size_of_val(&f), 8);
}

fn main() {
    // the compiler coerces the fn item to a fn pointer
    baz(bar::<i32>);
    baz(bar::<u32>);
}
```