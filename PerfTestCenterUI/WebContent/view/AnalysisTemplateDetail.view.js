sap.ui.jsview("sme.perf.ui.view.AnalysisTemplateDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.AnalysisTemplateDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		
	    var fileUploader = new sap.ui.commons.FileUploader({
	    	uploadUrl: "http://10.58.76.242:9090/UI5Upload/upload",
			name: "upload",
			multiple: false,
			uploadOnChange: false,
			fileSizeExceed: function (oEvent) {
				var sName = oEvent.getParameter("fileName");
				var fSize = oEvent.getParameter("fileSize");
				var fLimit = oFileUploader2.getMaximumFileSize();
				sap.ui.commons.MessageBox.show("File: " + sName + " is of size " + fSize + " MB which exceeds the file size limit of " + fLimit + " MB.", "ERROR", "File size exceeded");
			},
			uploadComplete: function (oEvent) {
				sap.m.MessageToast.show("Upload Completed.");
			}
	    });
	    var uploadButton = new sap.m.Button({
	    	text: "Upload",
	    	width: "4rem",
	    	press: function(){
	    		oController.onUploadFile(fileUploader);
	    	}
	    });
		
		var oForm = new sap.ui.layout.form.Form(this.createId("Form"),{
			title: "Analysis Template: {TemplateModel>/id}",
			editable: true,
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
			formContainers: [
				Utility.createFormContainerInput("Name", "{TemplateModel>/name}", undefined, true),
				Utility.createFormContainerInput("Owner", "{TemplateModel>/owner}", undefined, true),
				Utility.createFormContainerInput("Description", "{TemplateModel>/description}", 4),
				new sap.ui.layout.form.FormContainer({
					visible: "{= ${TemplateModel>/id} > 0 ? true : false }",
					formElements: [
						new sap.ui.layout.form.FormElement({
							label: new sap.m.Label({text: ""}),
							fields: [
							    fileUploader
							]
						})],
						layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: false})
					}),
				new sap.ui.layout.form.FormContainer({
					visible: "{= ${TemplateModel>/id} > 0 ? true : false }",
					formElements: [
						new sap.ui.layout.form.FormElement({
							label: new sap.m.Label({text: ""}),
							fields: [
							    uploadButton
							]
						})],
//						layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: false})
					})
		    ]
		});
 		return new sap.m.Page({
			title: "Analysis Template Detail",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    oForm 
			],
			footer:new sap.m.Bar({
            	contentRight:[
  					new sap.m.Button({
						visible: "{= ${TemplateModel>/fileName} ? true: false}",
						text: "Download",
						press: function(oEvent){
							oController.onDownload(oEvent);
						}
					}),
					new sap.m.Button({
						text: "Save",
						press: function(oEvent){
							oController.onSave(oEvent);
						}
					})
				]
			})
		});
	}

});