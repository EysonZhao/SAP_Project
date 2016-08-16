sap.ui.jsview("sme.perf.ui.view.ExecutionResultDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionResultDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		
		var oResultInfoPanel = new sap.m.Panel(this.createId("resultInfoPanel"), {
			content: [
				new sap.m.Table(this.createId('resultInfoTable'), {
				    growing: true,
				    growingScrollToLoad: false,
				    columns: [
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "ID"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "SessionID"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "SessionName"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "RecordCount"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "ImportDate"
				            })
				        })
				    ],
				    items: {
				        path: "executionResultModel>/results",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionResultModel>id}",
									tip:"ID: {executionResultModel>id}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionResultModel>resultSessionId}",
									tip:"SessionID: {executionResultModel>resultSessionId}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionResultModel>resultSessionName}",
									tip:"SessionName: {executionResultModel>resultSessionName}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionResultModel>resultCount}",
									tip:"RecourdCount: {executionResultModel>resultCount}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionResultModel>importDate}",
									tip:"ImportDate: {executionResultModel>importDate}",
					                wrapping: false
								})
				            ]
				        })
				    }
				})
			]
		});
		
		var oImportForm = new sap.ui.layout.form.Form({
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerInput("Path", "{importSummaryModel>/path}"),
            ]
		});
		
		var oImportSummaryForm = new sap.ui.layout.form.SimpleForm({
			content:[
				new sap.m.Table(this.createId('ImportSummaryTable'), {
				    growing: true,
				    growingScrollToLoad: false,
				    columns: [
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "isSuccess"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "SessionID"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "SessionName"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "auto",
				            header: new sap.m.Text({
				                text: "RecordCount"
				            })
				        })
				    ],
				    items: {
				        path: "importSummaryModel>/results",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{importSummaryModel>success}",
									tip:"isSuccess: {importSummaryModel>success}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{importSummaryModel>resultSessionId}",
									tip:"SessionID: {importSummaryModel>resultSessionId}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{importSummaryModel>resultSessionName}",
									tip:"SessionName: {importSummaryModel>resultSessionName}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{importSummaryModel>recordCount}",
									tip:"RecourdCount: {importSummaryModel>recordCount}",
					                wrapping: false
								})
				            ]
				        })
				    }
				})
			]
		});
		
		var oImportPanel = new sap.m.Panel(this.createId("importPanel"),{
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text: "ResultImport"
				    }),
				    new sap.m.ToolbarSpacer(),      
					new sap.m.Button({
						text: "Import",
						press: function(oEvent){
							oController.onImport(oEvent);
						}
					}),
//					new sap.m.Button({
//						text: "Refresh",
//						press: function(oEvent){
//							oController.onRefresh(oEvent);
//						}
//					}),
				]
			}),
			content:[oImportForm,
			         oImportSummaryForm]
		});
 		return new sap.m.Page({
			title: "Result Info",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    oResultInfoPanel,
			    oImportPanel
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
						text: "Auto Refresh On",
						press: function(oEvent){
							oController.onAutoRefreshOn(oEvent);
						}
					}),
					new sap.m.Button({
						text: "Auto Refresh Off",
						press: function(oEvent){
							oController.onAutoRefreshOff(oEvent);
						}
					})
				]
			})
		});
	}

});