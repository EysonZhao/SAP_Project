sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionDetail", {
		
		//-----------------------------0. main page----------------------------------
		mCurrentStep: 0,
		mPanelList: [],
		mExecutionModel: new sap.ui.model.json.JSONModel(),
		mSaveBtn: undefined,
		mBackBtn: undefined,
		mNextBtn: undefined,
		mOperation: "",
//		_mNeedUpdateHostsParameter: false,
//		_mNeedUpdateTasksParameter: false,
//		executionModel>/name
//		executionModel>/testRequestId
//		executionModel>/testRequest
//		executionModel>/projectId
//		executionModel>/project
//		executionModel>/projectParameters
//		executionModel>/projectParameters/parameters/entries
//		executionModel>/scenario
//		executionModel>/scenarioId
//		executionModel>/tasks
//		executionModel>/hostsParameter
//		executionModel>/hostsParameter/parameters/entries
//		executionModel>/tasksParameter
//		executionModel>/tasksParameter/parameters/entries
		
		initPanelList: function(){
			if(this.mPanelList.length <1){
				this.mPanelList.push(this.getView().byId("SelectTestRequestPanel"));
				this.mPanelList.push(this.getView().byId("SelectTestScenarioPanel"));
				this.mPanelList.push(this.getView().byId("TaskListPanel"));
				this.mPanelList.push(this.getView().byId("TaskHostPanel"));
				this.mPanelList.push(this.getView().byId("TaskParameterPanel"));
				this.mPanelList.push(this.getView().byId("SummaryPanel"));
			}
			this.mCurrentStep = 0;
			for(var i=0 ;i<this.mPanelList.length ; i++){
				this.mPanelList[i].setVisible(false);
			}
			this.mPanelList[this.mCurrentStep].setVisible(true);
		},
		onInit: function () {
			this.getRouter().getRoute("executiondetail").attachMatched(this.onRouteMatched, this);
		},
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			
			oView.setModel(this.mExecutionModel, "executionModel");
			oView.setModel(this._mTestRequestListModel, "testRequestListModel");
			this.initTestRequestListModel();
			
			oView.setModel(this._mTestScenarioListModel, "testScenarioListModel");
			oView.setModel(this._mPackageListModel, "packageListModel");
			oView.setModel(this._mTaskListModel, "taskListModel");
			oView.setModel(this._mTaskModel, "taskModel");
			this.mExecutionModel.setData({});
			this._mTestRequestListModel.setData({});
			this._mTestScenarioListModel.setData({});
			this._mPackageListModel.setData({});
			this._mTaskListModel.setData({});
			this._mTaskModel.setData({});
			this.initPackageListModel();
			
			oView.setModel(this._mHostListModel, "hostListModel");
			this.initPanelList();
			
			this.mSaveBtn = this.getView().byId("SaveBtn");
			this.mBackBtn = this.getView().byId("BackBtn");
			this.mNextBtn = this.getView().byId("NextBtn");
			
			this.mSaveBtn.setEnabled(false);
			this.mBackBtn.setEnabled(false);
			this.mNextBtn.setEnabled(true);
			
			var oArgs = oEvent.getParameter("arguments");
			var executionId = oArgs.executionId;
			if(!executionId){
				this.mOperation = 'add';
			}
			else{	//it's update operation
				this.mOperation = 'update';
				var that = this;
				$.ajax({
					url: this.getServiceUrl("/Execution/Get/") + executionId,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){					
						result.tasks.sort(function(a,b){
							return a.sn - b.sn;
						});
						that.mExecutionModel.setData(result);
						//parse task & host parameters
						var oData = that.mExecutionModel.getData();
						oData.hostsParameter = JSON.parse(oData.hostsParameterJson);
						oData.tasksParameter = JSON.parse(oData.tasksParameterJson);
						that.mExecutionModel.setData(oData);
						//retrieve test request
						$.ajax({
							url: that.getServiceUrl("/TestRequest/Get/") + oData.testRequestId,
							type: 'GET',
							contentType: 'application/json; charset=utf-8',
							success: function(result){
								var oTestRequest = result;
								oData.testRequest = oTestRequest;
								oData.testRequestName = oTestRequest.name;
								that.mExecutionModel.setData(oData);
								//retrieve project according to test request
								$.ajax({
									url: that.getServiceUrl("/Project/Get/") + oData.testRequest.projectId,
									type: 'GET',
									contentType: 'application/json; charset=utf-8',
									success: function(result){
										oData.project = result;
										//set the project parameter from ExecutionInfo to 'project'; ignore project's parameter template
										if(result.parameterTemplateJson){
											oData.projectParameters = JSON.parse(result.parameterTemplateJson);
											if(oData.projectParameters.parameters){
												oData.projectParameters.parameters.entries.sort(function(a, b){
													if(a.name < b.name)
														return -1;
													if(a.name > b.name)
														return 1;
													return 0;
												});
											}
										}
										else{
											oData.projectParameters = { entries: [] };
										}
										that.mExecutionModel.setData(oData);
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
							error: function(msg){
								MessageBox.error("Error resposne from server!", {
									title: "Error",
									actions:[MessageBox.Action.OK],
									defaultAction: MessageBox.Action.OK,
									details: JSON.stringify(msg, null, 4)
								});
							}
						});
						//retrieve scenario
						$.ajax({
							url: that.getServiceUrl("/Scenario/Get/") + oData.scenarioId,
							type: 'GET',
							contentType: 'application/json; charset=utf-8',
							success: function(result){
								var oScenario = result;
								oData.scenario = oScenario;
								oData.scenarioName = oScenario.name;
								that.mExecutionModel.setData(oData);
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
		onBack: function(){
			if(this.mCurrentStep-1 < 0){
				return;
			}

			this.mPanelList[this.mCurrentStep].setVisible(false);
			this.mCurrentStep--;
			this.mPanelList[this.mCurrentStep].setVisible(true);
			if(this.mCurrentStep == 0){
				this.mBackBtn.setEnabled(false);
			}
			this.mNextBtn.setEnabled(true);
//			//back to task list step
//			if(this.mCurrentStep == 2){
//				this._mTaskListUpdated = false;
//			}
			this.mSaveBtn.setEnabled(false);
		},
		onNext: function(){
			if(this.mCurrentStep+1 > this.mPanelList.length - 1){
				return ;
			}
			this.mPanelList[this.mCurrentStep].setVisible(false);
			this.mCurrentStep++;
			this.mPanelList[this.mCurrentStep].setVisible(true);
			if(this.mCurrentStep == this.mPanelList.length-1){
				this.mNextBtn.setEnabled(false);
			}
			this.mBackBtn.setEnabled(true);
			//go to TaskHost step, get the host parameters from server
			if(this.mCurrentStep == 3){
//				if(this._mTaskListUpdated){
					this.initHostsParameterModel();
//				}
			}
			//go to Task Parameter step,
			if(this.mCurrentStep == 4){
				//set tasks parameter model
//				if(this._mTaskListUpdated){
					this.initTasksParameterModel();
//				}
				//save hosts parameter to ExectionModel
				var oExecData = this.mExecutionModel.getData();
//				oExecData.hostsParameter = this._mHostsParameterModel.getData();
				this.mExecutionModel.setData(oExecData);
			}
			//go to last step
			if(this.mCurrentStep == 5){
				//save tasks parameter data to execution model
				var oExecData = this.mExecutionModel.getData();
				this.mExecutionModel.setData(oExecData);
				this.mSaveBtn.setEnabled(true);
			}
		},
		onSave: function(oEvent){
			var oExecData = this.mExecutionModel.getData();
			var that = this;
			oExecData.testRequestId = oExecData.testRequest.id;
			oExecData.scenarioId = oExecData.scenario.id;
			oExecData.projectParameterJson = JSON.stringify(oExecData.projectParameters);
			oExecData.hostsParameterJson = JSON.stringify(oExecData.hostsParameter);
			oExecData.tasksParameterJson = JSON.stringify(oExecData.tasksParameter);
			var url = "";
			if(oExecData.id){
				//update
				url = this.getServiceUrl('/Execution/Update');
			}
			else{
				//add
				url = this.getServiceUrl('/Execution/Add');
			}
			$.ajax({
				url: url,
				type:'POST',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(oExecData),
				success: function(result){
					//TODO: refresh UI properly
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
			})
		},
		
		//-----------------------1. Test Request Start ---------------------- 
		_mTestRequestSelectDialog: undefined,
		_mTestRequestListModel: new sap.ui.model.json.JSONModel(),
		
		initTestRequestListModel:function(){
			var that = this;
			$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/TestRequest/List',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success:function(result){
					that._mTestRequestListModel.setData({testRequests: result});
				},
				error: function(msg){
					MessageBox.error("Error resposne from server!", {
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, 4)
					});
				}
			})
		},
		initSelectedProjectModel: function(projectId){
			var that = this;
			if(projectId){
				$.ajax({
					url: this.getServiceUrl("/Project/Get/") + projectId,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						var oExecData = that.mExecutionModel.getData();
						oExecData.project = result;
						if(result.parameterTemplateJson){
							oExecData.projectParameters = JSON.parse(result.parameterTemplateJson);
						}
						else{
							oExecData.projectParameters = { entries: [] };
						}
						that.mExecutionModel.setData(oExecData);
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
        onChangeType: function(oEvent){
        	var select=this.getView().byId("TypeSelect");
        	var s=select.getSelectedKey();
        	this.mExecutionModel.getData().timeAnalyse=select.getSelectedKey();
//        	this.showMessage(select.getSelectedKey());
        },
		onSelectTestRequest: function(){
			if(!this._mTestRequestSelectDialog){
				this._mTestRequestSelectDialog = new sap.ui.jsfragment("sme.perf.ui.fragment.TestRequestSelectDialog", this);
			}
			this._mTestRequestSelectDialog.open();
		},
		onTestRequestSelectConfirm: function(oEvent){
			var aContexts = oEvent.getParameter("selectedContexts");
			if(aContexts.length){
				var testRequest = aContexts[0].getObject();
				var oExecData = this.mExecutionModel.getData();
				oExecData.testRequest = testRequest;
				oExecData.testRequestId = testRequest.id;
				oExecData.testRequestName = testRequest.name;
				this.mExecutionModel.setData(oExecData);
				//after the test request is specified, the project is set. so just enable to show the project's scenarios
				this.initTestScenarioListModel(testRequest.projectId);
				this.initSelectedProjectModel(testRequest.projectId);
			}
		},
		onTestRequestSelectSearch: function(oEvent){
			var sValue = oEvent.getParameter("value");
			var oFilters = [
			                new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("projectName", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("overallRequirement", sap.ui.model.FilterOperator.Contains, sValue)];
			var oBinding = oEvent.getSource().getBinding("items");
			oBinding.filter(new sap.ui.model.Filter({
				filters: oFilters,
				and: false
			}));
		},
		//-----------------------2. Test Scenario -----------------------
		_mTestScenarioSelectDialog: undefined,
		_mTestScenarioListModel: new sap.ui.model.json.JSONModel(),
//		_mSelectedTestScenarioModel: new sap.ui.model.json.JSONModel(),
		
		initTestScenarioListModel: function(projectId){
//			if(this._mTestScenarioListModel.getData().testScenarios == undefined){
				var that = this;
				$.ajax({
					url: that.getServiceUrl('/Scenario/List/' + projectId),
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success:function(result){
						that._mTestScenarioListModel.setData({testScenarios: result});
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
//			}
		},
		onSelectTestScenario: function(){
			if(this._mTestScenarioSelectDialog == undefined){
				this._mTestScenarioSelectDialog = new sap.ui.jsfragment("sme.perf.ui.fragment.TestScenarioSelectDialog", this);
			}
			this._mTestScenarioSelectDialog.open();
		},
		onTestScenarioSelectConfirm: function(oEvent){
			var aContexts = oEvent.getParameter("selectedContexts");
			if(aContexts.length){
				var selectedScenario = aContexts[0].getObject();
				var oExecData = this.mExecutionModel.getData();
				oExecData.scenario = selectedScenario;
				oExecData.scenarioId = selectedScenario.id;
				oExecData.scenarioName = selectedScenario.name;
				//copy the tasks from selected scenario's task list
				if(selectedScenario.taskListJson){
					var selectedTaskList = JSON.parse(selectedScenario.taskListJson);
					oExecData.tasks = selectedTaskList.tasks;
				}
				else{
					oExecData.tasks = [];
				}
				oExecData.tasks.sort(function(a,b){
					return a.sn - b.sn;
				});
				this.mExecutionModel.setData(oExecData);
			}
		},
		onTestScenarioSelectSearch: function(oEvent){
			var sValue = oEvent.getParameter("value");
			var oFilters = [
			                new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("projectName", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("description", sap.ui.model.FilterOperator.Contains, sValue)];
			var oBinding = oEvent.getSource().getBinding("items");
			oBinding.filter(new sap.ui.model.Filter({
				filters: oFilters,
				and: false
			}));
		},
		//-----------------------3. Task List-----------------------
		_mTaskModel: new sap.ui.model.json.JSONModel(),
		_mTaskListModel: new sap.ui.model.json.JSONModel(),		//for Task ComboBox
		_mPackageListModel: new sap.ui.model.json.JSONModel(),
//		_mExecutionTaskListModel: new sap.ui.model.json.JSONModel(), //for execution's task table
//		_mTaskListUpdated: true,	//used to identify whether the task list is updated (add/delete). if the task is updated, then the task parameterlist(host & parameter) will be refreshed.
		
		initPackageListModel: function(){
			var that = this;
			$.ajax({
				url: that.getServiceUrl('/Task/ListTaskLibrary'),
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
		onPackageSelectChange: function(oEvent){
			var selectedKey = oEvent.getParameters().value;
			this.initTaskListModel(selectedKey);
		},
		initTaskListModel: function(packageName){
			var oPackageListData = this._mPackageListModel.getData();
			for(var i=0 ; i<oPackageListData.packages.length; i++){
				if(oPackageListData.packages[i].name == packageName){
					this._mTaskListModel.setData({classes: oPackageListData.packages[i].classes});
				}
			}
		},
		onTaskAdd: function(oEvent){
			var oExecData = this.mExecutionModel.getData();
			if(oExecData.tasks == undefined){
				oExecData.tasks= [];
			}
			var oNewTask = this._mTaskModel.getData();
			var oTable = this.getView().byId("taskListTable");
			//clone a new entry
			var oNewTaskCopy = $.extend(true, {}, oNewTask);
			oNewTaskCopy.sn = oTable.getItems().length;			
			oExecData.tasks.push(oNewTaskCopy);
			this.mExecutionModel.setData(oExecData);
			//disconnect the TaskModel between ExecutionTaskListModel
			this._mTaskModel.setData($.extend(true, {}, oNewTaskCopy));
//			this._mTaskListUpdated = true;
		},
		onTaskUpdate: function(oEvent){
			var oExecData = this.mExecutionModel.getData();
			var oTaskData = this._mTaskModel.getData();
			//the 'sn' is also the array index number
			oExecData.tasks[oTaskData.sn] = $.extend(true, {}, oTaskData);
			this.mExecutionModel.setData(oExecData);
		},
		onTaskListMoveUp: function(oEvent){
			var selectedSn = parseInt(oEvent.getSource().oParent.mAggregations.cells[0].mProperties.text);
			var oExecData = this.mExecutionModel.getData();
			var oSelectedItemData = oExecData.tasks[selectedSn];
			if(selectedSn > 0){
				var oPreviousItemData = oExecData.tasks[selectedSn - 1];
				oExecData.tasks[selectedSn-1] = oSelectedItemData;
				oExecData.tasks[selectedSn] = oPreviousItemData;
				oSelectedItemData.sn = selectedSn -1 ;
				oPreviousItemData.sn = selectedSn;
				this.mExecutionModel.setData(oExecData);		
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
		onTaskListMoveDown: function(oEvent){
			var selectedSn = parseInt(oEvent.getSource().oParent.mAggregations.cells[0].mProperties.text);
			var oExecData = this.mExecutionModel.getData();
			var oSelectedItemData = oExecData.tasks[selectedSn];
			if(selectedSn +1 < oExecData.tasks.length){
				var oNextItemData = oExecData.tasks[selectedSn + 1];
				oExecData.tasks[selectedSn + 1] = oSelectedItemData;
				oExecData.tasks[selectedSn] = oNextItemData;
				oSelectedItemData.sn = selectedSn + 1;
				oNextItemData.sn = selectedSn;
				this.mExecutionModel.setData(oExecData);
				
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
		onTaskListDelete: function(oEvent){
			var oSelectedItemData = oEvent.getParameters().listItem.getBindingContext("executionModel").getObject();
			var oExecData = this.mExecutionModel.getData();
			//delete the selected one and decrease the sn for sn >= selected.sn
			oExecData.tasks.splice(oSelectedItemData.sn, 1);
			for(var i=oSelectedItemData.sn ; i<oExecData.tasks.length ; i++){
				oExecData.tasks[i].sn--;
			}
			this.mExecutionModel.setData(oExecData);
//			this._mTaskListUpdated = true;
		},
		onTaskListItemPress: function(oEvent){
			var oSelectedItemData = oEvent.getParameters().listItem.getBindingContext("executionModel").getObject();
			var oDuplicatedData = $.extend(true,{}, oSelectedItemData);
			this.initTaskListModel(oSelectedItemData.packageName);
			this._mTaskModel.setData(oDuplicatedData);
		},
		
		//-----------------------4. Host Parameter Start------------------
//		_mHostsParameterModel: new sap.ui.model.json.JSONModel(),
		_mHostListModel: new sap.ui.model.json.JSONModel(),
		_mSelectHostDialog: undefined,		
		_mSelectedHostParameterItem: undefined,
		
		initHostListModel:function(){
			var that = this;
			if(! this._mHostListModel.getData().hostList){
				$.ajax({
					url: this.getServiceUrl('/Host/List'),
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mHostListModel.setData({hostList: result});
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
		initHostsParameterModel: function(){
			var that = this;
			var tasksName = [];
			var oExecData = this.mExecutionModel.getData();
			for(var i=0 ; i<oExecData.tasks.length; i++){
				tasksName.push(oExecData.tasks[i].className);
			}
			if(tasksName.length > 0){
				$.ajax({
					url: this.getServiceUrl('/Task/GetTasksHostParameters'),
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(tasksName),
					success: function(result){
						//1. get the parameters from server, add the new ones, delete unnecesarry ones
						var oExecData = that.mExecutionModel.getData();
						if(! oExecData.hostsParameter){
							oExecData.hostsParameter = {parameters: {entries : [] }};
						}
						//delete the ones that not included in result
						if(oExecData.hostsParameter && oExecData.hostsParameter.parameters.entries){
							for(var i=0 ; i<oExecData.hostsParameter.parameters.entries.length ; i++){
								var index = -1;
								if(result.parameters && result.parameters.entries){
									for(var j=0 ; j<result.parameters.entries.length ; j++){
										if(oExecData.hostsParameter.parameters.entries[i].name == result.parameters.entries[j].name){
											index = i;
											break;
										}
									}
								}
								if(index == -1){
									oExecData.hostsParameter.parameters.entries.splice(i, 1);
									i--;
								}
							}
						}
						//add the new ones from result
						if(result.parameters && result.parameters.entries){
							for(var i=0 ; i<result.parameters.entries.length; i++){
								var index = -1;
								if(oExecData.hostsParameter && oExecData.hostsParameter.parameters.entries){
									for(var j=0 ; j<oExecData.hostsParameter.parameters.entries.length ; j++){
										if(oExecData.hostsParameter.parameters.entries[j].name == result.parameters.entries[i].name){
											index = j;
											break;
										}
									}
								}
								if(index == -1){
									oExecData.hostsParameter.parameters.entries.push(result.parameters.entries[i]);
								}
							}
						}
						that.mExecutionModel.setData(oExecData);
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
		onHostParameterItemPress: function(oEvent){
			this._mSelectedHostParameterItem = oEvent.getParameters().listItem.getBindingContext("executionModel").getObject()
			if(!this._mSelectHostDialog){
				//if it's the first time, then the get data from server and create the dialog.
				this.initHostListModel();
				this._mSelectHostDialog = new sap.ui.jsfragment("sme.perf.ui.fragment.SelectHostDialog", this);
			}
			this._mSelectHostDialog.open();
		},
		onHostSelectConfirm: function(oEvent){
			var selectedHosts = [];
			var aContexts = oEvent.getParameter("selectedContexts");
			for(var i=0 ; i< aContexts.length; i++){
				selectedHosts.push(aContexts[i].getObject());
			}
//			if(selectedHosts.length >  0 ){
				//update the value and refresh the UI.
				this._mSelectedHostParameterItem.value = selectedHosts;
				this.mExecutionModel.setData(this.mExecutionModel.getData());
//			}
		},
		onHostSelectSearch: function(oEvent){
			var sValue = oEvent.getParameter("value");
			var oFilters = [
			                new sap.ui.model.Filter("ip", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("operationSystem", sap.ui.model.FilterOperator.Contains, sValue),
			                new sap.ui.model.Filter("description", sap.ui.model.FilterOperator.Contains, sValue)];
			var oBinding = oEvent.getSource().getBinding("items");
			oBinding.filter(new sap.ui.model.Filter({
				filters: oFilters,
				and: false
			}));
		},
		//-----------------------4. Host Parameter End------------------
		//-----------------------5. Task Parameter Start---------------------
//		_mTasksParameterModel: new sap.ui.model.json.JSONModel(),
		initTasksParameterModel: function(){
			var that = this;
			var tasksName = [];
			var tasks = this.mExecutionModel.getData().tasks;
			for(var i=0 ; i<tasks.length; i++){
				tasksName.push(tasks[i].className);
			}
			if(tasksName.length > 0){
				$.ajax({
					url: this.getServiceUrl('/Task/GetTasksParameters'),
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(tasksName),
					success: function(result){
						var oExecData = that.mExecutionModel.getData();
						if(! oExecData.tasksParameter){
							oExecData.tasksParameter = { parameters: { entries: [] } };
						}
						//remove the ones not included in result
						if(oExecData.tasksParameter && oExecData.tasksParameter.parameters.entries){
							for(var i=0 ; i<oExecData.tasksParameter.parameters.entries.length ; i++){
								var found = false;
								if(result.parameters && result.parameters.entries){
									for(var j=0 ; j<result.parameters.entries.length ; j++){
										if(oExecData.tasksParameter.parameters.entries[i].name == result.parameters.entries[j].name){
											found = true;
											break;
										}
									}
								}
								if(found == false){
									oExecData.tasksParameter.parameters.entries.splice(i,1);
									i--;
								}
							}
						}
						//add the ones in result, but not in current data
						if(result.parameters && result.parameters.entries){
							for(var i=0 ; i<result.parameters.entries.length ; i++){
								var found =  false;
								if(oExecData.tasksParameter && oExecData.tasksParameter.parameters){
									for(var j=0 ; j<oExecData.tasksParameter.parameters.entries.length ; j++){
										if(result.parameters.entries[i].name == oExecData.tasksParameter.parameters.entries[j].name){
											found = true;
											break;
										}
									}
								}
								if(found == false){
									oExecData.tasksParameter.parameters.entries.push(result.parameters.entries[i]);
								}
							}
						}
						oExecData.tasksParameter.parameters.entries.sort(function(a, b){
							if(a.name < b.name)
								return -1;
							if(a.name > b.name)
								return 1;
							return 0;
						});
						that.mExecutionModel.setData(oExecData);
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
		//-----------------------5. Task Parameter End---------------------
		onDuplicate: function(oEvent){
			if(this.mExecutionModel.getData() &&
					this.mExecutionModel.getData().id) {
				var oData = this.mExecutionModel.getData();
				var that = this;
				$.ajax({
					url: this.getServiceUrl("/Execution/Duplicate/") + oData.id,
					type: 'GET',
					contentType: 'application/json',
					success: function(result){
						that.showMessage("Duplicated");
						var executionId = result.id;
						that.getRouter().navTo("executiondetail", { executionId: executionId });
					},
					error: function(msg){
						that.showServerErrorMessage(msg);
					}
				});
			}else{
				this.showMessage("Choose an Existing Execution first!");
			}
		}
	});
});
