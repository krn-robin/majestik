Majestik - a Magik implementation
=================================

Majestik is an attempt to implement the Magik programming language[^1], using ANTLR4[^2] and Java's Class-File API[^3].
It is currently in the very early stages of development and lacks most features.



Components
----------
Majestik is composed of three core components:
1. Lexer/Parser: This component parses the Magik source code and constructs an Abstract Syntax Tree (AST).

   Currently, the ANTLR4 grammar only understands the literal type `string`; the expression `invoke` and `block` statements, as well as simple assignments (`<<`).  All other tokens are ignored.
3. Compiler: This component translates the AST into JVM bytecode using the Class-File API.

   Currently, only supports literal type `string`, and `invoke` expressions.
4. Runtime: provides a standard library with basic functionality to run a Magik program.

   As of now, only the `write` variable in the `sw` package has a barebones implementation.



Features
-------
In the project's current state, it is possible (and pretty much limited just to) run a Magik "Hello World" example program[^4].
```magik
_block
   world << "World!"
   write("Hello ")
   write(world)
_endblock
```



Compiling
---------
```bash
mvn package
```



Running
-------
```bash
java --enable-preview -jar target/majestik-0.0.0-SNAPSHOT.jar helloworld.magik
```



FAQ
---
* Q: Why does Majestik require JDK 22?
  A: Majestik uses the Class-File API[^3], which was introduced as a preview feature in Java 22.

* Q: Can generated class files be used in a (Smallworld) Magik session?
  A: Yes, they can be used in a Magik session, provided the Magik session is started with JDK 22 with preview features enabled.



[^1]: [Magik (programming language) on Wikipedia](https://en.wikipedia.org/wiki/Magik_(programming_language))
[^2]: [ANTLR4](https://www.antlr.org)
[^3]: [Class-File API](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/classfile/package-summary.html)
[^4]: [Magik Hello World example](https://en.wikipedia.org/wiki/Magik_(programming_language)#Hello_World_example)
