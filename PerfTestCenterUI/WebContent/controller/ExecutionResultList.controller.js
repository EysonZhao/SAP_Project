sap.ui.define([
	"sme/perf/ui/controller/BaseController"
], function (BaseController) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionResultList", {
		
		_mExecutionListModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("executionresultlist").attachMatched(this.onRouteMatched, this);
		},
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			this._mScenarioListModel = new sap.ui.model.json.JSONModel();
			oView.setModel(this._mExecutionListModel, "executionListModel");
			
			var oArgs = oEvent.getParameter("arguments");
			this.refresh();
		},
		onItemPress: function(oEvent){
			var executionId = oEvent.getParameters().listItem.getBindingContext("executionListModel").getProperty("id");
			this.getRouter().navTo("executionresultdetail", {executionId: executionId});
		},
		refresh: function(clearHistory){
			var that=this;
	        clearHistory = (typeof clearHistory !== 'undefined') ? clearHistory : false;
	        if (clearHistory) {
	            this.getView().byId("table").getBinding("items").filter()
	        }
	        else {
				$.ajax({
					//Only list 'Finished' execution, or list all execution?
//					url: this.getServiceUrl('/Execution/ListFinished'),
					url: this.getServiceUrl('/Execution/List'),
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mExecutionListModel.setData(result);
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
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword)
	        ];
	        if (!isNaN(keywordAsNumber)) {
	            filters.push(new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.EQ, keywordAsNumber));
	        };
	        return new sap.ui.model.Filter({
	            filters: filters,
	            and: false
	        });
	    }
	});
});
