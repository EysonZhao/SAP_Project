sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ProjectDetail", {
		
		_mProjectModel: {},
		_mParameterModel: {},
		_mParameterListModel: {},
		onInit: function () {
			this.getRouter().getRoute("projectdetail").attachMatched(this.onRouteMatched, this);

		},
		
		onRouteMatched: function(oEvent){
			var oView = this.getView();
			this._mProjectModel = new sap.ui.model.json.JSONModel();
			oView.setModel(this._mProjectModel, "projectModel");
			
			this._mParameterModel = new sap.ui.model.json.JSONModel();
			oView.setModel(this._mParameterModel, "parameterModel");
			
			this._mParameterListModel = new sap.ui.model.json.JSONModel();
			oView.setModel(this._mParameterListModel, "parameterListModel");
			
			var oArgs = oEvent.getParameter("arguments");
			if(undefined == oArgs.projectId){	//add
				this._mProjectModel.setData({});
				this._mParameterModel.setData({});
				this._mParameterListModel.setData({});
			}
			else {	//show project info
				var that = this;
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Project/Get/' + oArgs.projectId,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that.bindModel(result);
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
		bindModel: function(result){
			this._mProjectModel.setData(result);
			if(this._mProjectModel.getData().parameterTemplateJson){
				var oParam = JSON.parse(result.parameterTemplateJson);
				this._mParameterListModel.setData(oParam);
			}
			else{
				this._mParameterListModel.setData({});
			}			
		},
		onNew: function(){
			var oData = this._mParameterListModel.getData();
			if(oData.parameters == undefined){
				oData.parameters = {entries: [] };
			}
			oData.parameters.entries.push({description:"", name: "", value: ""});
			this._mParameterListModel.setData(oData);
		},
		onDelete: function(oEvent){
	    	var oTable = sap.ui.getCore().byId(oEvent.getParameters().id);
	    	var selectedItem = oEvent.getParameters().listItem;
	    	var oItems = oTable.getItems();
	    	for(var i=0 ; i<oItems.length; i++){
	    		if(oItems[i] == selectedItem){
	    			var oData = this._mParameterListModel.getData();
	    			oData.parameters.entries.splice(i, 1);
	    			this._mParameterListModel.setData(oData);
	    		}
	    	}
		},
		onSave: function(){
			var that = this;
			if(this._mProjectModel.getData().id == undefined){	//check whether add the project info
				var oData = that._mProjectModel.getData();
				oData.parameterTemplateJson = JSON.stringify(that._mParameterListModel.getData());
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Project/Add',
					type: 'POST',
					contentType:'application/json; charset=utf-8',
					data: JSON.stringify(oData),
					success: function(result){
						that.bindModel(result);
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
			}
			else{
				var oData = that._mProjectModel.getData();
				oData.parameterTemplateJson = JSON.stringify(that._mParameterListModel.getData());
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Project/Update',
					type: 'POST',
					contentType:'application/json; charset=utf-8',
					data: JSON.stringify(this._mProjectModel.getData()),
					success: function(result){
						that.bindModel(result);
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
			}
		},
		onItemPress: function(oEvent){
			this._mParameterModel.setData(oEvent.getParameters().listItem.getBindingContext("parameterListModel").getObject());
		},
	});
});
