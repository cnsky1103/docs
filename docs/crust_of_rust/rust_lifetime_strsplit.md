---
layout: default
title: Rust生命周期：StrSplit
parent: Crust of Rust
nav_order: 4
---

- Self可以让你在修改类型名字的时候不用额外修改其他内容

- 如果你认为对一个类型可能有不同实现时，用Generic；如果你认为对同一个类型，trait里的函数只有一个实现，比如对hashmap他的iterator都是一样的，用associate types

- 如果只关心pattern的一种，可以用if let；如果关心多种pattern，用match

- take把原本的Option设置为None，然后返回一个新的Option

```rust
#![warn(rust_2018_idioms)]

pub struct StrSplit<'haystack, 'delimiter> {
    remainder: Option<&'haystack str>,
    delimiter: &'delimiter str,
}

impl<'haystack, 'delimiter> StrSplit<'haystack, 'delimiter> {
    fn new(haystack: &'haystack str, delimiter: &'delimiter str) -> Self {
        StrSplit {
            remainder: Some(haystack),
            delimiter,
        }
    }
}

impl<'haystack, 'delimiter> Iterator for StrSplit<'haystack, 'delimiter> {
    type Item = &'haystack str;

    fn next(&mut self) -> Option<Self::Item> {
        let remainder = self.remainder.as_mut()?;
        if let Some(next_delim) = remainder.find(self.delimiter) {
            let until_delim = &remainder[..next_delim];
            *remainder = &remainder[(next_delim + self.delimiter.len())..];
            return Some(until_delim);
        } else {
            return self.remainder.take();
        }
    }
}

fn until_char(s: &str, c: char) -> &str {
    let delim = &format!("{}", c);
    StrSplit::new(s, delim)
        .next()
        .expect("strsplit is always valid")
}

#[test]
fn it_works() {
    let haystack = "a b c d e";
    let letters: Vec<_> = StrSplit::new(haystack, " ").collect();
    assert_eq!(letters, vec!["a", "b", "c", "d", "e"]);
}

#[test]
fn tail() {
    let haystack = "a b c d ";
    let letters: Vec<_> = StrSplit::new(haystack, " ").collect();
    assert_eq!(letters, vec!["a", "b", "c", "d", ""]);
}

#[test]
fn until_char_test() {
    assert_eq!(until_char("hello world", 'o'), "hell");
}
```