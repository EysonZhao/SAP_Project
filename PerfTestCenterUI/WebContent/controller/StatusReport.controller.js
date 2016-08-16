sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.StatusReport", {
		
		mQueryModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("statusreport").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			this.mQueryModel.setData({});
			oView.setModel(this.mQueryModel, "queryModel");
			var endDate = new Date(); 	//today
			var startDate = new Date();
			startDate.setDate(endDate.getDate() - 6); //one week ago
			this.mQueryModel.setData({
				startDate: this.formatter.formatDateObjectToDateTimeString(startDate),
				endDate:this.formatter.formatDateObjectToDateTimeString(endDate)
			});
		},
		onGenerateReport: function(oEvent){
			var that = this;
			$.ajax({
				url: that.getServiceUrl('/Report/StatusReport'),
				type: 'POST',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(that.mQueryModel.getData()),
				success: function(result){
					var oData = result.projectStatusReportList;
					var oView = that.getView();
					var oPanels = oView.getPanels();
					for(var i=0 ; i<oData.length ; i++){
						var model = new sap.ui.model.json.JSONModel();
						oData[i].blockedTestRequestsCount = oData[i].blockedTestRequests.length;
						oData[i].activeTestRequestsCount = oData[i].activeTestRequests.length;
						oData[i].planTestRequestsCount = oData[i].planTestRequests.length;
						model.setData(oData[i]);
						oView.setModel(model, "statusReportModel_" + i);
						oPanels[i].setVisible(true);
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
		onIssueItemPress: function(oEvent){
			var oIssue;
			for(var i=0 ; i<10 ; i++){
				if(oEvent.getParameters().listItem.getBindingContext("statusReportModel_" + i)){
					oIssue = oEvent.getParameters().listItem.getBindingContext("statusReportModel_" + i).getObject()
					break;
				}
			}
			if(oIssue){
				window.open(oIssue.url);
			}
		}
	});
});
