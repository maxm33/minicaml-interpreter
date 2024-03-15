# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, it is a functional language that offers constructs for control-flow, declaring variables, functions and supports recursion.<br>

## Grammar

Every program must start with an expression `e`, from which can derive as follows:

```
e := value | Op e op e | Not e | If e then e else e | Let ide = e in e | Fun ide[16] -> e | Apply ide e[16] ; | Letrec ide ide[16] = e in e
op := + | - | * | / | & | `|` | > | < | ==
value := Int | Bool
Int := [0-9]+
Bool := true | false
ide := [a-z]+
```

As in OCaml, functions in MiniCaml are treated as values, so they can be passed as arguments to or returned from other functions, or stored in variables.<br><br>
_Note: MiniCaml syntax is flexibile when it comes to brackets, precisely ( ) and { }. You can put them wherever you want and they are not mandatory in any case, so that you can use them the way you prefer, such as delimiting guards, scope of blocks and operations, etc..._

## Usage

- Compile

```
javac Interpreter.java
```

- Run

```
java Interpreter <path-to-file>
```

Some test programs are available in `test` folder.
