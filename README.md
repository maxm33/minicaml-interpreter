# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, MiniCaml offers constructs for control-flow, declaration of variables, functions and recursive functions.<br>

## Grammar

Every program must start with an expression `e ;;` from which can derive as follows:

**e**&emsp;:=&emsp;**const**&ensp;|&ensp;**ide**&ensp;|&ensp;**e** ;; **e**&ensp;|&ensp;op **e** **symbol** **e**&ensp;|&ensp;! **e**&ensp;|&ensp;if **e** then **e** else **e**&ensp;|&ensp;apply **ide** **e<sub>1</sub> ... e<sub>16</sub>** ;&ensp;|&ensp;let **ide** = **e**&ensp;|&ensp;let **ide** = **e** in **e**&ensp;|&ensp;letrec **ide** **ide<sub>1</sub> ... ide<sub>16</sub>** = **e** in **e**&ensp;|&ensp;List.add **e** **e**&ensp;|&ensp;List.remove **e**&ensp;|&ensp;List.head **e**&ensp;|&ensp;List.isEmpty **e**&ensp;|&ensp;List.length **e**<br>
**const**&emsp;:=&emsp;_Int_&ensp;|&ensp;_Bool_&ensp;|&ensp;function **ide<sub>1</sub> ... ide<sub>16</sub>** -> **e**&ensp;|&ensp;letrec **ide** **ide<sub>1</sub> ... ide<sub>16</sub>** = **e**&ensp;|&ensp;[ **e<sub>0</sub> ... e<sub>n-1</sub>** ]<br>
**symbol**&emsp;:=&emsp;**+**&ensp;|&ensp;**-**&ensp;|&ensp;\*&ensp;|&ensp;**/**&ensp;|&ensp;**&**&ensp;|&ensp;**|**&ensp;|&ensp;**>**&ensp;|&ensp;<&ensp;|&ensp;**>=**&ensp;|&ensp;<=&ensp;|&ensp;**==**&ensp;|&ensp;**!=**<br>
**ide**&emsp;:=&emsp;_Identifiers_<br>

As in OCaml, functions in MiniCaml are treated as values, so they can be passed as arguments to or returned from other functions, or stored in variables/lists.<br>

## Usage

- Compile

```
javac Interpreter.java
```

- Run

```
java Interpreter <path-to-file>
```

> [!TIP]
> Some test programs are available in `test` folder.
