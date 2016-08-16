sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.TestRequestList", {
	
		onInit: function () {
			this.getRouter().getRoute("testrequestlist").attachMatched(this.onRouteMatched, this);
	    },
	    onRouteMatched: function (oEvent) {
	    	this.refresh();
	    },
	    refresh: function (clearHistory) {
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
//	            this.getView().byId("table").getBinding("items").filter()
	            this.getView().byId("sortableTestRequestTable").getBinding("rows").filter()
	        }
	        else {
	        	this.getTestRequestList();
	        }
	    },
	    operateSearch: function (values) {
	        var filters = [];
	        for (var i = 0; i < values.length; i++) {
	            filters.push(this.createFilter(values[i]));
	        }
//	        this.getView().byId("table").getBinding("items").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
	        this.getView().byId("sortableTestRequestTable").getBinding("rows").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
	    },
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("projectName", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("category", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("testOwner", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("latestStatus", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
	    getTestRequestList:function(){
	    	var that = this;
		   $.ajax({
				url:that.getConfig("serviceBaseUrl") + '/TestRequest/List',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						for(var i=0 ; i<result.length; i++){
							var oRequest = result[i];
							oRequest.issueCount = oRequest.issueList.length;
							oRequest.attachmentCount = oRequest.attachmentList.length;
						}
						var oModel = new sap.ui.model.json.JSONModel();
						oModel.setData({TestRequestList: result});
//						that.getView().byId("table").setModel(oModel);
						var oSortableTable = that.getView().byId("sortableTestRequestTable");
						oSortableTable.setModel(oModel);
						oSortableTable.bindRows("/TestRequestList");
						oSortableTable.sort(oSortableTable.getColumns()[0], sap.ui.table.SortOrder.Descending);
					}
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
	    },
	    handleItemPressed: function (oEvent) {
	        var requestId = oEvent.getParameters().rowContext.getProperty("id");
	        this.getRouter().navTo("testrequestdetail", { operation: "update", requestId: requestId });
	    },
	    onNew: function(){
	    	this.getRouter().navTo("testrequestdetail", {operation: "new"});
	    },
	    handleShowDetail: function(id){
	        this.getRouter().navTo("testrequestdetail", { operation: "update", requestId: id });
	    },
	    handleStatusUpdate: function(oEvent){
	        var requestId = oEvent.getSource().getParent().getBindingContext().getProperty("id");
	        this.getRouter().navTo("statusupdate", { requestId: requestId });
	    },
	    onIssueLinkClick: function(oEvent){
	        var requestId = oEvent.getSource().getParent().getBindingContext().getProperty("id");
	        this.getRouter().navTo("testissue", { requestId: requestId });
	    },
	    onAttachmentLinkClick: function(oEvent){
	    	var requestId = oEvent.getSource().getParent().getBindingContext().getProperty("id");
	        this.getRouter().navTo("testrequestattachment", { requestId: requestId });
	    }
	});
});