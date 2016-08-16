sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionGroupList", {
		
		mExecutionGroupListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("executiongrouplist").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			this.refresh();
		},
		
		refresh: function (clearHistory) {
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("executionGroupTable").getBinding("rows").filter()
	        }
	        else {
	        	this.getExecutionGroupList();
	        }
	    },
	    operateSearch: function () {
	    	var oSearchInput = this.getView().byId("search");
	    	var oTokens = oSearchInput.getTokens();
	    	if(oTokens.length > 0){
		    	var values=[];
		    	for(var i=0 ; i<oTokens.length ; i++){
		    		values.push(oTokens[i].getKey());
		    	}
		        var filters = [];
		        for (var i = 0; i < values.length; i++) {
		            filters.push(this.createFilter(values[i]));
		        }
	        	this.getView().byId("executionGroupTable").getBinding("rows").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
	        }
	    	else{
	    		this.refresh();
	    	}
	    },
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("state", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
	    getExecutionGroupList: function(){
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/ExecutionGroup/List"),
				type: 'GET',
				contentType: 'application/json',
				success: function(result){
					that.mExecutionGroupListModel.setData(result);
					var oTable = that.getView().byId("executionGroupTable");
					oTable.setModel(that.mExecutionGroupListModel);
					oTable.bindRows("/");
					oTable.sort(oTable.getColumns()[0], sap.ui.table.SortOrder.Descending);
					var oSearchInput = that.getView().byId("search");
			    	var oTokens = oSearchInput.getTokens();
			    	if(oTokens.length > 0){
			    		that.operateSearch();
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
		handleShowDetail: function(id){
	    	this.getRouter().navTo("executiongroupdetail", {executionGroupId: id});
	    },
	    onRunBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionGroupId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionGroupId'){
	    			executionGroupId = customData.getValue();
	    			break;
	    		}
	    	}
    		sap.m.MessageBox.confirm("Do you confirm to run the execution?", {
    			onClose: function(oAction){
    				if(oAction == sap.m.MessageBox.Action.OK){
    					that.runExecution(executionGroupId);
    				}
    			}
    		});
	    },
	    runExecution: function(executionGroupId){
			if(executionGroupId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/ExecutionGroup/Run/")+executionGroupId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					var oData = that.mExecutionGroupListModel.getData();
					if(oData && oData.length > 0){
						for(var i=0 ; i<oData.length ; i++){
							if(oData[i].id == result.id){
								oData[i] = result;
							}
						}
					}
					that.mExecutionGroupListModel.setData(oData);
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
	    onStopBtnPress: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var executionGroupId = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'executionGroupId'){
	    			executionGroupId = customData.getValue();
	    			break;
	    		}
	    	}
    		sap.m.MessageBox.confirm("Do you confirm to Stop the execution?", {
    			onClose: function(oAction){
    				if(oAction == sap.m.MessageBox.Action.OK){
    					that.stopExecution(executionGroupId);
    				}
    			}
    		});
	    },
	    stopExecution: function(executionGroupId){
			if(executionGroupId == 0){
				return;
			}
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/ExecutionGroup/Stop/")+executionGroupId,
				type: 'GET',
				contentType: 'application/json;charset=utf-8',
				success: function(result){
					var oData = that.mExecutionGroupListModel.getData();
					if(oData && oData.length > 0){
						for(var i=0 ; i<oData.length ; i++){
							if(oData[i].id == result.id){
								oData[i] = result;
							}
						}
					}
					that.mExecutionGroupListModel.setData(oData);
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
	    onNew: function(){
	    	this.getRouter().navTo("executiongroupdetail");
	    }
	});
});
