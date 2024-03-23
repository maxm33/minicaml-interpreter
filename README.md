# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, MiniCaml offers constructs for control-flow, declaration of variables, functions and recursive functions.<br>

## Grammar

Every program must start with an expression `e ;;` from which can derive as follows:

- **e**&emsp;:=&emsp;**val**&ensp;|&ensp;**ide**&ensp;|&ensp;**ListOp**&ensp;|&ensp;(**e**)&ensp;|&ensp;(**e** **op** **e**)&ensp;|&ensp;(**ide** **e<sub>1</sub> ... e<sub>16</sub>**)&ensp;|&ensp;**e** ;; **e**&ensp;|&ensp;!**e**&ensp;|&ensp;if **e** then **e** else **e**&ensp;|&ensp;let **ide** = **e**&ensp;|&ensp;let **ide** = **e** in **e**&ensp;|&ensp;let rec **ide** **ide<sub>1</sub> ... ide<sub>16</sub>** = **e** in **e**<br>
- **ListOp**&emsp;:=&emsp;List.cons **e** **e**&ensp;|&ensp;List.hd **e**&ensp;|&ensp;List.tl **e**&ensp;|&ensp;List.isEmpty **e**&ensp;|&ensp;List.length **e**&ensp;|&ensp;List.append **e** **e**&ensp;|&ensp;List.map **e** **e**&ensp;|&ensp;List.filter **e** **e**&ensp;|&ensp;List.exists **e** **e**&ensp;|&ensp;List.forAll **e** **e**&ensp;|&ensp;List.rev **e**<br>
- **val**&emsp;:=&emsp;_Int_&ensp;|&ensp;_Bool_&ensp;|&ensp;function **ide<sub>1</sub> ... ide<sub>16</sub>** -> **e**&ensp;|&ensp;let rec **ide** **ide<sub>1</sub> ... ide<sub>16</sub>** = **e**&ensp;|&ensp;[**e<sub>0</sub> ... e<sub>n-1</sub>**]<br>
- **op**&emsp;:=&emsp;**+**&ensp;|&ensp;**-**&ensp;|&ensp;\*&ensp;|&ensp;**/**&ensp;|&ensp;**&**&ensp;|&ensp;**|**&ensp;|&ensp;**>**&ensp;|&ensp;<&ensp;|&ensp;**>=**&ensp;|&ensp;<=&ensp;|&ensp;**%**&ensp;|&ensp;**^**&ensp;|&ensp;**==**&ensp;|&ensp;**!=**<br>
- **ide**&emsp;:=&emsp;_Identifiers_<br>

> [!TIP]
> You can refer to OCaml documentation for any doubt, since it should be almost equivalent to this lexic, syntax and semantics.

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
