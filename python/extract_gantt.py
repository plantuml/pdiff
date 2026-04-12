"""
Extract all .puml files containing @startgantt from C:\\github\\pdiff\\db
and copy them (flat, no subdirectories) into C:\\github\\pdiff\\python\\output.

The JSON header before @startgantt is replaced by a fixed YAML header.
"""

import os
import re

DB_DIR = r"C:\github\pdiff\db"
OUTPUT_DIR = r"C:\github\pdiff\python\output"

YAML_HEADER = """\
---
output: svg
expected-description: (Gantt)
expected-status: 0
---
"""


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    count_scanned = 0
    count_copied = 0

    for dirpath, _dirnames, filenames in os.walk(DB_DIR):
        for filename in filenames:
            if not filename.endswith(".puml"):
                continue

            count_scanned += 1
            src = os.path.join(dirpath, filename)

            try:
                with open(src, encoding="utf-8", errors="replace") as f:
                    content = f.read()
            except Exception as e:
                print(f"  [ERROR] Cannot read {src}: {e}")
                continue

            if "@startgantt" not in content:
                continue

            # Strip everything before @startgantt and prepend the YAML header
            idx = content.index("@startgantt")
            body = content[idx:]
            new_content = YAML_HEADER + body

            dst = os.path.join(OUTPUT_DIR, filename)
            with open(dst, "w", encoding="utf-8") as f:
                f.write(new_content)
            count_copied += 1

    print(f"Scanned {count_scanned} .puml files, copied {count_copied} Gantt files to {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
