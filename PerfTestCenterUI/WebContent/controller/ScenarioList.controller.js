sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ScenarioList", {
		
		_mScenarioListModel: {},
		
		onInit: function () {
			this.getRouter().getRoute("scenariolist").attachMatched(this.onRouteMatched, this);

		},
		
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			this._mScenarioListModel = new sap.ui.model.json.JSONModel();
			oView.setModel(this._mScenarioListModel, "scenarioListModel");
			
			var oArgs = oEvent.getParameter("arguments");
			this.refresh();
		},
		refresh: function(clearHistory){
			var that=this;
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("table").getBinding("items").filter()
	        }
	        else {
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Scenario/List',
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mScenarioListModel.setData({scenarioList: result});
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
		},
		search: function(values){
			var that = this;
	        var filters = [];
	        for (var i = 0; i < values.length; i++) {
	            filters.push(that.createFilter(values[i]));
	        }
	        that.getView().byId("table").getBinding("items").filter(new sap.ui.model.Filter({ filters: filters, and: true }));
		},
	    createFilter: function (keyword) {
	        var keywordAsNumber = parseInt(keyword);
	        var filters = [
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("description", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("project", sap.ui.model.FilterOperator.Contains, keyword),
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
			this.getRouter().navTo("scenariodetail");
		},
		onItemPress: function(oEvent){
	        var scenarioId = oEvent.getParameters().listItem.getBindingContext("scenarioListModel").getProperty("id");
	        this.getRouter().navTo("scenariodetail", { scenarioId: scenarioId });
		}
	});
});
