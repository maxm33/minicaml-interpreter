((1 + 3) * (2 - (16 / 8))) ;;
((2 > 1) & !(( !true != (4 < 6)) == (false | true))) ;;

[[(99)]] ;;
[ ] ;;

!(List.isEmpty [88]) ;;

let x = 5 ;;
[x,1,2] ;;
let var = (1 - 2) in (var * 6) ;;
let mul = function f y -> (f * y) in (mul 10 2) ;;

let y = 2 in
    let x = (y + 13) in
        let y = (x * y) in
            y ;;

let rec fact n = 
    if (n < 2) then 1 
	else (n * (fact (n - 1))) ;;
(fact 4) ;;

let f y = (x + y) ;;
(f 6) ;;

let list0 = [] ;;
let list1 = [11,5,21,5,1,2] ;;
let list2 = [ 3 2 ] ;;

let pred_even = function x -> ((x % 2) == 0) ;;
let doubler x = (x * 2) ;;

let rec search elem lis count =
    if (List.isEmpty lis) then -1
    else let head = (List.hd lis) in
        if (head == elem) then count
        else (search elem List.tl lis (count + 1)) ;;

let sum_list = function lst ->
  let f = function x acc -> (acc + x) in
  List.fold f 0 lst ;;

(sum_list list1) ;;

let sub_list lst =
  let f x acc = (acc - x) in
  List.fold f 0 lst in
  (sub_list list1) ;;

List.hd list0 ;;
List.tl list0 ;;
List.rev list0 ;;
List.length list0 ;;
List.isEmpty list0 ;;

List.hd list1 ;;
List.tl list1 ;;
List.rev list1 ;;
List.length list1 ;;
List.isEmpty list1 ;;

let list0 = List.cons 5 list0 ;;
let list0 = List.cons 6 list0 ;;

List.map doubler list1 ;;
List.filter pred_even list1 ;;
(List.append list1 list2) ;;

List.forAll pred_even list1 ;;
List.exists pred_even list2 ;;

(search 21 list1 0) ;;