sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.TestIssue", {
		
		mTestRequestModel: new sap.ui.model.json.JSONModel(),
		mStatusListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("testissue").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			
			var oView = this.getView();
			oView.setModel(this.mTestRequestModel, "testRequestModel");
			oView.setModel(this.mStatusListModel, "statusListModel");
			this.mStatusListModel.setData(Enums.issueStatus);
			var oArgs = oEvent.getParameter("arguments");
			if(oArgs.requestId){
				this.initTestRequest(oArgs.requestId);
			}
		},
		initTestRequest: function(requestId){
			var that = this;
			$.ajax({
				url: that.getServiceUrl("/TestIssue/List/") + requestId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					that.mTestRequestModel.setData(result);
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
		onNew: function(oEvent){
			var oData = this.mTestRequestModel.getData();
			if(!oData.issueList){
				oData.issueList = [];
			}
			oData.issueList.push({});
			this.mTestRequestModel.setData(oData);
		},
		onSave: function(oEvent){
			var that = this;
			$.ajax({
				url: that.getServiceUrl("/TestIssue/Update"),
				type: 'POST',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(that.mTestRequestModel.getData()),
				success: function(result){
					that.mTestRequestModel.setData(result);
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
		},
		onItemPress: function(oEvent){
			var oIssue = oEvent.getParameters().listItem.getBindingContext("testRequestModel").getObject()
			window.open(oIssue.url);
		}
	});
});
