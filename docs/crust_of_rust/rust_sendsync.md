---
layout: default
title: Send and Sync
parent: Crust of Rust
nav_order: 5
---

- mutex guard is not a Send, because 释放锁的线程必须是获取锁的线程，如果mutex guard被Send了就出问题了

- 或者是thread local state，从A Send到了B，B把他drop了