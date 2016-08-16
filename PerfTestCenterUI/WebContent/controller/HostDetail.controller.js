sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.HostDetail", {
		
		_mHostModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("hostdetail").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			var oView = this.getView();
			oView.setModel(this._mHostModel, "hostModel");
			this.initHostModel(oArgs.hostId);
		},
		initHostModel: function(hostId){
			var that = this;
			if(hostId){
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Host/Get/' + hostId,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success:function(result){
						that._mHostModel.setData(result);
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
				this._mHostModel.setData({});
			}
		},
		onNew: function(){
			this._mHostModel.setData({});
		},
		onDuplicate: function(){
			var oHostData = this._mHostModel.getData();
			if(oHostData.id){
				oHostData.id = undefined;
				this._mHostModel.setData(oHostData);
				this.showMessage("Duplicated");
			}
		},
		onSave: function(){
			var that = this;
			var oHostData = this._mHostModel.getData();
			if(oHostData.id){		//update
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Host/Update',
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(oHostData),
					success: function(result){
						that._mHostModel.setData(result);
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
				})
			}
			else{	//add
				$.ajax({
					url: this.getConfig("serviceBaseUrl") + '/Host/Add',
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(oHostData),
					success: function(result){
						that._mHostModel.setData(result);
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
				})
			}
		}
	});
});
