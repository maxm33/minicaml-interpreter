# MiniCaml Interpreter

This is an interpreter that understands a simplified version of OCaml (**MiniCaml**).<br><br>
Since it is implemented in Java, every construct, type or structure (**every expression**, better said) in MiniCaml is modeled as an object.<br><br>
OCaml offers constructs for OOP and imperative programming, which is not _(yet)_ the case in MiniCaml.<br>
However, MiniCaml offers constructs for control-flow, declaration of variables, functions and recursive functions.<br>

<br>

## Grammar

Every program must start with an expression `e ;;` from which can derive as follows:

<br>

- **e**&emsp;:=&emsp;**val**&ensp;|&ensp;**ide**&ensp;|&ensp;**ListOp**&ensp;|&ensp;(**e**)&ensp;|&ensp;(**e** **bop** **e**)&ensp;|&ensp;**uop** **e**&ensp;|&ensp;**e** ;; **e**&ensp;|&ensp;(**ide** **e<sub>1</sub> ... e<sub>16</sub>**)&ensp;|&ensp;if **e** then **e** else **e**&ensp;|&ensp;let _rec_ **ide** _**ide<sub>1</sub> ... ide<sub>16</sub>**_ = **e** _in **e**_&ensp;|&ensp;function **ide<sub>1</sub> ... ide<sub>16</sub>** -> **e**<br>
- **ListOp**&emsp;:=&emsp;List.hd **e**&ensp;|&ensp;List.tl **e**&ensp;|&ensp;List.rev **e**&ensp;|&ensp;List.isEmpty **e**&ensp;|&ensp;List.length **e**&ensp;|&ensp;List.cons **e** **e**&ensp;|&ensp;List.append **e** **e**&ensp;|&ensp;List.map **e** **e**&ensp;|&ensp;List.filter **e** **e**&ensp;|&ensp;List.exists **e** **e**&ensp;|&ensp;List.forAll **e** **e**&ensp;|&ensp;List.fold **e** **e** **e**<br>
- **val**&emsp;:=&emsp;Int&ensp;|&ensp;Bool&ensp;|&ensp;Closure&ensp;|&ensp;RecursiveClosure&ensp;|&ensp;[**e<sub>0</sub> ... e<sub>n-1</sub>**]<br>
- **uop**&emsp;:=&emsp;!<br>
- **bop**&emsp;:=&emsp;**+**&ensp;|&ensp;**-**&ensp;|&ensp;\*&ensp;|&ensp;**/**&ensp;|&ensp;**&**&ensp;|&ensp;**|**&ensp;|&ensp;**>**&ensp;|&ensp;<&ensp;|&ensp;**>=**&ensp;|&ensp;<=&ensp;|&ensp;**%**&ensp;|&ensp;**^**&ensp;|&ensp;**==**&ensp;|&ensp;**!=**<br>
- **ide**&emsp;:=&emsp;Identifiers<br>

<br>

> [!TIP]
> You can refer to OCaml documentation for any doubt, since it should be almost equivalent to this lexic, syntax and semantics.

<br>

As in OCaml, functions in MiniCaml are treated as values, so they can be passed as arguments to or returned from other functions, or stored in variables/lists.<br>

<br>

> [!NOTE]
>
> - '(&ensp;&ensp;)' can surround any expression (just one), but they are also necessary to define operations and functional applications, so use them wisely.
> - 'in', 'rec' and 'ide<sub>1</sub> ... ide<sub>16</sub>' are optional in 'let' declaration, thus they are formatted in _italic_. This was done to reduce several rules to just one, lightening the grammar representation.

<br>

## Usage

- Compile

```
javac Main.java
```

- Run

```
java Main <path-to-file>
```

<br>

> [!TIP]
> Some test programs are available in `test` folder.

<br>

## Example

The compilation of the available test through the command `java Main test/test.ml` returns the following:

<br>

```
((1 + 3) * (2 - (16 / 8))) ;;

-: Int = 0

((2 > 1) & !(( !true != (4 < 6)) == (false | true))) ;;

-: Bool = false


[[(99)]] ;;

-: Lis = [[99]]

[ ] ;;

-: Lis = []


!(List.isEmpty [88]) ;;

-: Bool = true


let x = 5 ;;

-: Int = 5

[x,1,2] ;;

-: Lis = [5,1,2]

let var = (1 - 2) in (var * 6) ;;

-: Int = -6

let mul = function f y -> (f * y) in (mul 10 2) ;;

-: Int = 20


let y = 2 in
    let x = (y + 13) in
        let y = (x * y) in
            y ;;

-: Int = 30


let rec fact n =
    if (n < 2) then 1
        else (n * (fact (n - 1))) ;;

-: RecursiveClosure = <rec>

(fact 4) ;;

-: Int = 24


let f y = (x + y) ;;

-: Closure = <fun>

(f 6) ;;

-: Int = 11


let list0 = [] ;;

-: Lis = []

let list1 = [11,5,21,5,1,2] ;;

-: Lis = [11,5,21,5,1,2]

let list2 = [ 3 2 ] ;;

-: Lis = [3,2]


let pred_even = function x -> ((x % 2) == 0) ;;

-: Closure = <fun>

let doubler x = (x * 2) ;;

-: Closure = <fun>


let rec search elem lis count =
    if (List.isEmpty lis) then -1
    else let head = (List.hd lis) in
        if (head == elem) then count
        else (search elem List.tl lis (count + 1)) ;;

-: RecursiveClosure = <rec>


let sum_list = function lst ->
  let f = function x acc -> (acc + x) in
  List.fold f 0 lst ;;

-: Closure = <fun>


(sum_list list1) ;;

-: Int = 45


let sub_list lst =
  let f x acc = (acc - x) in
  List.fold f 0 lst in
  (sub_list list1) ;;

-: Int = -45


List.hd list0 ;;

-: null

List.tl list0 ;;

-: null

List.rev list0 ;;

-: Lis = []

List.length list0 ;;

-: Int = 0

List.isEmpty list0 ;;

-: Bool = true


List.hd list1 ;;

-: Int = 11

List.tl list1 ;;

-: Lis = [5,21,5,1,2]

List.rev list1 ;;

-: Lis = [2,1,5,21,5,11]

List.length list1 ;;

-: Int = 6

List.isEmpty list1 ;;

-: Bool = false


let list0 = List.cons 5 list0 ;;

-: Lis = [5]

let list0 = List.cons 6 list0 ;;

-: Lis = [6,5]


List.map doubler list1 ;;

-: Lis = [22,10,42,10,2,4]

List.filter pred_even list1 ;;

-: Lis = [2]

(List.append list1 list2) ;;

-: Lis = [11,5,21,5,1,2,3,2]


List.forAll pred_even list1 ;;

-: Bool = false

List.exists pred_even list2 ;;

-: Bool = true


(search 21 list1 0) ;;

-: Int = 2
```
