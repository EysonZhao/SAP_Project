sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.TestIssueReport", {
		
		mTestIssueListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("testissuereport").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			this.refresh();
		},
	    refresh: function (clearHistory) {
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("testIssueTable").getBinding("rows").filter()
	        }
	        else {
	        	this.getTestIssueList();
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
	        	this.getView().byId("testIssueTable").getBinding("rows").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
	        }
	    	else{
	    		this.refresh();
	    	}
	    },
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("projectName", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("requestName", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("jiraKey", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("creator", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("title", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("processor", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("priority", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("status", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
		getTestIssueList: function(){
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/Report/GetAllTestIssue"),
				type: 'GET',
				contentType: 'application/json',
				success: function(result){
					that.mTestIssueListModel.setData(result);
					var oTable = that.getView().byId("testIssueTable");
					oTable.setModel(that.mTestIssueListModel);
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
	    handleItemPressed: function (oEvent) {
	        window.open(oEvent.getParameters().rowContext.getProperty("url"));
	    },
	    handleShowDetail: function(oEvent){
	    	var oUrl = oEvent.getSource().getParent().getBindingContext().getProperty("url");
	    	window.open(oUrl);
	    },
	    onSync: function (oEvent){
	    	var that = this;
	    	var oTable = that.getView().byId("testIssueTable");
	    	var oSyncthreshold = 20;
	    	if(oTable.mBindingInfos.rows.binding.aIndices.length > oSyncthreshold){
		    	MessageBox.confirm("There are " + oTable.mBindingInfos.rows.binding.aIndices.length + " Jira Issues to be synced. It might take quite a long time. \nDo you want to continue?", {
	    			onClose: function(oAction){
	    				if(oAction == sap.m.MessageBox.Action.OK){
	    					that.syncJiraIssues();
	    				}
	    			}
		    	});
	    	}
	    	else{
				that.syncJiraIssues();
	    	}
	    },
	    syncJiraIssues: function(){
	    	var that = this;
	    	var oTable = that.getView().byId("testIssueTable");
            var oIdList = [];
            var oModelData = oTable.mBindingInfos.rows.binding.oModel.getData();
            for(var i=0 ; i<oTable.mBindingInfos.rows.binding.aIndices.length ; i++){
            	var index = oTable.mBindingInfos.rows.binding.aIndices[i];
            	oIdList.push(oModelData[index].id);
            }
            $.ajax({
            	url: that.getServiceUrl("/TestIssue/Sync"),
            	type: 'POST',
            	contentType: 'application/json',
            	data: JSON.stringify(oIdList),
            	success: function(result){
            		that.refresh();
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
            })
	    }
	});
});
