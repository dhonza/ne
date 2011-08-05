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
changingParameters::usage = "changingParameters"
sortDataByParams::usage = "sortDataByParams[data,paramOrder] sorts configuration in data by given parameters, paramOrder is a 
	list of parameter names."
printAsTable::usage = "printAsTable"
plotBooleanAsBarChart::usage = "plotBooleanAsBarChart"
plotAsBoxWhiskerChart::usage = "plotAsBoxWhiskerChart"
testMannWhitney::usage = "testMannWhitney"

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

(* Returns a list of values of parameters given by a name for a single configuration. *)
paramValuesForConfiguration[cfg_,paramNames_List]:=
	(Cases[cfg[[idxPARAMS]],(#->x_)->x][[1]])&/@paramNames

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

listAllFiles[dirs_List]:=
	Transpose[{
		Flatten[FileNames[RegularExpression["experiments\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs],
		Flatten[FileNames[RegularExpression["parameters\\_\\d\\d\\d\\.txt"],{PREFIX<>#}]&/@dirs]
	}]

readAllFiles[dirs_List,labels_:Null]:=
	Module[{data,tlabels},
		data = #~Join~{readExperimentFile[#[[1]]],readParameterFile[#[[2]]]}&/@ listAllFiles[dirs];
		If[labels===Null,tlabels=Range[Length[data]],tlabels=labels];
		MapThread[{#1}~Join~#2&,{tlabels,data}]
	]

changingParameters[data_]:=
	Module[{allParams},
		allParams = (Union@@(#[[idxPARAMS]]&/@ data))/.(x_->_)->x;
		Select[Tally[allParams],#[[2]]>1&][[All,1]]
	]

sortDataByParams[data_,paramOrder_List]:=
	Sort[data,
		OrderedQ[{paramValuesForConfiguration[#1,paramOrder],paramValuesForConfiguration[#2,paramOrder]}]&]

printAsTable[data_,paramNames_List]:=
	Grid[
		Transpose@{
			{Style["ID",Bold]}~Join~labelsForData[data],
			{Style["PARAM FILE",Bold]}~Join~(StringReplace[#,PREFIX->""]&/@data[[All,idxPFILE]]),
			Sequence@@(({Style[#,Bold]}~Join~paramValuesForData[data,#])& /@ paramNames)
		},Frame->All]

plotBooleanAsBarChart[data_,paramName_,numOfColors_:1]:=
	Module[{colors,labelPlacement,sum},
		(* Prepare colors *)
		colors = listOfColors[numOfColors];
		labelPlacement = Placed[Style[#,FontSize->15]&/@labelsForData[data],Axis,Rotate[#,Pi/2]&];
		sum = Total[resultsForConfiguration[#,paramName]]&/@data;
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

End[]
Protect @@ Names["NE`*"];
EndPackage[]






