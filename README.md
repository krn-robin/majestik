Majestik - a Magik implementation
=================================

Majestik is an attempt to implement the Magik programming language[^1], using Magik-Tools[^2] and Java's Class-File API[^3].
It is currently in the very early stages of development and lacks most features.

Components

----------
Majestik is composed of two core components:
2. Compiler: This component translates the AST produced by Magik-Tools' Magik-Squid into JVM bytecode using the Class-File API.

   Currently, only supports literal types `string`, `integer` and `float`; as well as the `invoke` expressions.
3. Runtime: provides a standard library with basic functionality to run a Magik program.

   As of now, only the `write` variable in the `sw` package has a barebones implementation.

Features

-------
In the project's current state, it is possible (and pretty much limited just to) run a Magik "Hello World" example program[^4].

```magik
_block
  world << "World!"
  write("Hello")
  write(world)

  int << 12345
  write(int)

  flt << 5.4321
  write(flt)
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

* Q: Why does Majestik require JDK 23?
  A: Majestik uses the Class-File API[^3], which was introduced as a preview feature in Java 23.

* Q: Can generated class files be used in a (Smallworld) Magik session?
  A: Yes, they can be used in a Magik session, provided the Magik session is started with JDK 23 and preview features are enabled.

[^1]: [Magik (programming language) on Wikipedia](https://en.wikipedia.org/wiki/Magik_(programming_language))
[^2]: [Magik-Tools](https://github.com/StevenLooman/magik-tools)
[^3]: [Class-File API](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/lang/classfile/package-summary.html)
[^4]: [Magik Hello World example](https://en.wikipedia.org/wiki/Magik_(programming_language)#Hello_World_example)
