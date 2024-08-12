# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, MiniCaml offers constructs for control-flow, declaration of variables, functions and recursive functions.<br>

## Grammar

Every program must start with an expression `e ;;` from which can derive as follows:

- **e**&emsp;:=&emsp;**val**&ensp;|&ensp;**ide**&ensp;|&ensp;**ListOp**&ensp;|&ensp;(**e**)&ensp;|&ensp;(**e** **bop** **e**)&ensp;|&ensp;**uop** **e**&ensp;|&ensp;**e** ;; **e**&ensp;|&ensp;(**ide** **e<sub>1</sub> ... e<sub>16</sub>**)&ensp;|&ensp;if **e** then **e** else **e**&ensp;|&ensp;let _rec_ **ide** _**ide<sub>1</sub> ... ide<sub>16</sub>**_ = **e** _in **e**_&ensp;|&ensp;function **ide<sub>1</sub> ... ide<sub>16</sub>** -> **e**<br>
- **ListOp**&emsp;:=&emsp;List.hd **e**&ensp;|&ensp;List.tl **e**&ensp;|&ensp;List.rev **e**&ensp;|&ensp;List.isEmpty **e**&ensp;|&ensp;List.length **e**&ensp;|&ensp;List.cons **e** **e**&ensp;|&ensp;List.append **e** **e**&ensp;|&ensp;List.map **e** **e**&ensp;|&ensp;List.filter **e** **e**&ensp;|&ensp;List.exists **e** **e**&ensp;|&ensp;List.forAll **e** **e**&ensp;|&ensp;List.fold **e** **e** **e**<br>
- **val**&emsp;:=&emsp;Int&ensp;|&ensp;Bool&ensp;|&ensp;Closure&ensp;|&ensp;RecursiveClosure&ensp;|&ensp;[**e<sub>0</sub> ... e<sub>n-1</sub>**]<br>
- **uop**&emsp;:=&emsp;!<br>
- **bop**&emsp;:=&emsp;**+**&ensp;|&ensp;**-**&ensp;|&ensp;\*&ensp;|&ensp;**/**&ensp;|&ensp;**&**&ensp;|&ensp;**|**&ensp;|&ensp;**>**&ensp;|&ensp;<&ensp;|&ensp;**>=**&ensp;|&ensp;<=&ensp;|&ensp;**%**&ensp;|&ensp;**^**&ensp;|&ensp;**==**&ensp;|&ensp;**!=**<br>
- **ide**&emsp;:=&emsp;Identifiers<br>

> [!TIP]
> You can refer to OCaml documentation for any doubt, since it should be almost equivalent to this lexic, syntax and semantics.

As in OCaml, functions in MiniCaml are treated as values, so they can be passed as arguments to or returned from other functions, or stored in variables/lists.<br>

> [!NOTE]
>
> - '(&ensp;&ensp;)' can surround any expression (just one), but they are also necessary to define operations and functional applications, so use them wisely.
> - 'in', 'rec' and 'ide<sub>1</sub> ... ide<sub>16</sub>' are optional in 'let' declaration, thus they are formatted in _italic_. This was done to reduce several rules to just one, lightening the grammar representation.

## Usage

- Compile

```
javac Main.java
```

- Run

```
java Main <path-to-file>
```

> [!TIP]
> Some test programs are available in `test` folder.
