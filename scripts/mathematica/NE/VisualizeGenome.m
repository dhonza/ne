(* Mathematica Package *)

BeginPackage["VisualizeGenome`",{"BSFHistory`"}] 
convertGPATGenome::usage = "convertGPATGenome converts GPAT genome to Mathematica Expression."
sortForest::usage = "sortForest sorts children of each node by their string representation."
forestAsTreeForm::usage = "forestAsTreeForm converts a forest to TreeFrom, constants are replaced by ones"
forestAsTreeFormSimplified::usage = "forestAsTreeFormSimplified same as forestAsTreeForm but simplifies forest and the sorts it by sortForest"
showGenomes::usage = "showGenomes shows output of BSFHistory`listBSFEvolution"
showAsExpressions::usage = "showAsExpressions shows output of BSFHistory`listBSFEvolution"

Begin["`Private`"] (* Begin Private Context *) 

convertGPATGenome[forest_List] :=
    Module[ {rules,zeroRules},
        rules = {"times"[x__] :> Times[x], "plus"[x__] :> Plus[x], 
        "atan"[x__] :> ArcTan[Plus[x]], "gauss"[x__] :> Exp[-(Plus[x])^2], 
        "sin"[x__] :> Sin[Plus[x]]};
        zeroRules = {"times" -> 0, "plus" -> 0, "atan" -> 0, "gauss" -> 0, "sin" -> 0};
        (# //. rules /. zeroRules)& /@ forest
    ]
    
forestAsTreeForm[forest_List] :=
    TreeForm[# /. {_?NumberQ -> 1, "plus" -> "+", "times" -> "*"}, ImageSize -> 400] & /@ forest

forestAsTreeFormSimplified[forest_List] :=
    Module[ {simplified},
        simplified = 
         sortForest[(# /. {_?NumberQ -> 1, "plus" -> "+", 
               "times" -> "*"} //.
               {
                       "*"[a:_] -> a,
                       "+"[a:_] -> a,
                       "*"[x:__, 1, y:___] -> "*"[x, y],
                       "*"[x:___, 1, y:__] -> "*"[x, y],                                         
                       "+"[a:___,"+"[b:___],c:___] -> "+"[a,b,c],
                       "*"[a:___,"*"[b:___],c:___] -> "*"[a,b,c],
                       f : ("+"|"atan"|"sin"|"cos"|"gauss")[a : ___, b : _, c : ___, b : _, d : ___] :> Head[f][a, b, c, d]
               }) & /@ forest];
        TreeForm[#, ImageSize -> 400] & /@ simplified
    ]
  
sortForest[forest_List] :=
    Map[ReplaceAll[#, f : _[x : __] :> Sort[f]] &, forest, Infinity]

showGenomes[evolution_List] :=
    Module[ {shortNames},
        shortNames = {"atan" -> "A", "sin" -> "S", "cos" -> "C", "gauss" -> "G"};
        Grid[
         {Style[#, Bold, 20] & /@ {"Gen", "Genome", "Sorted", "Simplified", 
             "f"}}~Join~({
              #[[1]],
              Grid[{forestAsTreeForm[#[[4]]]/.shortNames}],
              Grid[{forestAsTreeForm[sortForest[#[[4]]]]/.shortNames}],
              Grid[{forestAsTreeFormSimplified[#[[4]]]/.shortNames}],
              #[[5]]
              } & /@ evolution),
         Frame -> All
         ]
    ]
    
showAsExpressions[evolution_List] :=
    Module[ {},
        Grid[
         {Style[#, Bold, 20] & /@ {"Gen", "Expression", "Expanded","f"}}~Join~
		({#[[1]],convertGPATGenome[#[[4]]],Expand[convertGPATGenome[#[[4]]]],#[[5]]}& /@ evolution),
        Frame -> All
        ]
    ]
    
End[] (* End Private Context *)

EndPackage[]