sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionResultDetail", {
		
		mExecutionResultModel: new sap.ui.model.json.JSONModel(),
		mImportSummaryModel: new sap.ui.model.json.JSONModel(),
		mExecutionId: undefined,
		mRefreshInterval: undefined,
		
		onInit: function () {
			this.getRouter().getRoute("executionresultdetail").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			var oView = this.getView();
			oView.setModel(this.mExecutionResultModel, "executionResultModel");
			oView.setModel(this.mImportSummaryModel, "importSummaryModel");
			
			this.mExecutionId = oArgs.executionId;
			if(this.mExecutionId){
				this.loadResultList();
				this.getImportSummary();
			}
		},
		
		loadResultList: function(){
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/TestResult/Get/") + that.mExecutionId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					that.mExecutionResultModel.setData({results: result});
				},
				error: function(msg){
					that.showServerErrorMessage(msg);
				}
			})
		},
		//start to import result
		onImport: function(oEvent){
			var that = this;
			MessageBox.confirm("Import result might task several minutes. \nYou could click 'Refresh' button to refresh. \nDo you want to continue?", {
    			onClose: function(oAction){
    				if(oAction == sap.m.MessageBox.Action.OK){
    					var oPath = that.mImportSummaryModel.getData().path;
    					$.ajax({
    						url: that.getServiceUrl("/TestResult/Import/") + that.mExecutionId,
    						type: 'POST',
    						data: oPath,
    						contentType: 'application/json; charset=utf-8',
    						success: function(result){
    							
    						},
    						error: function(msg){
    							that.showServerErrorMessage(msg);
    						}
    					});
    				}
    			}
    		});
		},
		//refresh the import summary
		onRefresh: function(oEvent){
			this.loadResultList();
			this.getImportSummary();
		},
		
		getImportSummary: function(){
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/TestResult/GetImportSummary/") + that.mExecutionId,
				type: 'GET',
				contentType: 'application/json; charset=utf-8',
				success: function(result){
					var oData = that.mImportSummaryModel.getData();
					oData.results = result;
					that.mImportSummaryModel.setData(oData);
				},
				error: function(msg){
					that.showServerErrorMessage(msg);
				}
			});
			
		},
		autoRefresh : function(){
			this.showMessage("Auto Refreshed");
			this.loadResultList();
			this.getImportSummary();
		},
		onAutoRefreshOn: function(oEvent){
			if(undefined === this.mRefreshInterval){
				this.mRefreshInterval = setInterval(this.autoRefresh.bind(this), 5000);
			}
		},
		onAutoRefreshOff: function(oEVent){
			if(undefined !== this.mRefreshInterval){
				clearInterval(this.mRefreshInterval);
				this.mRefreshInterval = undefined;
			}
		}
	});
});
