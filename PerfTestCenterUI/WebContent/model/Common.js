"use strict";
var Enums = {
	priority:[{name: "Very High"},
	          {name: "High"}, 
	          {name: "Medium"}, 
	          {name: "Low"}],
	category:[{name: "Benchmark"}, 
	          {name: "Sizing"}, 
	          {name: "Ad-hoc"}, 
	          {name: "Debug"}, 
	          {name: "CustomerCase"}, 
	          {name: "Concurrent"}, 
	          {name: "PerformanceValidation"}, 
	          {name: "Support"}, 
	          {name: "MultipleSchema"}, 
	          {name: "LoadTest"}, 
	          {name: "StabilityTest"},
	          {name: "Benchmark01"}],
	status: [{name: "New"}, {name: "Plan"}, {name: "Prepare"},{name: "Testing"}, {name: "Done"}, {name: "Blocked"}, {name: "Pending"}, {name: "Terminated"}],
	issueStatus:[{name:"Open"}, {name: "Processing"}, {name: "Resolved"}, {name: "Closed"}]
};