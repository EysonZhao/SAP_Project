sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ScenarioDetail", {
		
		_mScenarioModel: new sap.ui.model.json.JSONModel(),			//model for the current scenario
		_mTaskModel: new sap.ui.model.json.JSONModel(),			//model for the current editing task
		
		_mProjectListModel: new sap.ui.model.json.JSONModel(),		//model for 'project' comboBox control
		_mPackageListModel:new sap.ui.model.json.JSONModel(),		//model for task edit 'package' select control
		_mTaskListModel: new sap.ui.model.json.JSONModel(),		//model for task edit 'task' select control
		_mScenarioTaskListModel: new sap.ui.model.json.JSONModel(),	//model for task list table
		
		onInit: function () {
			this.getRouter().getRoute("scenariodetail").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			oView.setModel(this._mScenarioModel, "scenarioModel");
			oView.setModel(this._mScenarioTaskListModel, "scenarioTaskListModel");
			oView.setModel(this._mProjectListModel, "projectListModel");
			oView.setModel(this._mPackageListModel, "packageListModel");
			oView.setModel(this._mTaskListModel, "taskListModel");
			oView.setModel(this._mTaskModel, "taskModel");
			var oArgs = oEvent.getParameter("arguments");
			this.initScenarioModel(oArgs.scenarioId);
			this.initProjectListModel();
			this.initPackageListModel();
			this.initTaskListModel();
		},
		initProjectListModel: function(){
			var that = this;
			$.ajax({
				url:this.getConfig("serviceBaseUrl") + '/Project/List',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						that._mProjectListModel.setData({projects:result});
						that.initTaskListModel();
					}
				},
				error: function(msg){
					MessageBox.error("Error resposne from server!", {
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, 4)
					});
				}
			});
		},
		initPackageListModel: function(){
			//TODO: replace the mock data by request data from service
			var that = this;
			$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/Task/ListTaskLibrary',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					that._mPackageListModel.setData({packages: result});
				},
				error: function(msg){
					MessageBox.error("Error resposne from server!", {
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, 4)
					});
				}
			});
		}, 
		initTaskListModel: function(){
			var oPackageListData = this._mPackageListModel.getData();
			if(oPackageListData && oPackageListData.packages && oPackageListData.packages.length > 0){
				this._mTaskListModel.setData({classes: oPackageListData.packages[0].classes});
			}
		},
		initScenarioModel: function(scenarioId){
			if(scenarioId){
				var that = this;
				$.ajax({
					url:this.getConfig("serviceBaseUrl") + '/Scenario/Get/' + scenarioId,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						if(result){
							that._mScenarioModel.setData(result);
							var oTask = JSON.parse(result.taskListJson);
							if(! oTask){
								oTask={tasks:[]};
							}
							oTask.tasks.sort(function(a, b){
								return a.sn - b.sn;
							});
							that._mScenarioTaskListModel.setData(oTask);
						}
					},
					error: function(msg){
						MessageBox.error("Error resposne from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					}
				});
			}
			else{
				this._mScenarioModel.setData({});
				this._mScenarioTaskListModel.setData({});
			}
		},
		onSave: function(){
			var that = this;
			var oScenario = this._mScenarioModel.getData();
			if(oScenario.id){	//update
				oScenario.taskListJson = JSON.stringify(this._mScenarioTaskListModel.getData());
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Scenario/Update',
					type: 'POST',
					contentType: 'application/json;charset=utf-8',
					data: JSON.stringify(oScenario),
					success: function(result){
						that._mScenarioModel.setData(result);
						that.showMessage("Saved");
					},
					error: function(msg){
						MessageBox.error("Error resposne from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					}
				});
			}
			else{				//add
				oScenario.taskListJson = JSON.stringify(this._mScenarioTaskListModel.getData());
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Scenario/Add',
					type: 'POST',
					contentType: 'application/json;charset=utf-8',
					data: JSON.stringify(oScenario),
					success: function(result){
						that._mScenarioModel.setData(result);
						that.showMessage("Saved");
					},
					error: function(msg){
						MessageBox.error("Error resposne from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					}
				});
			}
		},
		onMoveUp: function(oEvent){
			var selectedSn = parseInt(oEvent.getSource().oParent.mAggregations.cells[0].mProperties.text);
			var oScenarioTaskListData = this._mScenarioTaskListModel.getData();
			var oSelectedItemData = oScenarioTaskListData.tasks[selectedSn];
			if(selectedSn > 0){
				var oPreviousItemData = oScenarioTaskListData.tasks[selectedSn - 1];
				oScenarioTaskListData.tasks[selectedSn-1] = oSelectedItemData;
				oScenarioTaskListData.tasks[selectedSn] = oPreviousItemData;
				oSelectedItemData.sn = selectedSn -1 ;
				oPreviousItemData.sn = selectedSn;
				this._mScenarioTaskListModel.setData(oScenarioTaskListData);
				
				//check whether the moved item is the selected item or replaced item
				var oTaskData = this._mTaskModel.getData();
				if(selectedSn == oTaskData.sn){
					oTaskData.sn = selectedSn -1;
				}
				else if(selectedSn-1 == oTaskData.sn){
					oTaskData.sn = selectedSn + 1;
				}
				this._mTaskModel.setData(oTaskData);
			}
		},
		onMoveDown: function(oEvent){
			var selectedSn = parseInt(oEvent.getSource().oParent.mAggregations.cells[0].mProperties.text);
			var oScenarioTaskListData = this._mScenarioTaskListModel.getData();
			var oSelectedItemData = oScenarioTaskListData.tasks[selectedSn];
			if(selectedSn +1 < oScenarioTaskListData.tasks.length){
				var oNextItemData = oScenarioTaskListData.tasks[selectedSn + 1];
				oScenarioTaskListData.tasks[selectedSn + 1] = oSelectedItemData;
				oScenarioTaskListData.tasks[selectedSn] = oNextItemData;
				oSelectedItemData.sn = selectedSn + 1;
				oNextItemData.sn = selectedSn;
				this._mScenarioTaskListModel.setData(oScenarioTaskListData);
				
				//check whether the moved item is the selected item or replaced item
				var oTaskData = this._mTaskModel.getData();
				if(selectedSn == oTaskData.sn){
					oTaskData.sn = selectedSn + 1;
				}
				else if(selectedSn + 1 == oTaskData.sn){
					oTaskData.sn = selectedSn - 1;
				}
				this._mTaskModel.setData(oTaskData);
			}
		},
		onDelete: function(oEvent){
			var oSelectedItemData = oEvent.getParameters().listItem.getBindingContext("scenarioTaskListModel").getObject();
			var oScenarioTaskListData = this._mScenarioTaskListModel.getData();
			for(var i=0 ; i<oScenarioTaskListData.tasks.length ; i++){
				if(i == oSelectedItemData.sn){
					oScenarioTaskListData.tasks.splice(i, 1);
					break;
				}
			}
			
			for(var i=0 ; i<oScenarioTaskListData.tasks.length ; i++){
				if(i >= oSelectedItemData.sn){
					oScenarioTaskListData.tasks[i].sn--;
				}
			}
			this._mScenarioTaskListModel.setData(oScenarioTaskListData);
		},
		onItemPress: function(oEvent){
			var oSelectedItemData = oEvent.getParameters().listItem.getBindingContext("scenarioTaskListModel").getObject();
			var oDuplicatedData = $.extend(true,{}, oSelectedItemData);
			this._mTaskModel.setData(oDuplicatedData);
		},
		onPackageSelectChange: function(oEvent){
			var selectedKey = oEvent.getParameters().value;
			var oPackageListData = this._mPackageListModel.getData();
			for(var i=0 ; i<oPackageListData.packages.length; i++){
				if(oPackageListData.packages[i].name == selectedKey){
					this._mTaskListModel.setData({classes: oPackageListData.packages[i].classes});
					var oTaskData = this._mTaskModel.getData();
					oTaskData.className = "";
					this._mTaskModel.setData(oTaskData);
					var oTaskListControl = this.getView().byId("TaskSelect");
					oTaskListControl.setSelectedKey("");
				}
			}
		},
		onTaskAdd: function(oEvent){
			var oScenarioTaskListData = this._mScenarioTaskListModel.getData();
			if(oScenarioTaskListData.tasks == undefined){
				oScenarioTaskListData.tasks= [];
			}
			var oNewTask = this._mTaskModel.getData();
			var oTable = this.getView().byId("table");
			//clone a new entry
			var oNewTaskCopy = $.extend(true, {}, oNewTask);
			oNewTaskCopy.sn = oTable.getItems().length;			
			oScenarioTaskListData.tasks.push(oNewTaskCopy);
			this._mScenarioTaskListModel.setData(oScenarioTaskListData);
			//disconnect the TaskModel between ScenarioTaskListModel
			this._mTaskModel.setData($.extend(true, {}, oNewTaskCopy));
		},
		onTaskUpdate: function(oEvent){
			var oScenarioTaskListData = this._mScenarioTaskListModel.getData();
			var oTaskData = this._mTaskModel.getData();
			//the 'sn' is also the array index number
			oScenarioTaskListData.tasks[oTaskData.sn] = $.extend(true, {}, oTaskData);
			this._mScenarioTaskListModel.setData(this._mScenarioTaskListModel.getData());
		}
	});
});
