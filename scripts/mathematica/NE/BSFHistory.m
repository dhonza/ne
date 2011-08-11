(* Mathematica Package *)

BeginPackage["BSFHistory`"]
listBSFEvolution::usage = "listBSFEvolution"  
listBSF::usage = "listBSF lists only BSF (searches only in the last generation!) in the sme format as listBSFEvolution"
Begin["`Private`"] (* Begin Private Context *) 

(* Finds the genome id of the BSF. Warning it searches only the last generation! *)
findBSFId[genomes_] :=
    Sort[Flatten[genomes, 1], #1[[4]] > #2[[4]] &][[1, 1]]
 
traceBackIds[parentId_Integer, genomes_] :=
    Module[ {p, n, i, l = {}},
        n = parentId;
        For[i = Length[genomes], i >= 1, i--,
         p = Cases[genomes[[i]], {n, _, _, _}];
         If[ p =!= {},
             p = Sequence @@ p;
             PrependTo[l, {i}~Join~p];
             n = p[[2]];,
             Null
         ];
         ];
        l
    ]
  
removeDuplicit[ids_] :=
    Module[ {i, l = {}, it},
        it = ids[[1]];
        For[i = 2, i <= Length[ids], i++,
         (*Print[all[[i,2]]," ",all[[i-1,2]]];*)
         
         If[ ids[[i, 4]] =!= ids[[i - 1, 4]],
             AppendTo[l, it];
             it = ids[[i]],
             Null
         ]
         ];
        AppendTo[l, it];
        l
    ]
  
removeDuplicitStructural[ids_] :=
    Module[ {i, l = {}, it, bsf},
        it = ids[[1]];
        For[i = 2, i <= Length[ids], i++,
         (*Print[all[[i,2]]," ",all[[i-1,2]]];*)
         
         If[ (ids[[i, 4]] /. _?NumberQ -> C) =!= (ids[[i - 1, 
              4]] /. _?NumberQ -> C),
             AppendTo[l, it];
             it = ids[[i]],
             Null
         ];
         If[ ids[[i, 4]] =!= ids[[i - 1, 4]],
             bsf = ids[[i]],
             Null
         ]
         ];
        AppendTo[l, it];
        If[ bsf != it,
            AppendTo[l, bsf]
        ];
        l
    ]
  
onlyNBiggestChanges[ids_, N_] :=
    ids[[Sort[
      Ordering[
       Abs[#[[1]] - #[[2]]] & /@ Partition[ids[[All, 5]], 2, 1], -N]]]]

Options[listBSFEvolution] = {Choice -> "Structural"};    
listBSFEvolution[fileName_,OptionsPattern[]] :=
    Module[ {genomes,rules,bsf},
        genomes = ToExpression@StringSplit[Import[fileName], "\n"];
        (* Convert all our heads to strings *)
        rules = # -> ToString[#] & /@ {Global`times, Global`plus, Global`sin, Global`cos, Global`atan, Global`gauss};
        genomes = genomes /. rules;
        bsf = traceBackIds[findBSFId[genomes],genomes];
        Switch[OptionValue[Choice],
        	"NBiggest",onlyNBiggestChanges[bsf,15],
        	"Changing",removeDuplicit[bsf],
        	"Structural",removeDuplicitStructural[bsf],
        	"All",bsf,
        	_,$Failed
        ]
            ]

listBSF[fileName_] :=
	Module[{rules},
		rules = # -> ToString[#] & /@ {Global`times, Global`plus, Global`sin, Global`cos, Global`atan, Global`gauss};
		{"last"}~Join~(Sort[
			ToExpression@
			(StringSplit[Import[fileName], "\n"][[-1]]), #1[[4]] > #2[[4]] &][[1]]) /. rules
	]
End[] (* End Private Context *)

EndPackage[]