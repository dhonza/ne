(* ::Package:: *)

(* ::Section:: *)
(*SADE*)


populationSize=25;
radioactivity=0.1;
localRadioactivity=0.1;
mutationRate=0.5;
mutagenRate=400;
crossRate=0.3;


(* ::Text:: *)
(*Generate a random point sufficing the constraints.*)


newPoint[constraints_List]:=
{RandomReal/@constraints,Indeterminate}


(* ::Text:: *)
(*Helper function, fits x between low and high bounds.*)


fitInterval[x_,{low_,high_}]:=
If[IntervalMemberQ[Interval[{low,high}],x],x,Sequence@@Nearest[{low,high},x]]


(* ::Text:: *)
(*Helper function, forces constraints on all genomes in the population.*)


applyConstraints[pop_List, constraints_List]:=
{MapThread[fitInterval,{#1[[1]],constraints}],#1[[2]]}&/@pop


(* ::Text:: *)
(*Evaluates population.*)


evaluatePopulation[fitness_,pop_List, constraints_List]:=
Module[{},
{#1[[1]],fitness[#[[1]]]}&/@applyConstraints[pop, constraints]
]


(* ::Text:: *)
(*Helper function for select. Performs a single tournament of two random members of the population. The inferior is removed from the population.*)


singleTournament[pop_List]:=
Module[{tournament},
tournament=RandomSample[Range[Length[pop]],2];
(*Print[tournament];*)
If[pop[[tournament[[1]]]][[2]]>pop[[tournament[[2]]]][[2]],
Delete[pop,tournament[[2]]],
Delete[pop,tournament[[1]]]
]
]


(* ::Text:: *)
(*Tournament selection of populationSize individuals. This should be run on union of previous generation and its offspring.*)


select[pop_List]:=
NestWhile[singleTournament,pop,Length[#]>populationSize&]


(* ::Text:: *)
(*Prints generation statistics*)


generationInfo[pop_List]:=
Module[{sortPop},
sortPop=Sort[pop,#1[[2]]>#2[[2]]&];
If[BSF[[2]]<sortPop[[1]][[2]],BSF=sortPop[[1]]];
Print["G: ",generation," BSF: ", BSF," BOG: ",sortPop[[1]]," AVG: ",Mean[pop[[All,2]]]];
generation++;
pop
]


(* ::Text:: *)
(*Creates initial population according to constraints.*)


initialGeneration[fitness_,constraints_List]:=
Module[{},
generation=1;
BSF={{Indeterminate,Indeterminate},-Infinity};
mutagen=(#[[2]]-#[[1]])/mutagenRate&/@constraints;
generationInfo[
evaluatePopulation[fitness,
Array[newPoint[constraints]&,populationSize],
constraints]
]
]


mutate[pop_List,constraints_List]:=
Module[{g,x},
g=RandomChoice[pop][[1]];
x=newPoint[constraints][[1]];
{g+mutationRate*(x-g),Indeterminate}
]


mutateLocal[pop_List]:=
Module[{g,delta},
g=RandomChoice[pop][[1]];
delta=RandomReal[{-#,#}]&/@mutagen;
{g+delta,Indeterminate}
]


cross[pop_List]:=
Module[{g1,g2,g3},
{g1,g2,g3}=RandomSample[pop,3][[All,1]];
{g3+crossRate*(g2-g1),Indeterminate}
]


reproduce[pop_List,constraints_List]:=
Module[{toMutate,toLocalMutate,toCross},
toMutate=Floor[radioactivity*populationSize];
toLocalMutate=Floor[localRadioactivity*populationSize];
toCross=populationSize-(toMutate+toLocalMutate);
Array[mutate[pop, constraints]&,toMutate]~Join~
Array[mutateLocal[pop]&,toLocalMutate]~Join~
Array[cross[pop]&,toCross]
]


nextGeneration[fitness_, pop_List, constraints_List]:=
generationInfo[
select[pop~Join~evaluatePopulation[fitness,reproduce[pop,constraints],constraints]]
]


SADE[fitness_,constraints_List,generations_]:=
Nest[nextGeneration[fitness,#,constraints]&,initialGeneration[fitness,constraints],generations]


(* ::Input:: *)
(*constraints={{0.0,1.0},{0.0,20.0}};*)


(* ::Input:: *)
(*f1[x_List]:=Total[x];*)


(* ::Input:: *)
(*pop=initialGeneration[f1,constraints]*)


(* ::Input:: *)
(*SADE[f1,constraints,10]*)


(* ::Section:: *)
(*Parameter Search*)


dirPrefix = "/Users/drchaj1/java/test/"; 
SetDirectory[dirPrefix]; 
template = Import[StringJoin[dirPrefix, "cfg/gpat_template.properties"], "Text"]; 
ranges = ToExpression[StringCases[template, RegularExpression["({.*})"] -> "$1"]]; 
fMax = 300.;
programmName = "run " ;


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
SADE[fitness,ranges,1]
