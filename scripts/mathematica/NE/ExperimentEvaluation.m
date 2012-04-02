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
assignLabelsByParameters::usage = "assignLabelsByParameters replaces current labels with labels given by parameters"
extractParameters::usage = "extractParameters"
replaceLabels::usage = "replaceLabels"
selectData::usage = "selectData[data, paramRule] selects only experiments having given parameter value (given by paramRule), param rules are in form:
	{ParamA -> Value1, AndParamB -> {Value2 , OrValue3}}."    
removeData::usage = "removeData works similar to selectData, but removes experiments" 
saveData::usage = "saveData"
keepOnlyBest::usage = "keepOnlyBest"
aggregateBoolean::usage = "aggregateBoolean"

changingParameters::usage = "changingParameters"
sortDataByParams::usage = "sortDataByParams[data,paramOrder] sorts configuration in data by given parameters, paramOrder is a 
	list of parameter names."
sortConfigurationResults::usage = "sortConfigurationResults"

printAsTable::usage = "printAsTable"
printBooleanValueAsTable::usage = "printBooleanValueAsTable"
printBooleanRanksAsTable::usage = "printBooleanRanksAsTable"
plotBooleanAsBarChart::usage = "plotBooleanAsBarChart"
plotBooleanAsBarChartPub::usage = "plotBooleanAsBarChartPub"
plotAsBoxWhiskerChart::usage = "plotAsBoxWhiskerChart"
plotAsBoxWhiskerChartPub::usage = "plotAsBoxWhiskerChartPub"
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
(*    ((Cases[cfg[[idxPARAMS]],(#->x_)->x]~Join~{"NONE"})[[1]])&/@paramNames*)
    ((Cases[cfg[[idxPARAMS]],(#->x_)->x]~Join~{""})[[1]])&/@paramNames

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
    
assignLabelsByParameters[data_,paramNames_,replacementRules_:{}] :=
    Module[ {values},
        values = Replace[paramNames,#[[idxPARAMS]]~Join~{_ -> Null},1] & /@ data;        
        (* Remove missing parameters *)
        values = Select[#, (# =!= Null)&]& /@ values;
        (* Convert any nonstring values to strings *)
        values = Map[ToString,values,{2}];        
        (*values = Select[values] & /@ values;*)
        If[ replacementRules =!= {},
            values = StringReplace[#, replacementRules]& /@ values
        ];
        (* Delete repeating missing values *)
        values = DeleteCases[#,""]& /@ values;
        
        (* Insert underscores and join to strings *)
        values = StringJoin[Riffle[#,"_"]]& /@ values;
        replaceLabels[data,values]
    ]

extractParameters[data_,params_] :=
    Module[ {paramNames,values,replacement},
    	paramNames = params[[All,1]];
        values = Replace[paramNames,#[[idxPARAMS]]~Join~{_ -> Null},1] & /@ data;        
        values = Map[ToString,values,{2}];
        replacement = params[[All,2]];
        MapThread[(StringReplace[#1,#2]&),{#,replacement}]&/@values        
    ]

replaceLabels[data_,labels_] :=
    Module[ {newData},
        newData = data;
        newData[[All,1]] = labels;
        newData
    ]

selectDataHelper[params_,paramRule_] :=
    Module[ {orRules},
        (*Print[paramRule," ", MemberQ[params, paramRule]];*)
        If[ MatchQ[paramRule,_ -> {__}],
            (
                orRules = paramRule/. (f : _ -> t : {__}) :> (Rule[f, #] & /@ t);
                MemberQ[MemberQ[params,#]& /@ orRules,True]
            ),
            MemberQ[params, paramRule]
        ]
    ]

selectData[data_,paramRule_List] :=
    Module[ {},
        Select[data, 
            Function[x,FreeQ[selectDataHelper[x[[idxPARAMS]],#]& /@ paramRule,False]]
        ]
    ]

removeData[data_,paramRule_List] :=
    Module[ {},
        Select[data, 
            Function[x,FreeQ[selectDataHelper[x[[idxPARAMS]],#]& /@ paramRule,True]]
        ]
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
(* AGGREGATE DATA ----------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)

(* Helper function, aggregates the whole dataset, resulting in dataset with a single configuration. 
The configuration contains only single result parameter "paramName".*)    
aggregateBooleanAll[data_,paramName_] :=
 Module[ {results,params},
 	(*"ID","EFILE","PFILE",*)
 	params = #[[idxPARAMS]]&/@data;
 	results = Select[#[[idxRESULTS]], #[[1]] == paramName &]&/@data;
 	params = Intersection[Flatten[params]]; (* extract not changing parameters only*)
 	results = {paramName->Flatten[MapThread[Mean[{##}]&,results[[All,All,2]]]]};
 	{"ID","EFILE","PFILE",results,params} 	
 ]

aggregateBoolean[data_,paramName_,partSize_:1] :=
 Module[ {},
 	aggregateBooleanAll[#,paramName]&/@Partition[data,partSize]	
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
(* Prints parameters *)
printAsTable[data_,paramNames_List] :=
    Grid[
        Transpose@{
            {Style["ID",Bold]}~Join~labelsForData[data],
            {Style["PARAM FILE",Bold]}~Join~(StringReplace[#,PREFIX->""]&/@data[[All,idxPFILE]]),
            Sequence@@(({Style[#,Bold]}~Join~paramValuesForData[data,#])& /@ paramNames)
        },Frame->All]

(* Prints results *)
(* Other possibility is two set Operation -> Total *)
Options[printBooleanValueAsTable] = {Operation -> (100*Mean[#]&)};
printBooleanValueAsTable[data_,paramName_,OptionsPattern[]] :=
    Module[ {labels,sum},
        labels = labelsForData[data];
        sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
    	Grid[
        	Transpose@{
            	{Style["ID",Bold]}~Join~labels,
            	{Style["PARAM FILE",Bold]}~Join~(StringReplace[#,PREFIX->""]&/@data[[All,idxPFILE]]),
            	{Style[paramName,Bold]}~Join~sum
        	},Frame->All]       
    ]

(* Computes ranks averaging the same values: assignMeanRanks[{10, 2, 10, 4, 5, 10, 4}] gives {6, 1, 6, 5/2, 4, 6, 5/2} *)
assignMeanRanks[values_List] :=
    Module[ {multi, pos, rank, means, replace},
        multi = Select[Tally[values], #[[2]] > 1 &][[All, 1]];
        pos = Position[values, #] & /@ multi;
        rank = Array[0 &, Length[values]];
        rank[[Ordering[values]]] = Range[Length[values]];
        means = Mean[Extract[rank, #]] & /@ pos;
        replace = 
         Flatten[MapThread[
           Outer[Rule, Flatten[#1], {#2}] &, {pos, means}]];
        ReplacePart[rank, replace]
    ]

Options[printBooleanRanksAsTable] = {Operation -> (100*Mean[#]&)};
printBooleanRanksAsTable[data_,paramName_,groupSize_,OptionsPattern[]]:=
    Module[ {labels,sum,ranks,params,paramValues,table,tableData},
        labels = labelsForData[data];
        sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
        ranks = Flatten[Mean/@Transpose[assignMeanRanks /@ Partition[sum,groupSize]]];
        params = (changingParameters /@ Partition[data,groupSize])[[1]];
        paramValues = paramValuesForConfiguration[#,params]& /@ (data[[1;;groupSize]]);
        tableData = Transpose[{Range[groupSize]}~Join~Transpose[paramValues]~Join~{ranks}];
        tableData = Sort[tableData,#1[[4]] < #2[[4]]&];
        table = {Style[#,Bold]& /@ ({"ID"}~Join~params~Join~{"RANK"})}~Join~tableData;
        Grid[table,Frame->All]
    ]
(* -------------------------------------------------------------------------------------------------------- *)
(* PLOTS -------------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)

CHOSEN2 = {Null,Null}

(* Other possibility is to set Operation -> Total *)
Options[plotBooleanAsBarChartPub] = {
	Operation -> (100*Mean[#]&),
	Epilog -> {},
	PlotRange -> Automatic,
	ImagePadding->Automatic,
	AxesLabel -> "SUCCESS %",
	AspectRatio->0.5/GoldenRatio,
	ImageSize->{{700},{1600}},
	BarLabelsRotate->0
	};
plotBooleanAsBarChartPub[data_,paramName_,partNames_,subChartSpacing_,partSize_:1,OptionsPattern[]] :=
    Module[ {colors,parts,labels,partPlacement,labelPlacement,sum,barLabels},
    	If[Mod[Length[data],partSize] != 0, Print["WARNING: Mod[Length[data],partSize] != 0"]];
        colors = listOfColors[partSize];
        labels = labelsForData[data];
        parts = Grid[Array[{""}&,Length[labels[[1]]]]~Join~{{#}},Spacings->{2,subChartSpacing}]&/@partNames;
        labels = Grid[Partition[#,1],Spacings->{2,0}]&/@labels;                 
        sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
        sum = Partition[sum,partSize];
        barLabels = (Placed[Style[Grid[{{#1}},Background->White],FontSize->13],Above,Rotate[#,OptionValue[BarLabelsRotate]]&]&);
        partPlacement = Placed[Style[#,FontSize->17]&/@parts,Axis];
        labelPlacement = Placed[Style[#,FontSize->15]&/@labels,Axis];
 		BarChart[sum,
 			LabelingFunction->barLabels,
 			ChartLabels->{partPlacement,labelPlacement},
 			ChartStyle->colors,
 			ImageSize->OptionValue[ImageSize],
 			AxesStyle->Directive[15],
 			AspectRatio->OptionValue[AspectRatio],
 			BarSpacing->{Automatic, 1.5},
 			Epilog->OptionValue[Epilog],
 			PlotRange->OptionValue[PlotRange],
 			ImagePadding->OptionValue[ImagePadding],
 			AxesLabel->OptionValue[AxesLabel]
 			]            
    ]

Options[plotAsBoxWhiskerChartPub] = {Epilog -> {},PlotRange -> Automatic,ImagePadding->Automatic,AxesLabel -> "", PlotRangePadding -> Automatic};
plotAsBoxWhiskerChartPub[data_,paramName_,partNames_,subChartSpacing_,partSize_:1,OptionsPattern[]] :=
    Module[ {colors,parts,labels,partPlacement,labelPlacement,values,barLabels},
        colors = listOfColors[partSize];
        labels = labelsForData[data];
        parts = Grid[Array[{""}&,Length[labels[[1]]]]~Join~{{#}},Spacings->{2,subChartSpacing}]&/@partNames;
        labels = Grid[Partition[#,1],Spacings->{2,0}]&/@labels;                 
        values = resultsForConfiguration[#,paramName]&/@data;
 		values = Partition[values,partSize];
        barLabels = (Placed[Style[Grid[{{#1}},Background->White],FontSize->13],Above]&);
        partPlacement = Placed[Style[#,FontSize->17]&/@parts,Axis];
        labelPlacement = Placed[Style[#,FontSize->15]&/@labels,Axis];
 		(*Style[#, FontSize -> 14] & /@ *)
 		BoxWhiskerChart[
 			values,
 			"Notched",
 			ChartLabels->labelPlacement,
 			LabelingFunction->(Placed[Style[Round[Mean[#1],0.1], FontSize -> 14], Above] &),
 			ChartStyle->colors,
 			FrameTicksStyle -> Directive[18],
			AspectRatio -> 0.5/GoldenRatio,
 			ImageSize->700,
 			Epilog->OptionValue[Epilog],
 			PlotRange->OptionValue[PlotRange],
 			ImagePadding->OptionValue[ImagePadding],
			PlotRangePadding->OptionValue[PlotRangePadding],
 			AxesLabel->{"",OptionValue[AxesLabel]}
 			]        
    ]
        
Options[plotBooleanAsBarChart] = {Operation -> (100*Mean[#]&)};    
plotBooleanAsBarChart[data_,paramName_,partSize_:1,OptionsPattern[]] :=
    Module[ {colors,labels,labelPlacement,sum,buttons},
        (* Prepare colors *)
        CHOSEN2 = {Null,Null};
        colors = listOfColors[partSize];
        labels = labelsForData[data];
        labelPlacement = Placed[Style[#,FontSize->15]&/@labels,Axis,Rotate[#,Pi/2]&];
        sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
        buttons = MapThread[Button[#1,CHOSEN2 = Append[CHOSEN2,#2][[-2 ;; -1]]]&,{sum,labels}];
        Grid[{
            {BarChart[buttons,ChartLabels->labelPlacement,
            	ChartStyle->colors,
            	LabelingFunction->Center,
            	ImageSize->{{700},{1600}},
            	AspectRatio->0.5/GoldenRatio,
 				BarSpacing->{Automatic, 1.5}
 			]},
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
