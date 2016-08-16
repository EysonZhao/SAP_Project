sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox",
	"sap/viz/ui5/controls/VizFrame",
	"sap/viz/ui5/data/FlattenedDataset",
	"sap/viz/ui5/controls/common/feeds/FeedItem"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.AnalysisTransaction", {
		
		_mTransactionModel: new sap.ui.model.json.JSONModel(),
		_mAllSessionIdsModel:new sap.ui.model.json.JSONModel(),
		_mUsersModel:new sap.ui.model.json.JSONModel(),
		_mThroughputModel:new sap.ui.model.json.JSONModel(),
		mQueryModel:new sap.ui.model.json.JSONModel(),
		mSelectModel:new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("analysistransaction").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			
			this.getView().setModel(this._mTransactionModel,"TransactionModel");
			this.getView().setModel(this._mAllSessionIdsModel,"AllIdsModel");
			this.getView().setModel(this._mUsersModel,"usersModel");
			this.getView().setModel(this._mThroughputModel,"ThroughputModel");
			this.getView().setModel(this.mQueryModel,"QueryModel");
			this.getView().setModel(this.mSelectModel,"SelectModel");
			
			var data = [
					 {"date":"0","userNumber":"5"},
					 {"date":"1","userNumber":"7"},
					 {"date":"6","userNumber":"1"},
					 {"date":"7","userNumber":"2"},
					 {"date":"17","userNumber":"9"},
					 {"date":"20","userNumber":"4"},
					 {"date":"10","userNumber":"8"},
					 ];
			
			this._mUsersModel.setData(data);
			this.mSelectModel.setData({select:false});
			
			var that = this;
			$.ajax({
				url: that.getServiceUrl("/AnalysisTransaction/GetsessionIds"),
	    		type: 'GET',
	    		contentType: 'application/json; charset=utf-8',
	    		success: function(result){
					that._mAllSessionIdsModel.setData(result);
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
		},
		onShow: function(){
			var that=this;
			var sid = this.getView().byId("combobox").getSelectedKey();

			if(this.mSelectModel.getData().select ==false){
				$.ajax({
					url: that.getServiceUrl("/AnalysisTransaction/Get/"+sid),
		    		type: 'GET',
		    		contentType: 'application/json; charset=utf-8',
		    		success: function(result){
						that._mTransactionModel.setData(result);
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
			else{
				$.ajax({
					url: that.getServiceUrl("/AnalysisTransaction/Getfromview/"+sid),
		    		type: 'GET',
		    		contentType: 'application/json; charset=utf-8',
		    		success: function(result){
						that._mTransactionModel.setData(result);
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
		onChart: function(){
			var that=this;
			var sid = this.getView().byId("combobox").getSelectedKey();
			$.ajax({
				url: that.getServiceUrl("/AnalysisTransaction/Users/"+sid),
	    		type: 'GET',
	    		contentType: 'application/json; charset=utf-8',
	    		success: function(result){
					that._mUsersModel.setData(result);
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
		},
		onThrouput: function(){			
			this.mQueryModel.setData({granularity:this.getView().byId("input1").getValue(),
					transactionName:this.getView().byId("input2").getValue(),
					resultSessionId:this.getView().byId("combobox2").getValue(),
			});
			var that=this;
			var sid = this.getView().byId("combobox").getSelectedKey();
			$.ajax({
				url: that.getServiceUrl("/AnalysisTransaction/Throughput"),
	    		type: 'POST',
	    		data: JSON.stringify(that.mQueryModel.getData()),
	    		contentType: 'application/json; charset=utf-8',
	    		success: function(result){
					that._mThroughputModel.setData(result);
					that.showMessage("Done!");
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
		
	});
});