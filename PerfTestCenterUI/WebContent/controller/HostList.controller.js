sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.HostList", {
		
		_mHostListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("hostlist").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			oView.setModel(this._mHostListModel, "hostListModel");
			this.refresh();
		},
		search: function(values){
			var that = this;
	        var filters = [];
	        for (var i = 0; i < values.length; i++) {
	            filters.push(that.createFilter(values[i]));
	        }
	        that.getView().byId("table").getBinding("items").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
		},
		refresh: function(clearHistory){
			var that=this;
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("table").getBinding("items").filter()
	        }
	        else {
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Host/List',
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mHostListModel.setData({hostList: result});
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
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("hostName", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("mode", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("IP", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("operationSystem", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    },
		onNew: function(){
			this.getRouter().navTo("hostdetail");
		},
		onItemPress: function(oEvent){
	        var hostId = oEvent.getParameters().listItem.getBindingContext("hostListModel").getProperty("id");
	        this.getRouter().navTo("hostdetail", { hostId: hostId });
		}
	});
});
