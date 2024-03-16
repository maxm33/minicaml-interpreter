# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, it is a functional language that offers constructs for control-flow, declaration of variables, functions and recursive functions.<br>

## Grammar

Every program must start with an expression `e`, from which can derive as follows:

**e**&emsp;:=&emsp;**const**&ensp;|&ensp;Op **e** **op** **e**&ensp;|&ensp;Not **e**&ensp;|&ensp;If **e** then **e** else **e**&ensp;|&ensp;Let **ide** = **e** in **e**&ensp;|&ensp;Fun **ide<sub> 1</sub> ... ide<sub> 16</sub>** -> **e**&ensp;|&ensp;Apply **ide** **e<sub> 1</sub> ... e<sub> 16</sub>** ;&ensp;|&ensp;Letrec **ide** **ide<sub> 1</sub> ... ide<sub>16</sub>** = **e** in **e** <br>
**const**&emsp;:=&emsp;`[0-9]+`&ensp;|&ensp;`true`&ensp;|&ensp;`false` <br>
**op**&emsp;:=&emsp;`+`&ensp;|&ensp;`-`&ensp;|&ensp;`*`&ensp;|&ensp;`/`&ensp;|&ensp;`&`&ensp;|&ensp;`|`&ensp;|&ensp;`>`&ensp;|&ensp;`<`&ensp;|&ensp;`==`&ensp;|&ensp;`!=` <br>
**ide**&emsp;:=&emsp;`[a-z]+` <br>

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
