(* ::Package:: *)

(* ::Section:: *)
(*xNES(Exponential NES)*)


(* ::Text:: *)
(*Author: Jan Koutn\[IAcute]k, IDSIA*)
(*Reference:  Tobias Glasmachers, Tom Schaul, Yi Sun, Daan Wierstra and J\[UDoubleDot]rgen Schmidhuber. Exponential Natural Evolution Strategies. Proceedings of the Genetic and Evolutionary Computation Conference (GECCO - 2010, Portland)*)


utilityFunction[n_] := utilityFunction[n] = 
   Reverse[N[(Max[0, #1] & ) /@ (Log[n/2 - 1] - Log[Range[n]])/
       Total[(Max[0, #1] & ) /@ (Log[n/2 - 1] - Log[Range[n]])] - 1/n]]


learningRateXNES[d_] := N[3*((3 + Log[d])/(5*d*Sqrt[d]))]


populationSize[d_] := 4 + Floor[3*Log[d]]


xNESstep[f_, dim_, \[Mu]A_, \[Lambda]_, \[Eta]\[Mu]_, \[Eta]A_] := 
  Module[{\[Mu], \[Sigma], z, x, s, u, g\[Mu], g\[Sigma], gA, A, expA, o}, 
   {\[Mu], A} = \[Mu]A; z = RandomReal[NormalDistribution[0, 1], {dim, \[Lambda]}]; 
    expA = N[MatrixExp[A]]; x = \[Mu] + expA . z; o = Ordering[f /@ Transpose[x]]; 
    z = z[[All,o]]; u = utilityFunction[\[Lambda]]; g\[Mu] = z . u; 
    gA = Plus @@ MapIndexed[#1*(Outer[Times, z[[All,#2[[1]]]], z[[All,#2[[1]]]]] - 
          IdentityMatrix[dim]) & , u]; {\[Mu] + \[Eta]\[Mu]*expA . g\[Mu], A + (\[Eta]A/2)*gA}]


xNES[f_, dim_, \[Mu]A_, \[Lambda]_, \[Eta]\[Mu]_, \[Eta]A_, nIter_] := 
  Nest[xNESstep[f, dim, #1, \[Lambda], \[Eta]\[Mu], \[Eta]A] & , \[Mu]A, nIter]


(* ::Section:: *)
(*Parameter Search*)


dirPrefix = "/Users/drchaj1/java/test/"; 
SetDirectory[dirPrefix]; 
template = Import[StringJoin[dirPrefix, "cfg/gp_template.properties"], "Text"]; 
ranges = ToExpression[StringCases[template, RegularExpression["({.*})"] -> "$1"]]; 
fMax = 300.;
programmName = "runGP " ;


penalty[x_List] := Module[{}, 
   Total[MapThread[If[IntervalMemberQ[Interval[#1], #2], 0., 
       Abs[Sequence @@ Nearest[#1, #2] - #2]] & , {ranges, x}]]]


constrain[x_List] := Module[{}, MapThread[If[IntervalMemberQ[Interval[#1], #2], #2, 
      Sequence @@ Nearest[#1, #2]] & , {ranges, x}]]


createConfig[x_List] := Module[{}, Export[StringJoin[dirPrefix, "exp", ToString[cnt], 
     "/cfg.properties"], StringReplacePart[template, ToString /@ Round[x, 0.0001], 
     StringPosition[template, RegularExpression["{.*}"]]], "Text"]]


fitness[{(x__)?NumericQ}] := Module[{fos, ps, ps2, result, fval, p}, 
   CreateDirectory[StringJoin[dirPrefix, "exp", ToString[cnt]]]; 
    createConfig[constrain[{x}]]; Run[StringJoin[dirPrefix, programmName, ToString[cnt]]]; 
    result = Import[StringJoin[dirPrefix, "exp", ToString[cnt], 
       "/experiments_overall.txt"]]; 
    fval = fMax - ToExpression[StringCases[result, 
         RegularExpression["GENERATIONS Mean:(.*) Std\\.Dev\\.\\(SQRT\\)"] -> "$1"][[
        1]]]; p = penalty[{x}]; If[p > 0, fval = fval - 10*p]; 
    Print[{cnt, {x}, fval}]; If[bsf[[1]] < fval, bsf = {fval, x}]; cnt = cnt + 1; 
    fval]


(* ::Section:: *)
(*Experiments*)


cnt = 1; 
bsf = {-Infinity}; 

(*
NMaximize[{fitness[{m, n}],
ranges[[1]][[1]]< m < ranges[[1]][[2]] 
&& ranges[[2]][[1]] < n < ranges[[2]][[2]]}, {m, n}, 
  EvaluationMonitor :> Print[{m, n}], Method -> "DifferentialEvolution"]
*)

NMaximize[{fitness[{m, n,o}], 
ranges[[1]][[1]]< m < ranges[[1]][[2]] 
&& ranges[[2]][[1]] < n < ranges[[2]][[2]]
&& ranges[[3]][[1]] < o < ranges[[3]][[2]]}, {m, n,o}, 
  EvaluationMonitor :> Print[{m, n,o}], Method -> "DifferentialEvolution"]
