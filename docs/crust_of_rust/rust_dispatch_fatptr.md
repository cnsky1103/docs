---
layout: default
title: Dispatch and Fat Pointer
parent: Crust of Rust
nav_order: 1
---

- monomorphization优点：效率高；缺点：很难扩展，你没法直接把binary给人家当库用，因为里面没有对面需要的类型的代码，你必须给他source code

- &dyn Hei, what actually stored in &, is 1. a ptr to the actual, concrete, implementing type; 2. a ptr to a vtable for the referenced trait