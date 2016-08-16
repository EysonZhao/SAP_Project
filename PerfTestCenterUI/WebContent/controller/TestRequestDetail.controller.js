sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.TestRequestDetail", {
		
		operation: "update",
		oTestRequest: {},
		oProjectList: undefined,
		_mTestRequestModel: new sap.ui.model.json.JSONModel({}),
		
		onInit: function () {
			this.getRouter().getRoute("testrequestdetail").attachMatched(this.onRouteMatched, this);
		}, 
		initCategoryComboBox:function(){
			var oCategoryModel = new sap.ui.model.json.JSONModel({categories: Enums.category});
			this.getView().setModel(oCategoryModel, "categoryModel");
		},
		initPriorityComboBox: function(){
			var oPriorityModel = new sap.ui.model.json.JSONModel({priorities: Enums.priority});
			this.getView().setModel(oPriorityModel, "priorityModel");
		},
		onRouteMatched: function(oEvent){
			this.initCategoryComboBox();
			this.initPriorityComboBox();
			this.initProjectComboBox();
			this.getView().setModel(this._mTestRequestModel, "testRequestModel");
			
			var oArgs = oEvent.getParameter("arguments");
			this.operation = oArgs.operation;			
			switch(this.operation){
			case "new":
				this._mTestRequestModel.setData({});
				break;
			case "update":
				if(oArgs.requestId){
					this.getTestRequest(oArgs.requestId);
				}
				break;
			}
		},
		initProjectComboBox: function(){
			if(this.oProjectList){
				return;
			}
			var that = this;
			$.ajax({
				url:this.getConfig("serviceBaseUrl") + '/Project/List',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						var oProjectListModel = new sap.ui.model.json.JSONModel();
						oProjectListModel.setData({projects:result});
						that.getView().setModel(oProjectListModel, "projectsModel");
					}
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
			
			$.ajax({
				url:this.getConfig("serviceBaseUrl") + '/TestRequest/GetCodeline',
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						var oAllIdsModel = new sap.ui.model.json.JSONModel();
						oAllIdsModel.setData(result);
						that.getView().setModel(oAllIdsModel, "AllIdsModel");
					}
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
		getTestRequest: function(requestId){
			var that = this;
			$.ajax({
				url:this.getConfig("serviceBaseUrl") + '/TestRequest/Get/' + requestId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						that._mTestRequestModel.setData(result);
					}
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
		onSave: function(){
			var that = this;
			if(that._mTestRequestModel.getData().id){
				that._mTestRequestModel.getData().codeline = that.getView().byId('combobox2').getSelectedKey();
				$.ajax({
					type : "POST",
					contentType : "application/json; charset=utf-8",
					url : this.getConfig("serviceBaseUrl") + '/TestRequest/Update',
					data : JSON.stringify(that._mTestRequestModel.getData()),
					dataType : 'json',
					success : function(result) {
						that.showMessage("Saved");
					},
					error : function(msg) {
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
				$.ajax({
					type : "POST",
					contentType : "application/json; charset=utf-8",
					url : this.getConfig("serviceBaseUrl") + '/TestRequest/Add',
					data : JSON.stringify(that._mTestRequestModel.getData()),
					dataType : 'json',
					success : function(result) {
						that._mTestRequestModel.setData(result);
						that.showMessage("Saved");
					},
					error : function(msg) {
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
		onDuplicate: function(){
			var that = this;
			var requestId = that._mTestRequestModel.getData().id; 
			if(requestId){
				$.ajax({
					url: that.getServiceUrl('/TestRequest/Duplicate/') + requestId,
					type: 'GET',
					contentType: 'application/json',
					success: function(result){
						that._mTestRequestModel.setData(result);
						that.showMessage("Duplicated");
					},
					error: function(msg){
						MessageBox.error("Error resposne from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					}
				})
			}
			else{
				that.showMessage("please try to save first or try to duplicate a exsiting test one");
			}
		},
		onNew: function(){
			var that = this;
			that._mTestRequestModel.setData({});
		}
	});
});