sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionList", {
		
		_mExecutionListModel: new sap.ui.model.json.JSONModel(),
		mNewControlModel: new sap.ui.model.json.JSONModel(),
		mShowOptionModel: new sap.ui.model.json.JSONModel(),
		mFilterModel : new sap.ui.model.json.JSONModel(),
		mQueryModel : new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("executionlist").attachMatched(this.onRouteMatched, this);

		},
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			this._mScenarioListModel = new sap.ui.model.json.JSONModel();
			this.mNewControlModel= new sap.ui.model.json.JSONModel();
			oView.setModel(this._mExecutionListModel, "executionListModel");
			oView.setModel(this.mNewControlModel,"newControlModel");
			oView.setModel(this.mShowOptionModel,"ShowOptionModel");
			oView.setModel(this.mFilterModel,"filterModel");
			oView.setModel(this.mQueryModel,"queryModel");
			
			var endDate = new Date(); 	//today
			var startDate = new Date();
			startDate.setDate(endDate.getDate() - 180); //15 days ago
			this.mQueryModel.setData({
					state:"(sme.perf.execution.State.New,sme.perf.execution.State.Ready,sme.perf.execution.State.Running," +
						"sme.perf.execution.State.Finished,sme.perf.execution.State.Failed,sme.perf.execution.State.Terminated," +
						"sme.perf.execution.State.Paused)",
					dateType:"createDate",
					from:this.formatter.formatDateObjectToDateTimeString(startDate),
					to:this.formatter.formatDateObjectToDateTimeString(endDate) });
			this.mShowOptionModel.setData({show:false});
			this.mNewControlModel.setData({uploadEnable:false,newEnable:false,id:0});
			this.mFilterModel.setData({New:true,Ready:true,Running:true,Finished:true,Failed:true,Terminated:true,Paused:true});
			var oArgs = oEvent.getParameter("arguments");
			this.refresh();
		},
		onNew: function(){
			this.getRouter().navTo("executiondetail");
		},
		onItemPress: function(oEvent){
			var executionId = oEvent.getParameters().listItem.getBindingContext("executionListModel").getProperty("id");
			this.getRouter().navTo("executiondetail", {executionId: executionId});
		},
		refresh: function(clearHistory){
			var that=this;
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("table").getBinding("items").filter()
	        }
	        else {
				$.ajax({
					url: this.getServiceUrl('/Execution/List'),
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mExecutionListModel.setData(result);
					},
					error: function(msg){
						MessageBox.error("Error resposne from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					},
					beforeSend: function(){
						that.getView().setBusy(true);
					},
					complete: function(){
						that.getView().setBusy(false);
					}
				});
	        }
		},
		search: function(values){
			var that = this;
	        var filters = [];
	        for (var i = 0; i < values.length; i++) {
	            filters.push(that.createFilter(values[i]));
	        }
	        that.getView().byId("table").getBinding("items").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
		},
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
	    onRunBtnPress: function(oEvent){	    	
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}	    	
    		sap.m.MessageBox.confirm("Do you confirm to run the execution?", {
    			onClose: function(oAction){
    				if(oAction == sap.m.MessageBox.Action.OK){
    					that.runExecution(executionId);
    				}
    			}
    		});
	    },
	    onStopBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
    		sap.m.MessageBox.confirm("Do you confirm to STOP the execution?", {
    			onClose: function(oAction){
    				if(oAction == sap.m.MessageBox.Action.OK){
    					that.stopExecution(executionId);
    				}
    			}
    		});
	    },
	    onPauseBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
    		that.pauseExecution(executionId);
	    	that.showMessage("The execution is paused!")
	    },
	    onContinueBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
    		that.continueExecution(executionId);
	    	that.showMessage("The execution is continued!")
	    },
	    onLogBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
			var oLogUrl = this.getServiceUrl('/Download/ExcutionLog/') + executionId;
			window.open(oLogUrl);
	    },
	    runExecution: function(executionId){
			if(executionId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/Execution/Run/")+executionId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					var oData = that._mExecutionListModel.getData();
					if(oData && oData.length > 0){
						for(var i=0 ; i<oData.length ; i++){
							if(oData[i].id == result.id){
								oData[i] = result;
							}
						}
					}
					that._mExecutionListModel.setData(oData);
				},
				error: function(msg){
					MessageBox.show("Error resposne from server!", {
						icon: MessageBox.Icon.ERROR,
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, "\t")
					});
				}
			});
	    },
	    stopExecution: function(executionId){
			if(executionId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/Execution/Stop/")+executionId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					that.showMessage(result);
					that.refresh();
				},
				error: function(msg){
					MessageBox.show("Error resposne from server!", {
						icon: MessageBox.Icon.ERROR,
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, "\t")
					});
				}
			});
	    },
	    pauseExecution: function(executionId){
			if(executionId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/Execution/Pause/")+executionId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					that.showMessage(result);
					that.refresh();
				},
				error: function(msg){
					MessageBox.show("Error resposne from server!", {
						icon: MessageBox.Icon.ERROR,
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, "\t")
					});
				}
			});
	    },
	    continueExecution: function(executionId){
			if(executionId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/Execution/Continue/")+executionId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					that.showMessage(result);
					that.refresh();
				},
				error: function(msg){
					MessageBox.show("Error resposne from server!", {
						icon: MessageBox.Icon.ERROR,
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, "\t")
					});
				}
			});
	    },
	    onExportBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
			var oLogUrl = this.getServiceUrl('/Download/ExecutionExport/') + executionId;
			window.open(oLogUrl);
	    },
	    onImportBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
//			var oUploadUrl = this.getServiceUrl('/UploadExecutionResult.html/?executionId=') + executionId;
//			window.open(oUploadUrl);
	    	that.mNewControlModel.setData({uploadEnable:true,newEnable:false,id:executionId});
	    	that.getView().byId("fileupload").clear();
	    },
	    onDurationBtnPress: function(oEvent){
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionId'){
	    			executionId = customData.getValue();
	    			break;
	    		}
	    	}
			if(0 != executionId){
				this.getRouter().navTo("executionrundetail", {executionId: executionId});
			}			
	    },
	    onUploadFile: function(fileUploader){
	    	var id = this.mNewControlModel.getData().id;
			if(id){
				fileUploader.setUploadUrl(this.getServiceUrl("/Upload/ExecutionResult/"+id));
				fileUploader.upload();
			}	
	    },
		uploadContentChanged:function(){
			this.getView().byId("UploadButton").setEnabled(true);			
		},
		
        onChangePeriod: function(oEvent){
        	var select=this.getView().byId("dateTypeSelect");
        	var s=select.getSelectedKey();
        },
	    
		onFilter: function(){
			this.mQueryModel.getData().dateType=this.getView().byId("dateTypeSelect").getSelectedKey();
			var state = "(";
			if(this.mFilterModel.getData().New == true){state+="sme.perf.execution.State.New,";}
			if(this.mFilterModel.getData().Ready == true){state+="sme.perf.execution.State.Ready,";}
			if(this.mFilterModel.getData().Running == true){state+="sme.perf.execution.State.Running,";}
			if(this.mFilterModel.getData().Finished == true){state+="sme.perf.execution.State.Finished,";}
			if(this.mFilterModel.getData().Failed == true){state+="sme.perf.execution.State.Failed,";}
			if(this.mFilterModel.getData().Terminated == true){state+="sme.perf.execution.State.Terminated,";}
			if(this.mFilterModel.getData().Paused == true){state+="sme.perf.execution.State.Paused,";}
			state=state.substring(0,state.length-1);
			state+=")";
			this.mQueryModel.getData().state=state;
//			this.showMessage(JSON.stringify(this.mQueryModel.getData()));
			var that=this;
			$.ajax({
				url: this.getServiceUrl("/Execution/Filter"),
				type: 'POST',
				contentType: 'application/json;charset=utf-8',
				data: JSON.stringify(that.mQueryModel.getData()),
				success: function(result){
					that._mExecutionListModel.setData(result);
				},
				error: function(msg){
					MessageBox.show("Error resposne from server!", {
						icon: MessageBox.Icon.ERROR,
						title: "Error",
						actions:[MessageBox.Action.OK],
						defaultAction: MessageBox.Action.OK,
						details: JSON.stringify(msg, null, "\t")
					});
				}
			});
		}
		
	});
});
