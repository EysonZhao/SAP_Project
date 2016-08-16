sap.ui.define([
	"sme/perf/ui/controller/BaseController",
	"sap/m/MessageBox"
], function (BaseController, MessageBox) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.ExecutionGroupDetail", {
		
		mExecutionGroupModel: new sap.ui.model.json.JSONModel(),
		mParamsModel: new sap.ui.model.json.JSONModel(),
		paramChanged:false,
		
		onInit: function () {
			this.getRouter().getRoute("executiongroupdetail").attachMatched(this.onRouteMatched, this);
		},
		onRouteMatched: function(oEvent){
			var oArgs = oEvent.getParameter("arguments");
			var oView = this.getView();
			this.paramChanged=false;
			oView.setModel(this.mExecutionGroupModel, "executionGroupModel");
			oView.setModel(this.mParamsModel, "paramsModel");
			this.mParamsModel.setData({});
			this.mExecutionGroupModel.setData({});
			if(oArgs.executionGroupId){
				this.getExecutionGroup(oArgs.executionGroupId);
			}
		},
		getExecutionGroup: function(executionGroupId){
			var that = this;
			$.ajax({
				url: this.getServiceUrl("/ExecutionGroup/Get/") + executionGroupId,
				type: 'GET',
				contentType: 'application/json',
				success: function(result){
					that.mExecutionGroupModel.setData(result);
					that.paramChanged=true;
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
		onSave: function(oEvent){
			var reg = new RegExp("^([0-9]+;)*[0-9]+$");
			var ExeIdList = this.mExecutionGroupModel.getData().executionIdList;
			if(reg.test(ExeIdList)){
					var that = this;
					var oData = this.mExecutionGroupModel.getData();
					if(oData.id){	//have id, then update
						$.ajax({
							url: this.getServiceUrl("/ExecutionGroup/Update/"),
							type: 'POST',
							contentType: 'application/json',
							data: JSON.stringify(oData),
							success: function(result){
								that.mExecutionGroupModel.setData(result);
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
					else{	//id is null, then add a new one
						$.ajax({
							url: this.getServiceUrl("/ExecutionGroup/Add/"),
							type: 'POST',
							contentType: 'application/json',
							data: JSON.stringify(oData),
							success: function(result){
								that.mExecutionGroupModel.setData(result);
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
					
					if(that.paramChanged){//reset the parameters together.
	
						$.ajax({
							url: this.getServiceUrl("/ExecutionGroup/Modifyparams/")+oData.id,
							type: 'POST',
							contentType: 'application/json',
							data: JSON.stringify(that.mParamsModel.getData()),
							success: function(result){
								that.mParamsModel.setData(result);
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
			}//end test exelist if
			else{
				this.showMessage("The fommat of the executionIdList is not correct!");
			}
			
						
		},
		onShowParams:function(oEvent){
			var that = this;
			var oData = this.mExecutionGroupModel.getData();
			

			if(oData.id){	//have id, then update
				$.ajax({
					url: this.getServiceUrl("/ExecutionGroup/Getparam/")+oData.id,
					type: 'POST',
					contentType: 'application/json',
					data: JSON.stringify(oData),
					success: function(result){
						that.mParamsModel.setData(result);
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
			else{ // oData.id do not exist
				that.showMessage("The execution group has not build up yet!");
			}
		},
		onDuplicate: function(oEvent){
			var that = this;
			var oData = this.mExecutionGroupModel.getData();
			if(oData.id){	//have id, then update
				$.ajax({
					url: this.getServiceUrl("/ExecutionGroup/Duplicate/")+oData.id,
					type: 'POST',
					contentType: 'application/json',
					data: JSON.stringify(oData),
					success: function(result){
						that.mExecutionGroupModel.setData(result);
						that.showMessage("Duplicated");
					},
					error: function(msg){
						MessageBox.error("Error response from server!", {
							title: "Error",
							actions:[MessageBox.Action.OK],
							defaultAction: MessageBox.Action.OK,
							details: JSON.stringify(msg, null, 4)
						});
					}
				});
			}
			else{	//id is null, then add a new one
				this.showMessage("Save it before duplicate.");
			}
		}
	});
});
