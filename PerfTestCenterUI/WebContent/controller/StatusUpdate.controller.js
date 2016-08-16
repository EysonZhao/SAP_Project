sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.StatusUpdate", {
		
		_mTestRequestId:undefined,
//		_mTestRequest: {},
//		_mStatusUpdateList: {},
//		_mStatusUpdate: {},
		_mTestRequestModel: new sap.ui.model.json.JSONModel(),
		_mStatusUpdateModel: new sap.ui.model.json.JSONModel(),
		_mStatusUpdateListModel: new sap.ui.model.json.JSONModel(),
		_mStatusEnumModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("statusupdate").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			this._mTestRequestId = oArgs.requestId;
			var oView = this.getView();
			//init the status update to be empty
			this._mStatusUpdateModel.setData({status: ""});
			
			oView.setModel(this._mTestRequestModel, "testRequestModel");
			oView.setModel(this._mStatusUpdateModel, "statusUpdateModel");
			oView.setModel(this._mStatusUpdateListModel, "statusUpdateListModel");
			oView.setModel(this._mStatusEnumModel, "statusEnumModel");
			
			this._mStatusEnumModel.setData({status: Enums.status});
			this.getTestRequest(this._mTestRequestId);
			this.getStatusUpdateList(this._mTestRequestId);
		},
		getStatusUpdate: function(statusUpdateId){
			var that = this;
			$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/StatusUpdate/Get/' + statusUpdateId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						that._mStatusUpdateModel.setData(result);
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
		getStatusUpdateList: function(testRequestId){
			var that = this;
			$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/StatusUpdate/List/' + testRequestId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					if(result){
						that._mStatusUpdateListModel.setData({statusUpdateList: result});
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
		onNew: function(){
			this._mStatusUpdateModel.setData({});
			sap.ui.getCore().byId("statusComboBox").setSelectedKey("");
		},
		onSave: function(){
			var that = this;
			that._mStatusUpdateModel.getData().testRequestId = that._mTestRequestId;
			if(that._mStatusUpdateModel.getData().id){ //update
				$.ajax({
					url:this.getConfig("serviceBaseUrl") + '/StatusUpdate/Update',
					type:'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(that._mStatusUpdateModel.getData()),
					success: function(result){
						if(result){
							that._mStatusUpdateModel.setData(result);
							that.getStatusUpdateList(that._mTestRequestId);
							that.showMessage("Saved");
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
			}
			else{	//add
				$.ajax({
					url:this.getConfig("serviceBaseUrl") + '/StatusUpdate/Add',
					type:'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(that._mStatusUpdateModel.getData()),
					success: function(result){
						if(result){
							that._mStatusUpdateModel.setData(result);
							that.getStatusUpdateList(that._mTestRequestId);
							that.showMessage("Saved");
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
			}
		},
		onItemPress: function(oEvent){
			this._mStatusUpdateModel.setData(oEvent.getParameters().listItem.getBindingContext("statusUpdateListModel").getObject());
		}
	});
});
