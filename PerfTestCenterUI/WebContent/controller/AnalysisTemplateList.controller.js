sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.AnalysisTemplateList", {
		
		mTemplateListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("analysistemplatelist").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			this.refresh();
		},
	    refresh: function (clearHistory) {
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("Table").getBinding("rows").filter()
	        }
	        else {
	        	this.getAnalysisTemplateList();
	        }
	    },
	    getAnalysisTemplateList: function(){
	    	var that = this;
	    	$.ajax({
	    		url: that.getServiceUrl("/AnalysisTemplate/List"),
	    		type: 'GET',
	    		contentType: 'application/json; charset=utf-8',
	    		success: function(result){
					that.mTemplateListModel.setData(result);
					var oTable = that.getView().byId("Table");
					oTable.setModel(that.mTemplateListModel);
					oTable.bindRows("/");
					oTable.sort(oTable.getColumns()[1], sap.ui.table.SortOrder.Descending);
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
	        	this.getView().byId("Table").getBinding("rows").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
	        }
	    	else{
	    		this.refresh();
	    	}
	    },
	    
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("owner", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("description", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
		handleShowDetail: function(oEvent){
	    	var id = oEvent.getSource().getParent().getBindingContext().getProperty("id");
	    	this.getRouter().navTo("analysistemplatedetail", { id: id });
		},
		onNew: function(oEvent){
	    	this.getRouter().navTo("analysistemplatedetail");
		},
		onTest: function(oEvent){
			this.getRouter().navTo("analysistransaction");
		},
		onDownload: function(oEvent){
    		var that = this;
    		var customDataList = oEvent.getSource().getCustomData();
    		var id = 0;
	    	for(var i=0 ; i<customDataList.length ; i++){
	    		var customData = customDataList[i];
	    		if(customData.getKey() == 'Id'){
	    			id = customData.getValue();
	    			break;
	    		}
	    	}
	    	if(id == 0){
	    		return;
	    	}
			
			window.open(this.getServiceUrl("/AnalysisTemplate/Download/")+id);
		}
	});
});
