sap.ui.define([
	"sme/perf/ui/controller/BaseController",
], function (BaseController) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ZIssueReport", {

		mQueryModel: new sap.ui.model.json.JSONModel(),
		mDefaultOptionModel: new sap.ui.model.json.JSONModel(),
		mTimeModel: new sap.ui.model.json.JSONModel(),

		
		onInit: function () {
			this.getRouter().getRoute("zissuereport").attachMatched(this.onRouteMatched, this);
		},

		onRouteMatched: function(oEvent){		
			var oView = this.getView();
			this.mQueryModel.setData({});
			this.mDefaultOptionModel.setData({});
			oView.setModel(this.mQueryModel, "queryModel");
			oView.setModel(this.mDefaultOptionModel,"DefaultOptionModel");
			oView.setModel(this.mTimeModel,"TimeModel");
			this.refresh();
			
		},
		//refresh the page and set the settings to the default value
		refresh: function(){       	
        	this.getView().byId("PeriodSelect").setSelectedItemId("default_select");

			this.onChangePeriod();
			var endDate = new Date(); 	//today
			var startDate = new Date();
			startDate.setDate(endDate.getDate() - 15); //15 days ago
			this.mQueryModel.setData({
				startDate: this.formatter.formatDateObjectToDateTimeString(startDate),
				endDate:this.formatter.formatDateObjectToDateTimeString(endDate),
				period:30
			});
			this.mDefaultOptionModel.setData({
				period:30,
				checked:true
			});
			this.mTimeModel.setData({
				time:"One Month"
			});
			var that=this;
			$.ajax({
			url: this.getConfig("serviceBaseUrl") + '/Report/IssueReport',
			type: 'POST',
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(that.mQueryModel.getData()),
			success: function(result){
				var len=result.length;
				
				var oView = that.getView();
				var oPanels = oView.getPanels();
				for(var i=0;i<len;i++){
					var model = new sap.ui.model.json.JSONModel();
					result[i].nearlyvisible=(result[i].nearlySubmittedList.length>0?true:false);
					result[i].oldervisible=(result[i].olderSubmittedList.length>0?true:false);
					
					oPanels[i].setVisible(result[i].olderSubmittedList.length+result[i].nearlySubmittedList.length>0?true:false);
					model.setData(result[i]);
					oView.setModel(model,"issueReportModel_"+i);
				}
			},
			error: function(msg){
				that.showMessage("Error resposne from server!");
			},
			beforeSend: function(){
				that.getView().setBusy(true);
			},
			complete: function(){
				that.getView().setBusy(false);
			}
			})
		},
	        onGenerateReport: function(oEvent){
	        	var that=this;
				$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/Report/IssueReport',
				type: 'POST',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(that.mQueryModel.getData()),
				success: function(result){
					var len=result.length;
					
					var oView = that.getView();
					var oPanels = oView.getPanels();
					for(var i=0;i<len;i++){
						var model = new sap.ui.model.json.JSONModel();
						result[i].nearlyvisible=(result[i].nearlySubmittedList.length>0?true:false);
						result[i].oldervisible=(result[i].olderSubmittedList.length>0?true:false);
						
						oPanels[i].setVisible(result[i].olderSubmittedList.length+result[i].nearlySubmittedList.length>0?true:false);
						model.setData(result[i]);
						oView.setModel(model,"issueReportModel_"+i);
					}
		        	var select=that.getView().byId("PeriodSelect");
		        	that.mTimeModel.setData({time:select.getSelectedItem().getText()});
				},
				error: function(msg){
					that.showMessage("Error resposne from server!");
				},
				beforeSend: function(){
					that.getView().setBusy(true);
				},
				complete: function(){
					that.getView().setBusy(false);
				}
			});
	        },
	        
	        toReport: function(){
	        	var that=this;
				$.ajax({
				url: this.getConfig("serviceBaseUrl") + '/Report/GenerateReport',
				type: 'POST',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(that.mQueryModel.getData()),
				success: function(result){
					that.showMessage("Generate successfully!");
				},
				error: function(msg){
					that.showMessage("Error resposne from server!");
				},
				beforeSend: function(){
					that.getView().setBusy(true);
				},
				complete: function(){
					that.getView().setBusy(false);
					var oLogUrl = that.getServiceUrl('/Download/Report');
					window.open(oLogUrl);
				}
				});
	        },
	        
	        onDownload: function(){
	        	var oLogUrl = this.getServiceUrl('/Download/GetReport');
				window.open(oLogUrl);
	        },
	        onChangePeriod: function(oEvent){
	        	var select=this.getView().byId("PeriodSelect");
	        	var s=select.getSelectedKey();
	        	this.mQueryModel.getData().period=select.getSelectedKey();

	        }


	});
});
