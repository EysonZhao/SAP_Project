sap.ui.jsview("sme.perf.ui.view.TestRequestAttachment", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.TestRequestAttachment";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
	    var fileUploader = new sap.ui.commons.FileUploader({
			name: "upload",
			multiple: false,
			visible:"{newControlModel>/newEnable}",
			//Only pressed NEW button then the fileUploder part can be used 
			
			uploadOnChange: false,
			change:function(){
				oController.uploadContentChanged();
			},//The upload button can press only after the content is changed(it is not empty)
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
 		return new sap.m.Page(this.createId("Page"),{
			title: "{i18n>/testRequestAttachmentTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.m.Panel({
					headerToolbar: new sap.m.Toolbar({
						content: [
						    new sap.m.Title({
						    	text: "Test Request: {attachmentModel>/testRequestId} ",
						    	titleStyle: sap.ui.core.TitleLevel.H3
						    })
						]
					}),
					content:[
					    new sap.m.List({
			    	    	items:{
			    	    		path: "attachmentModel>/attachmentList",
			    	    		template: new sap.m.StandardListItem({
			    	    			type: sap.m.ListType.Navigation,
			    	    			tooltip: "click to download",
			    	    			title: "{attachmentModel>fileName}",
			    	    			description: {
			    	    				path: "attachmentModel>uploadDate",
			    	    				formatter: oController.formatter.formatDateTimeStringToDateString
			    	    			}
			    	    		})
			    	    	},
					    	itemPress: function(oEvent){
					    		oController.onItemPress(oEvent);
					    	}
			    	    }),
			    	    new sap.m.Panel({
			    	    	content:[
			    					    fileUploader,	
			    						new sap.m.Button({
			    						text: "Upload",
			    						width: "20%",
			    						enabled:"{newControlModel>/uploadEnable}",
			    						visible:"{newControlModel>/newEnable}",
			    						press: function(){
			    							oController.onUploadFile(fileUploader);
			    						}
			    						})
			    	    	         ]
			    	    })
			    	    		    	
					]
				})
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button({
						text: "Refresh",
						press: function(oEvent){
							oController.onRefresh(oEvent);
						}
					}),
					new sap.m.Button({
						text: "New",
						press: function(oEvent){
							oController.onNew(oEvent);
						}
					})
				]
			})
		});
	}

});