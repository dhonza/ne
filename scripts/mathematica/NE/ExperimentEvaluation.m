(* Mathematica Package *)

BeginPackage["ExperimentEvaluation`",{"BSFHistory`","VisualizeGenome`"}]
listAllFiles::usage = "listAllExperimentFiles[{dir1, dir2, ...}] gives a list of items in a form {experiments_AAA.txt,parameters_AAA.txt}."
readAllFiles::usage = "readAllFiles[{dir1, dir2, ...},{labelForConfig1, ...}] reads all data for each configuration and assigns labels.
	It gives a list of items in a form: 
	{labelForConfig1,
     experiments_AAA.txt,
     parameters_AAA.txt,
    {statName1\[Rule]{valueExp1, valueExp2, ...}, ...},
    {paramName1\[Rule]paramValue1, paramName2\[Rule]paramValue2, ...}}.
     If labels are not given assigns numbers."
saveData::usage = "saveData"
keepOnlyBest::usage = "keepOnlyBest"

changingParameters::usage = "changingParameters"
sortDataByParams::usage = "sortDataByParams[data,paramOrder] sorts configuration in data by given parameters, paramOrder is a 
	list of parameter names."
sortConfigurationResults::usage = "sortConfigurationResults"

printAsTable::usage = "printAsTable"
plotBooleanAsBarChart::usage = "plotBooleanAsBarChart"
plotAsBoxWhiskerChart::usage = "plotAsBoxWhiskerChart"
plotAsHistograms::usage = "plotAsSmoothHistograms"

testMannWhitney::usage = "testMannWhitney[data,label1,label2,statName] computes Mann-Whitney U test on a single stat (given by name) 
of two configurations (given by labels)). This non-parametric test compares whether two independent samples of observations have 
equally large values. Test homoscedasticity, see "

testKruskalWallis::usage = "testKruskalWallis"

testFisherExact::usage = "testFisherExact"
testFisherExactAll::usage = "testFisherExactAll"

readExperimentFile::usage = "readExperimentFile[fileName]"
readParameterFile::usage = "readParameterFile[fileName]"  

Begin["`Private`"] (* Begin Private Context *) 

PREFIX = "/Users/drchaj1/java/exp/";
idxLABEL = 1;
idxEFILE = 2;
idxPFILE = 3;
idxRESULTS = 4;
idxPARAMS = 5;

(* Reads experiments_ ???.txt file. Returns list in a form: {statName1->{valueExp1, valueExp2, ...}, ...}. *)
(* Converts "true" to 1 and "false" to 0. *) 
readExperimentFile[fileName_] :=
    Module[ {contents,stats,data},
        contents = ImportString[Import[fileName],"TSV"];
        stats = contents[[1]];
        data = Transpose[Rest[contents]];
        MapThread[#1->#2&,{stats,data}] /. {"true"->1.,"false"->0.}
    ]

(* Reads parameters_ ???.txt file. Returns list in a form: {paramName1->paramValue1, paramName2->paramValue2, ...}. *)
(* Converts strings to numbers if possible. *) 
readParameterFile[fileName_] :=
    Module[ {contents,nameValue,allParamNames,
        (* This converts string to expression, but keeps complex numbers as strings (solves problem with "I" input.).*)
        convert :=
            Function[x,If[ NumberQ[ToExpression[x]]&&UnsameQ[Head[ToExpression[x]],Complex],
                           ToExpression[x],
                           x
                       ]]
            },
        (* Extract only lines with a parameter which always contains " = ". *)
        contents = FindList[fileName," = "];
        (* Extract name, value pair. Still everything is a string. *)
        nameValue = StringSplit[#," = "]& /@ contents;
        (* Some parameters can be twice in the file (the changing ones) - make the list of their names unique. *)
        allParamNames = Union[nameValue[[All,1]]];
        (* Now change the nameValue so it contains only the last occurence of each parameter (as changing are written
           after non changing). *)
        nameValue = nameValue[[Sequence@@Last[Position[nameValue[[All,1]],#]]]]& /@ allParamNames;
        (* Convert value part of nameValue to number if possible. *)
        #[[1]]->convert[#[[2]]]& /@ nameValue
    ]

configurationForLabel[data_,label_] :=
    Select[data,#[[idxLABEL]]==label&][[1]]

labelsForData[data_] :=
    data[[All,idxLABEL]]

(* Returns a list of values of parameters given by a name for a single configuration. Nonexisting values are replaced by "NONE" string. *)
paramValuesForConfiguration[cfg_,paramNames_List] :=
    ((Cases[cfg[[idxPARAMS]],(#->x_)->x]~Join~{"NONE"})[[1]])&/@paramNames

(* Returns a list of values of a single parameter given by a name for a all configurations. *)
paramValuesForData[data_,paramName_] :=
    Sequence@@(paramValuesForConfiguration[#,{paramName}])&/@data

resultsForConfiguration[cfg_,statName_] :=
    Cases[cfg[[idxRESULTS]],(statName->x_)->x][[1]]    

runFileForConfiguration[cfg_, runNum_, name_] :=
    StringReplace[cfg[[idxPFILE]], {"parameters" -> "run", ".txt" -> "_"}] <>
     toFixedWidth[runNum,3] <>
     name
(* Pads integer by leading zeroes, outputs String. *)
toFixedWidth[n_Integer, width_Integer] :=
    StringJoin[PadLeft[Characters[ToString[n]], width, "0"]]
  
listOfColors[numOfColors_] :=
    If[ numOfColors == 1,
        colors = "Rainbow",
        colors = 
            If[ numOfColors <= 11,
                Take[ColorData[10,"ColorList"],numOfColors],
                ColorData["Rainbow"][#]& /@ Range[0.05, 0.95, 0.9/(numOfColors - 1)]
            ]
    (*colors = Take[{LightRed,LightGreen,LightBlue,LightOrange,LightBrown,LightCyan,LightMagenta},numOfColors]*)
    ];

(* -------------------------------------------------------------------------------------------------------- *)
(* DATA IMPORT/EXPORT ------------------------------------------------------------------------------------ *)
(* -------------------------------------------------------------------------------------------------------- *)

listAllFiles[dirs_List] :=
    Transpose[{
        Flatten[FileNames[RegularExpression["experiments\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs],
        Flatten[FileNames[RegularExpression["parameters\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs]
    }]

Options[readAllFiles] = {ReplaceParamValues -> {}};
readAllFiles[dirs_List,labels_:Null,OptionsPattern[]] :=
    Module[ {data,tlabels},
        data = #~Join~{readExperimentFile[#[[1]]],(readParameterFile[#[[2]]]/.OptionValue[ReplaceParamValues])}&/@ listAllFiles[dirs];
        If[ labels===Null,
            tlabels = ToString /@ Range[Length[data]],
            tlabels = labels
        ];
        MapThread[{#1}~Join~#2&,{tlabels,data}]
    ]

saveData[data_,targetDir_] :=
    Module[ {fullTargetDir},
        (* Create target directory. *)
        fullTargetDir = PREFIX<>targetDir;
        If[ DirectoryQ[fullTargetDir],
            (Print["Directory exists!"];
             Return[$Failed])
        ];
        CreateDirectory[fullTargetDir,CreateIntermediateDirectories->True];
        (* Copy parameter files. *)
        MapIndexed[
            CopyFile[
                #1,
                FileNameJoin[{fullTargetDir,"parameters_"<>toFixedWidth[#2[[1]],3]<>".txt"}]
            ]&,
            data[[All,idxPFILE]]
        ];
        MapIndexed[
            CopyFile[
                #1,
                FileNameJoin[{fullTargetDir,"experiments_"<>toFixedWidth[#2[[1]],3]<>".txt"}]
            ]&,
            data[[All,idxEFILE]]
        ];
    ]

keepOnlyBest[dirs_List,statName_,nbest_] :=
    Module[ {data,best,allFiles,bestFiles},
        data = readAllFiles[dirs];
        best = {#[[idxPFILE]],sortConfigurationResults[data, #[[idxLABEL]], statName, nbest, Output -> "Raw"]}& /@ data;
        allFiles = FileNames[StringReplace[#[[1]],{"parameters" -> "run",".txt" -> "_*.txt"}] & /@ best];
        bestFiles = FileNames[Flatten[Outer[StringReplace[ToString[#1],{"parameters"->"run",".txt"->""}]<>"_"<>toFixedWidth[#2,3]<>"*.txt"&,{#[[1]]},#[[2,All,1]]] & /@ best]];
        DeleteFile[Complement[allFiles,bestFiles]]
    ]

changingParameters[data_] :=
    Module[ {allParams},
        allParams = (Union@@(#[[idxPARAMS]]&/@ data))/.(x_->_)->x;
        Select[Tally[allParams],#[[2]]>1&][[All,1]]
    ]

(* -------------------------------------------------------------------------------------------------------- *)
(* SORT DATA ---------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
Options[sortDataByParams] = {ReplaceParamValues -> {}};
sortDataByParams[data_,paramOrder_List,OptionsPattern[]] :=
    Sort[data,
        OrderedQ[{paramValuesForConfiguration[#1,paramOrder],paramValuesForConfiguration[#2,paramOrder]}]&
    ]/.OptionValue[ReplaceParamValues]

Options[sortConfigurationResults] = {Output -> "Table"};
sortConfigurationResults[data_,label_,statName_,number_:All,opts:OptionsPattern[]] :=
    Module[ {cfg,res,chosen},
        cfg = configurationForLabel[data,label];
        res = resultsForConfiguration[cfg,statName];
        chosen = Take[Sort[Transpose[{Range[Length[res]],res}],#1[[2]]>#2[[2]]&],number];
        Switch[OptionValue[Output],
            "Table",Grid[chosen,Frame->All],
            "Raw",chosen,
            "GenomeFileList",runFileForConfiguration[cfg,#[[1]],"_GENOMES_MATH.txt"]& /@ chosen,
            "BSF", listBSF[runFileForConfiguration[cfg,#[[1]],"_GENOMES_MATH.txt"]]& /@ chosen,
            "BSFGenomes", showGenomes[listBSF[runFileForConfiguration[cfg,#[[1]],"_GENOMES_MATH.txt"]]& /@ chosen],
            "Expressions", showAsExpressions[listBSF[runFileForConfiguration[cfg,#[[1]],"_GENOMES_MATH.txt"]]& /@ chosen],
            _,$Failed
            ]
    ]

(* -------------------------------------------------------------------------------------------------------- *)
(* TABLES ------------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
printAsTable[data_,paramNames_List] :=
    Grid[
        Transpose@{
            {Style["ID",Bold]}~Join~labelsForData[data],
            {Style["PARAM FILE",Bold]}~Join~(StringReplace[#,PREFIX->""]&/@data[[All,idxPFILE]]),
            Sequence@@(({Style[#,Bold]}~Join~paramValuesForData[data,#])& /@ paramNames)
        },Frame->All]

(* -------------------------------------------------------------------------------------------------------- *)
(* PLOTS -------------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)

CHOSEN2 = {Null,Null}

(* Other possibility is two set Operation -> Total *)
Options[plotBooleanAsBarChart] = {Operation -> (100*Mean[#]&)};
plotBooleanAsBarChart[data_,paramName_,numOfColors_:1,OptionsPattern[]] :=
    Module[ {colors,labels,labelPlacement,sum,buttons},
        (* Prepare colors *)
        CHOSEN2 = {Null,Null};
        colors = listOfColors[numOfColors];
        labels = labelsForData[data];
        labelPlacement = Placed[Style[#,FontSize->15]&/@labels,Axis,Rotate[#,Pi/2]&];
        sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
        buttons = MapThread[Button[#1,CHOSEN2 = Append[CHOSEN2,#2][[-2 ;; -1]]]&,{sum,labels}];
        Grid[{
            {BarChart[buttons,ChartLabels->labelPlacement,ChartStyle->colors,LabelingFunction->Center,ImageSize->1200]},
            {Dynamic[If[ Count[CHOSEN2,Null] == 0,
                         testFisherExact[data, CHOSEN2[[1]], CHOSEN2[[2]], paramName, TwoSided -> True, PValueOnly -> False],
                         "Choose two bars for Fisher's Exact Test."
                     ]]}
        }]
    ]

plotAsBoxWhiskerChart[data_,paramName_,numOfColors_:1] :=
    Module[ {colors,labelPlacement,values},
        (* Prepare colors *)
        colors = listOfColors[numOfColors];
        labelPlacement = Placed[Style[#,FontSize->15]&/@labelsForData[data],Axis,Rotate[#,Pi/2]&];
        values = resultsForConfiguration[#,paramName]&/@data;
        BoxWhiskerChart[values,"Notched",ChartLabels->labelPlacement,ChartStyle->colors,ImageSize->1200]
    ]

plotAsHistograms[data_,labels_List,statName_,numOfColors_:1] :=
    Module[ {results,colors,labelPlacement,values},
        results = resultsForConfiguration[configurationForLabel[data,#],statName]& /@ labels;
        Histogram[results,ImageSize->1200]
    ]
(* -------------------------------------------------------------------------------------------------------- *)
(* STATISTIC TESTS ---------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
testMannWhitney[data_,label1_,label2_,statName_] :=
    Module[ {data1,data2,vars,res},
        data1 = resultsForConfiguration[configurationForLabel[data,label1],statName];
        data2 = resultsForConfiguration[configurationForLabel[data,label2],statName];
        vars = {Variance[data1],Variance[data2]};
        Print["median 1: ",Median[data1]];
        Print["median 2: ",Median[data2]];
        Print["mean 1: ",Mean[data1]];
        Print["mean 2: ",Mean[data2]];
        Print["variance 1: ",vars[[1]]];
        Print["variance 2: ",vars[[2]]];
        vars = Sort[vars];
        Print["higher/lower variance ratio: ",vars[[2]]/vars[[1]]];
        If[ vars[[2]]/vars[[1]]>4,
            Print["WARNING: ratio > 4"]
        ];
        res = MannWhitneyTest[{data1,data2},Automatic,"HypothesisTestData"];
        Print["U stat: ", res["TestData"][[1]]];
        Print["P-value: ", res["TestData"][[2]]];
        Print[res["TestConclusion"]];
    ]

testKruskalWallis[data_,labels_List,statName_] :=
    Module[ {results},
        results = resultsForConfiguration[configurationForLabel[data,#],statName]& /@ labels;
        Print[Grid[
            {
                {""}~Join~(Style[#,Bold]& /@ labels),
                {Style["Mean",Bold]}~Join~(Mean /@ results),
                {Style["Variance",Bold]}~Join~(Median /@ results),
                {Style["Variance",Bold]}~Join~(Variance /@ results)
            },Frame->All
        ]];
        LocationEquivalenceTest[results,{"TestDataTable","KruskalWallis"}]
    ]
(* Computes marginals for a 2x2 table *)
testFisherComputeMarginals[{{a_,b_},{c_,d_}}] :=
    {
        {a,   b,   a+b},
        {c,   d,   c+d},
        {a+c, b+d, a+b+c+d}
    }

(* Computes P for a contingency table *)
testFisherComputeP[{{a_, b_, R1_},{c_, d_, R2_},{C1_,C2_,N_}}] :=
    (R1!R2!C1!C2!)/(N!Times[Sequence@@(#!&/@{a, b, c, d})])

(* Generates 2x2 tables for one direction. *)
testFisherCreateOutcomeSet[{{a_,b_},{c_,d_}}] :=
    Drop[NestWhileList[{{#[[1,1]]-1,#[[1,2]]+1},{#[[2,1]]+1,#[[2,2]]-1}}&,
        {{a,b},{c,d}},
        MatrixQ[#,NonNegative]&
    ],-1]

(* Generates 2x2 tables in opposite direction. *)
testFisherCreateOutcomeSet2[{{a_,b_},{c_,d_}}] :=
    Take[NestWhileList[{{#[[1,1]]+1,#[[1,2]]-1},{#[[2,1]]-1,#[[2,2]]+1}}&,
        {{a,b},{c,d}},
        MatrixQ[#,NonNegative]&
    ],{2,-2}]

Options[testFisherExact] = {TwoSided -> True};
testFisherExact[{{a_,b_},{c_,d_}},OptionsPattern[]] :=
    Module[ {res,table = {{a,b},{c,d}}},
        res = If[ OptionValue[TwoSided],
                  {#,testFisherComputeP[#]}&/@(testFisherComputeMarginals/@(testFisherCreateOutcomeSet[table]~Join~testFisherCreateOutcomeSet2[table])),
                  {#,testFisherComputeP[#]}&/@(testFisherComputeMarginals/@(testFisherCreateOutcomeSet[table]))
              ];
        Total[Select[res,#[[2]]<=res[[1,2]]&][[All,2]]]//N
    ]

Options[testFisherExact] = {TwoSided -> True,PValueOnly -> False};
testFisherExact[data_,label1_,label2_,statName_,opt:OptionsPattern[]] :=
    Module[ {data1,data2,outcomeA1,outcomeA2,outcomeB1,outcomeB2,significanceLevel = 0.05,res},
        data1 = resultsForConfiguration[configurationForLabel[data,label1],statName];
        data2 = resultsForConfiguration[configurationForLabel[data,label2],statName];
        outcomeA1 = Total[data1];
        outcomeA2 = Total[data2];
        outcomeB1 = Length[data1]-outcomeA1;
        outcomeB2 = Length[data2]-outcomeA2;
        res = testFisherExact[{{outcomeA1,outcomeA2},{outcomeB1,outcomeB2}},opt];
        If[ Not[OptionValue[PValueOnly]],
            Grid[
                {
                    {"",Style[label1,Bold],Style[label2,Bold]},
                    {Style["true",Bold],outcomeA1,outcomeA2},
                    {Style["false",Bold],outcomeB1,outcomeB2},
                    {Style["p-value",Bold],If[ res < significanceLevel,
                                               Item[res,Background->LightRed],
                                               Item[res,Background->LightGreen]
                                           ],SpanFromLeft}
                }
            ,Frame->All],
            res
        ]
    ]

Options[testFisherExactAll] = {TwoSided -> True};
testFisherExactAll[data_,statName_,significanceLevel_,opt:OptionsPattern[]] :=
    Module[ {labels,items},
        labels = labelsForData[data];
        items = {Style[#, Bold] & /@ ({""}~Join~Rest[labels])}~Join~
    Flatten[Most[
            MapIndexed[
             Outer["f", {#1}, 
               Array["span", Sequence @@ #2]~Join~
          labels[[Sequence @@ #2 + 1 ;; -1]]] &, labels]], 1];
        items = items /. "f"[_, "span"[_]] -> "";
        items[[All, 1]] = Style[#, Bold] & /@ ({""}~Join~Most[labels]);
        items = items /. ("f"[a_,b_] :> testFisherExact[data,a,b,statName,opt,PValueOnly -> True]);
        items = items /. (a:_?NumberQ :> If[ a < significanceLevel,
                                             Item[a,Background->LightRed],
                                             Item[a,Background->LightGreen]
                                         ]);
        Grid[items, Frame -> All]
    ]
  
End[] (* End Private Context *)

EndPackage[]
