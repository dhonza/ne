(* Mathematica Package *)

BeginPackage["MazeNavigation`",{"ExperimentEvaluation`"}]

loadMap::usage = "loadMap"
showState::usage = "showState"
simulateToTarget::usage = "simulateToTarget"
inputVector::usage = "inputVector"
singleStep::usage = "singleStep"
rotateR::usage = "rotateR"
rotateL::usage = "rotateL"
moveF::usage = "moveF"

Begin["`Private`"] (* Begin Private Context *)
 
neighbours[state_] :=
    Module[ {pos = OptionValue[state, pos], map = OptionValue[state, map],
       dir = OptionValue[state, dir], nr},
        nr = map[[pos[[1]] - 1 ;; pos[[1]] + 1, 
          pos[[2]] - 1 ;; pos[[2]] + 1]];
        Switch[dir,
         "N", nr,
         "W", {{nr[[3, 1]], nr[[2, 1]], nr[[1, 1]]}, {nr[[3, 2]], 
           nr[[2, 2]], nr[[1, 2]]}, {nr[[3, 3]], nr[[2, 3]], nr[[1, 3]]}},
         "E", {{nr[[1, 3]], nr[[2, 3]], nr[[3, 3]]}, {nr[[1, 2]], 
           nr[[2, 2]], nr[[3, 2]]}, {nr[[1, 1]], nr[[1, 2]], nr[[1, 3]]}},
         "S", {{nr[[3, 3]], nr[[3, 2]], nr[[3, 1]]}, {nr[[2, 3]], 
           nr[[2, 2]], nr[[2, 1]]}, {nr[[1, 3]], nr[[1, 2]], nr[[1, 1]]}}
         ]
    ]

rotateR[state_] :=
    Module[ {dirs = {"N", "E", "S", "W"}},
        (state /. (dir -> _) ->
            (dir -> (RotateLeft[dirs])[[
               Position[dirs, OptionValue[state, dir]][[1, 1]]]]))
    ]

rotateL[state_] :=
    Module[ {dirs = {"N", "E", "S", "W"}},
        (state /. (dir -> _) ->
            (dir -> (RotateRight[dirs])[[
               Position[dirs, OptionValue[state, dir]][[1, 1]]]]))
    ]

moveF[state_] :=
    Module[ {p = OptionValue[state, pos], dir = OptionValue[state, dir], newPos},
        newPos = Switch[dir,
          "N", {p[[1]] - 1, p[[2]]},
          "W", {p[[1]], p[[2]] - 1},
          "S", {p[[1]] + 1, p[[2]]},
          "E", {p[[1]], p[[2]] + 1}
          ];
        If[ neighbours[state][[1, 2]] != 1,
            state /. {(pos -> _) -> (pos -> newPos), (route -> old : _) -> (route -> (old~Join~{newPos}))},
            state
        ]
    ]

inputVector[state_] :=
    Module[ {pos = OptionValue[state, pos], 
      target = OptionValue[state, target],
       dir = OptionValue[state, dir], n, slice},
        n = neighbours[state];
        Switch[dir,
         "N", slice = {pos[[1]] >= target[[1]], pos[[1]] <= target[[1]], 
           pos[[2]] >= target[[2]], pos[[2]] <= target[[2]]},
         "S", slice = {pos[[1]] <= target[[1]], pos[[1]] >= target[[1]], 
           pos[[2]] <= target[[2]], pos[[2]] >= target[[2]]},
         "W", slice = {pos[[2]] >= target[[2]], pos[[2]] <= target[[2]], 
           pos[[1]] <= target[[1]], pos[[1]] >= target[[1]]},
         "E", slice = {pos[[2]] <= target[[2]], pos[[2]] >= target[[2]], 
           pos[[1]] >= target[[1]], pos[[1]] <= target[[1]]}
         ];
        {Global`x0 -> If[ n[[1, 1]] == 1,
                          1.0,
                          0.0
                      ],
         Global`x1 -> If[ n[[1, 2]] == 1,
                          1.0,
                          0.0
                      ],
         Global`x2 -> If[ n[[1, 3]] == 1,
                          1.0,
                          0.0
                      ],
         Global`x3 -> (slice[[1]] /. {True -> 1.0, False -> 0.0}),
         Global`x4 -> (slice[[2]] /. {True -> 1.0, False -> 0.0}),
         Global`x5 -> (slice[[3]] /. {True -> 1.0, False -> 0.0}),
         Global`x6 -> (slice[[4]] /. {True -> 1.0, False -> 0.0})
         }
    ]

loadMap[fileName_] :=
    Module[ {m},
        m = Import[fileName];
        {pos -> Flatten[Position[m, 2]], 
        target -> Flatten[Position[m, 3]], dir -> "S", map -> m, 
        route -> {Flatten[Position[m, 2]]}}
    ]

showState[state_] :=
    Module[ {pos = OptionValue[state, pos], map = OptionValue[state, map],
       dir = OptionValue[state, dir], route = OptionValue[state, route], 
      p},
        route = {#[[2]], Length[map] - #[[1]] + 1} & /@ route;
        p = {pos[[2]], Length[map] - pos[[1]] + 1};
        Show[
         ArrayPlot[map, 
          ColorRules -> {0 -> White, 1 -> Black, 2 -> Yellow, 3 -> Red}, 
          DataRange -> {{1, Length[map]}, {1, Length[map]}}, 
          PlotRangePadding -> None, Frame -> False, ImagePadding -> None, 
          PlotRangeClipping -> True, Mesh -> True],
         Graphics[{Thick,Line[route], Disk[p, 0.4]}]
         ]
    ]

simulateToTarget[state_List, expr_, maxSteps_] :=
    Module[ {target = OptionValue[state, target]},
        showState[
         NestWhile[singleStep[#, expr] &, state, 
          OptionValue[#, pos] != target &, 1, maxSteps]]
    ]

singleStep[state_,exp_List] :=
    Module[ {i,o},
        i = inputVector[state];
        (*Print[i];*)
        o = exp /. i; (*all outputs in a list*)
        (*Print[o];*)
        Piecewise[{{moveF@rotateL@state,o[[1]] < -0.5},{moveF@rotateR@state,o[[1]] > 0.5}},moveF@state]
    ] 
    
End[] (* End Private Context *)

EndPackage[]