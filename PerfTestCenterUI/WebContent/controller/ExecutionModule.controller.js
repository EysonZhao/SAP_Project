sap.ui.define([
	"sme/perf/ui/controller/BaseController"
], function (BaseController) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionModule", {
		
		mMyModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("executionmodule").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			var oView = this.getView();
			oView.setModel(this.mMyModel, "model_name");
			
		},
	});
});
