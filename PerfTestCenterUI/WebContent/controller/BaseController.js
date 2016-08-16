sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History",
	"sme/perf/ui/model/formatter",
	"sap/m/MessageToast",
	"sap/m/MessageBox"
], function (Controller, History, formatter, MessageToast, MessageBox) {
	"use strict";
	return Controller.extend("sme.perf.ui.controller.BaseController", {
		formatter: formatter,
		globalConfigModel: undefined,
		
		getRouter : function () {
			return sap.ui.core.UIComponent.getRouterFor(this);
		},
		onNavBack: function (oEvent) {
			var oHistory, sPreviousHash;
			oHistory = History.getInstance();
			sPreviousHash = oHistory.getPreviousHash();
			if (sPreviousHash !== undefined) {
				window.history.go(-1);
			} else {
				this.getRouter().navTo("appHome", {}, true /*no history*/);
			}
		},
		getConfig: function(key){
			if(undefined == this.globalConfigModel){
				this.globalConfigModel = this.getView().getModel("globalConfigModel");
			}
			var oData = this.globalConfigModel.getData();
			return oData[key];
		},
		getServiceUrl: function(relativeUrl){
			return this.getConfig("serviceBaseUrl") + relativeUrl;
		},
		showMessage: function(message){
			MessageToast.show(message);
		},
		showServerErrorMessage: function(msg){
			MessageBox.error("Error resposne from server!", {
				title: "Error",
				actions:[MessageBox.Action.OK],
				defaultAction: MessageBox.Action.OK,
				details: JSON.stringify(msg, null, 4)
			});
		}
	});
});