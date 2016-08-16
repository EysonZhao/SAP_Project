sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.AnalysisTemplateDetail", {
		
		mTemplateModel: new sap.ui.model.json.JSONModel(),
		
		onInit: function () {
			this.getRouter().getRoute("analysistemplatedetail").attachMatched(this.onRouteMatched, this);
		},
		
		onRouteMatched: function(oEvent){
			this.getView().setModel(this.mTemplateModel, "TemplateModel");
			var oArgs = oEvent.getParameter("arguments");
			if(oArgs.id){
				//get the Analysis Template by id
				var that = this;
				$.ajax({
					url: that.getServiceUrl("/AnalysisTemplate/Get/") + oArgs.id,
					type: 'GET',
					contentType: 'application/json; charset=utf-8',
					success: function(result){
						that.mTemplateModel.setData(result);
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
				this.mTemplateModel.setData({});
			}
		},
		onSave: function(oEvent){
			var id = this.mTemplateModel.getData().id;
			if(id){	//update
				var that = this;
				$.ajax({
					url: that.getServiceUrl("/AnalysisTemplate/Update"),
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(that.mTemplateModel.getData()),
					success: function(result){
						that.mTemplateModel.setData(result);
						that.showMessage("Saved.");
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
				var that = this;
				$.ajax({
					url: that.getServiceUrl("/AnalysisTemplate/Add"),
					type: 'POST',
					contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(that.mTemplateModel.getData()),
					success: function(result){
						that.mTemplateModel.setData(result);
						that.showMessage("Saved.");
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
		},
		onDownload: function(oEvent){
			var id = this.mTemplateModel.getData().id;
			if(id){
				var downloadUrl = this.getServiceUrl("/AnalysisTemplate/Download/") + id;
				window.open(downloadUrl);
			}
		},
		onUploadFile: function(fileUploader){
			var id = this.mTemplateModel.getData().id;
			if(id){
				fileUploader.setUploadUrl(this.getServiceUrl("/AnalysisTemplate/Upload/") + id);// ;
				fileUploader.upload();
			}
		}
	});
});
