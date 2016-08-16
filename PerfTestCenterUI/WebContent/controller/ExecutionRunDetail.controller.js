sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	'sap/m/MessageBox'
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionRunDetail", {
		
		executionTaskInfoModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("executionrundetail").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			var oView = this.getView();
			this.getData(oArgs.executionId);

		},
		
		getData: function(executionId){
			var that = this;
			$.ajax({
				url:that.getServiceUrl("/Execution/Get/") + executionId,
				type: 'GET',
				contentType: 'application/json',
				success: function(result){
					that.executionTaskInfoModel.setData(result);
					var oTreeTable = that.getView().byId("table");
			        oTreeTable.setModel(that.executionTaskInfoModel);
			        oTreeTable.bindRows({
			            path: '/tasks',
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
	});
});
