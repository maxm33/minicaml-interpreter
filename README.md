# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
As the language used to implement it is Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers ways to work with objects and the imperative paradigm, which is not _(yet)_ possible in MiniCaml.<br>
However, it is possible to control-flow, declare variables, functions and even recursive functions.<br>

## Grammar of MiniCaml

Every program must start with an expression `e`, from which you can derive as follows:

```
e := value | Op e op e | Not e | If e then e else e | Let ide = e in e | Fun ide -> e | Apply ide e | Letrec ide ide = e in e
op := + | - | * | / | & | | | > | < | ==
value := Int | Bool
Int := [0-9]+
Bool := true | false
ide := [a-z]+
```

As in OCaml, functions in MiniCaml are treated as values (like integers and booleans) so you can use them as arguments to other functions, return them from functions or store them in variables.<br><br>
_Note: MiniCaml syntax is flexibile when it comes to brackets, precisely ( ) and { }. You can put them wherever you want and they are not mandatory in any case, so that you can use them the way you prefer, such as defining guards, scope of blocks, operations, etc..._

## Usage

* Compile

```
javac Interpreter.java
```

* Run

```
java Interpreter <path-to-file>
```

<br><br>There is a test you can run using

```
java Interpreter test/test.javaml
```

which will calculate factorial of 4.<br>

_Note: It is not mandatory to pass a .javaml file, the program can be read from any type of file._
