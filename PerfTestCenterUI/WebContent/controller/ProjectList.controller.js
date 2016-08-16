sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ProjectList", {
		
		_mProjectListModel: {},
		
		onInit: function () {
			this.getRouter().getRoute("projectlist").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			this._mProjectListModel = new sap.ui.model.json.JSONModel();
			var oView = this.getView();
			oView.setModel(this._mProjectListModel, "projectListModel");
			
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
					url: this.getConfig("serviceBaseUrl") + '/Project/List',
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that._mProjectListModel.setData({projectList: result});
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
	            new sap.ui.model.Filter("name", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("description", sap.ui.model.FilterOperator.Contains, keyword),
	            new sap.ui.model.Filter("parameterTemplateJson", sap.ui.model.FilterOperator.Contains, keyword),
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
			this.getRouter().navTo("projectdetail");
		},
		
		onItemPress: function(oEvent){
	        var projectId = oEvent.getParameters().listItem.getBindingContext("projectListModel").getProperty("id");
	        this.getRouter().navTo("projectdetail", { projectId: projectId });
		}
	});
});
