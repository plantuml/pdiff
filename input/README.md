# Diagram Integration

*This is a proposal for integrating diagrams into our pdiff database*

You can contribute by uploading your `.puml` files into the `input` folder through a Pull Request.

Upon submission, a workflow will be triggered by GitHub to automatically process and integrate the diagrams from the `.puml` files into the pdiff database.


You have the option to include a YAML header in your diagram for additional metadata. Here’s an example of how to format it:

```
@startuml
---
author: foo
---
Alice -> Bob : hello
@enduml
```

Use this opportunity to enhance your contributions and help expand our diagram repository!

