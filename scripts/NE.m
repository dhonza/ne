(* ::Package:: *)

BeginPackage["NE`"]
Unprotect @@ Names["NE`*"];
ClearAll @@ Names["NE`*"];

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

changingParameters::usage = "changingParameters"
sortDataByParams::usage = "sortDataByParams[data,paramOrder] sorts configuration in data by given parameters, paramOrder is a 
	list of parameter names."
sortConfigurationResults::usage = "sortConfigurationResults"

printAsTable::usage = "printAsTable"
plotBooleanAsBarChart::usage = "plotBooleanAsBarChart"
plotAsBoxWhiskerChart::usage = "plotAsBoxWhiskerChart"
testMannWhitney::usage = "testMannWhitney[data,label1,label2,statName] computes Mann-Whitney U test on a single stat (given by name) 
of two configurations (given by labels)). This non-parametric test compares whether two independent samples of observations have 
equally large values. Test homoscedasticity, see "

testFisherExact::usage = "testFisherExact"

readExperimentFile::usage = "readExperimentFile[fileName]"
readParameterFile::usage = "readParameterFile[fileName]"

Begin["`Private`"]

PREFIX = "/Users/drchaj1/java/exp/";
idxLABEL = 1;
idxEFILE = 2;
idxPFILE = 3;
idxRESULTS = 4;
idxPARAMS = 5;

(* Reads experiments_ ???.txt file. Returns list in a form: {statName1->{valueExp1, valueExp2, ...}, ...}. *)
(* Converts "true" to 1 and "false" to 0. *) 
readExperimentFile[fileName_]:=
	Module[{contents,stats,data},
		contents = ImportString[Import[fileName],"TSV"];
		stats = contents[[1]];
		data = Transpose[Rest[contents]];
		MapThread[#1->#2&,{stats,data}] /. {"true"->1.,"false"->0.}
	]

(* Reads parameters_ ???.txt file. Returns list in a form: {paramName1->paramValue1, paramName2->paramValue2, ...}. *)
(* Converts strings to numbers if possible. *) 
readParameterFile[fileName_]:=
	Module[{contents,nameValue,allParamNames,
		(* This converts string to expression, but keeps complex numbers as strings (solves problem with "I" input.).*)
		convert:=Function[x,If[NumberQ[ToExpression[x]]&&UnsameQ[Head[ToExpression[x]],Complex],ToExpression[x],x]]
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

configurationForLabel[data_,label_]:=
	Select[data,#[[idxLABEL]]==label&][[1]]

labelsForData[data_]:=
	data[[All,idxLABEL]]

(* Returns a list of values of parameters given by a name for a single configuration. Nonexisting values are replaced by "NONE" string. *)
paramValuesForConfiguration[cfg_,paramNames_List]:=
	((Cases[cfg[[idxPARAMS]],(#->x_)->x]~Join~{"NONE"})[[1]])&/@paramNames

(* Returns a list of values of a single parameter given by a name for a all configurations. *)
paramValuesForData[data_,paramName_]:=
	Sequence@@(paramValuesForConfiguration[#,{paramName}])&/@data

resultsForConfiguration[cfg_,statName_]:=
	Cases[cfg[[idxRESULTS]],(statName->x_)->x][[1]]	

listOfColors[numOfColors_]:=
	If[numOfColors == 1,
		colors = "Rainbow",
		colors = Take[ColorData[10,"ColorList"],numOfColors]
		(*colors = Take[{LightRed,LightGreen,LightBlue,LightOrange,LightBrown,LightCyan,LightMagenta},numOfColors]*)
	];

(* -------------------------------------------------------------------------------------------------------- *)
(* DATA IMPORT/EXPORT ------------------------------------------------------------------------------------ *)
(* -------------------------------------------------------------------------------------------------------- *)

listAllFiles[dirs_List]:=
	Transpose[{
		Flatten[FileNames[RegularExpression["experiments\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs],
		Flatten[FileNames[RegularExpression["parameters\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs]
	}]

Options[readAllFiles]={ReplaceParamValues -> {}};
readAllFiles[dirs_List,labels_:Null,OptionsPattern[]]:=
	Module[{data,tlabels},
		data = #~Join~{readExperimentFile[#[[1]]],(readParameterFile[#[[2]]]/.OptionValue[ReplaceParamValues])}&/@ listAllFiles[dirs];
		If[labels===Null,tlabels=ToString /@ Range[Length[data]],tlabels=labels];
		MapThread[{#1}~Join~#2&,{tlabels,data}]
	]

saveData[data_,targetDir_]:=
	Module[{fullTargetDir},
		(* Create target directory. *)
		fullTargetDir = PREFIX<>targetDir;
		If[DirectoryQ[fullTargetDir],(Print["Directory exists!"];Return[$Failed])];
		CreateDirectory[fullTargetDir,CreateIntermediateDirectories->True];
		(* Copy parameter files. *)
		MapIndexed[
			CopyFile[
				#1,
				FileNameJoin[{fullTargetDir,"parameters_"<>ToString[PaddedForm[#2[[1]],2,NumberPadding->{"0","0"}]]<>".txt"}]
			]&,
			data[[All,idxPFILE]]
		];
		MapIndexed[
			CopyFile[
				#1,
				FileNameJoin[{fullTargetDir,"experiments_"<>ToString[PaddedForm[#2[[1]],2,NumberPadding->{"0","0"}]]<>".txt"}]
			]&,
			data[[All,idxEFILE]]
		];
	]

changingParameters[data_]:=
	Module[{allParams},
		allParams = (Union@@(#[[idxPARAMS]]&/@ data))/.(x_->_)->x;
		Select[Tally[allParams],#[[2]]>1&][[All,1]]
	]

(* -------------------------------------------------------------------------------------------------------- *)
(* SORT DATA ---------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
Options[sortDataByParams]={ReplaceParamValues -> {}};
sortDataByParams[data_,paramOrder_List,OptionsPattern[]]:=
	Sort[data,
		OrderedQ[{paramValuesForConfiguration[#1,paramOrder],paramValuesForConfiguration[#2,paramOrder]}]&
	]/.OptionValue[ReplaceParamValues]

sortConfigurationResults[data_,label_,statName_,number_:All]:=
	Module[{res},
		res = resultsForConfiguration[configurationForLabel[data,label],statName];
		Grid[Take[Sort[Transpose[{Range[Length[res]],res}],#1[[2]]>#2[[2]]&],number],Frame->All]
	]

(* -------------------------------------------------------------------------------------------------------- *)
(* TABLES ------------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
printAsTable[data_,paramNames_List]:=
	Grid[
		Transpose@{
			{Style["ID",Bold]}~Join~labelsForData[data],
			{Style["PARAM FILE",Bold]}~Join~(StringReplace[#,PREFIX->""]&/@data[[All,idxPFILE]]),
			Sequence@@(({Style[#,Bold]}~Join~paramValuesForData[data,#])& /@ paramNames)
		},Frame->All]

(* -------------------------------------------------------------------------------------------------------- *)
(* PLOTS -------------------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)

(* Other possibility is two set Operation -> Total *)
Options[plotBooleanAsBarChart]={Operation -> (100*Mean[#]&)};
plotBooleanAsBarChart[data_,paramName_,numOfColors_:1,OptionsPattern[]]:=
	Module[{colors,labelPlacement,sum},
		(* Prepare colors *)
		colors = listOfColors[numOfColors];
		labelPlacement = Placed[Style[#,FontSize->15]&/@labelsForData[data],Axis,Rotate[#,Pi/2]&];
		sum = OptionValue[Operation][resultsForConfiguration[#,paramName]]&/@data;
		BarChart[sum,ChartLabels->labelPlacement,ChartStyle->colors,LabelingFunction->Center,ImageSize->1200]
	]

plotAsBoxWhiskerChart[data_,paramName_,numOfColors_:1]:=
	Module[{colors,labelPlacement,values},
		(* Prepare colors *)
		colors = listOfColors[numOfColors];
		labelPlacement = Placed[Style[#,FontSize->15]&/@labelsForData[data],Axis,Rotate[#,Pi/2]&];
		values = resultsForConfiguration[#,paramName]&/@data;
		BoxWhiskerChart[values,"Notched",ChartLabels->labelPlacement,ChartStyle->colors,ImageSize->1200]
	]

(* -------------------------------------------------------------------------------------------------------- *)
(* STATISTIC TESTS ---------------------------------------------------------------------------------------- *)
(* -------------------------------------------------------------------------------------------------------- *)
testMannWhitney[data_,label1_,label2_,statName_]:=
	Module[{data1,data2,vars,res},
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
		If[vars[[2]]/vars[[1]]>4,Print["WARNING: ratio > 4"]];
		res = MannWhitneyTest[{data1,data2},Automatic,"HypothesisTestData"];
		Print["U stat: ", res["TestData"][[1]]];
		Print["P-value: ", res["TestData"][[2]]];
		Print[res["TestConclusion"]];
	]

(* Computes marginals for a 2x2 table *)
testFisherComputeMarginals[{{a_,b_},{c_,d_}}]:=
	{
		{a,   b,   a+b},
		{c,   d,   c+d},
		{a+c, b+d, a+b+c+d}
	}

(* Computes P for a contingency table *)
testFisherComputeP[{{a_, b_, R1_},{c_, d_, R2_},{C1_,C2_,N_}}]:=
		(R1!R2!C1!C2!)/(N!Times[Sequence@@(#!&/@{a, b, c, d})])

(* Generates 2x2 tables for one direction. *)
testFisherCreateOutcomeSet[{{a_,b_},{c_,d_}}]:=
	Drop[NestWhileList[{{#[[1,1]]-1,#[[1,2]]+1},{#[[2,1]]+1,#[[2,2]]-1}}&,
		{{a,b},{c,d}},
		MatrixQ[#,NonNegative]&
	],-1]

(* Generates 2x2 tables in opposite direction. *)
testFisherCreateOutcomeSet2[{{a_,b_},{c_,d_}}]:=
	Take[NestWhileList[{{#[[1,1]]+1,#[[1,2]]-1},{#[[2,1]]-1,#[[2,2]]+1}}&,
		{{a,b},{c,d}},
		MatrixQ[#,NonNegative]&
	],{2,-2}]

Options[testFisherExact]={TwoSided -> True};
testFisherExact[{{a_,b_},{c_,d_}},OptionsPattern[]]:=
	Module[{res,table = {{a,b},{c,d}}},		
		res = If[OptionValue[TwoSided],
			{#,testFisherComputeP[#]}&/@(testFisherComputeMarginals/@(testFisherCreateOutcomeSet[table]~Join~testFisherCreateOutcomeSet2[table])),
			{#,testFisherComputeP[#]}&/@(testFisherComputeMarginals/@(testFisherCreateOutcomeSet[table]))];
		Total[Select[res,#[[2]]<=res[[1,2]]&][[All,2]]]//N
	]

End[]
Protect @@ Names["NE`*"];
EndPackage[]












