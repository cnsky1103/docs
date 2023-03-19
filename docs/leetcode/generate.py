import os

current_file_path = os.path.abspath(__file__)
current_dir_path = os.path.dirname(current_file_path)
code_path =  current_dir_path + os.sep + "code"

files = os.listdir(code_path)

for file in files:
    num = file.split('.')[0]
    lang = file.split('.')[-1]
    
    doc_file = current_dir_path + os.sep + num + ".md"
    
    if not os.path.exists(doc_file):
        code = open(code_path + os.sep + file, 'r').read()
        # create file if it doesn't exist
        with open(doc_file, 'w') as f:
            f.write(
f"""---
layout: default
title: {num}
parent: Leetcode
nav_order: {num}
---

# {num}

```{lang}
{code}
```
""")