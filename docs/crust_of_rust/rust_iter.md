---
layout: default
title: Iterators
parent: crust_of_rust
nav_order: 3
---

```rust
let vs = vec![1,2,3];
for v in vs {
    // consumes vs, owned v
}

for v in vs.iter() {
    // borrows vs, reference to v
}

for v in &vs {
    // equivalent to  vs.iter()
}
```
