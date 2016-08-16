sap.ui.jsview("sme.perf.ui.view.TestIssue", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.TestIssue";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
 		return new sap.m.Page({
			title: "{i18n>/testIssueTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
		  		new sap.ui.layout.form.SimpleForm(this.createId('TestIssueForm'), {
		  			title:{
		  				text: '{testRequestModel>/name}'
		  			},
		            content: [
		  				new sap.m.Table(this.createId('TestIssueTable'), {
		  				    growing: true,
		  				    growingScrollToLoad: false,
		  				    growingThreshold: 8,
		  				    columns: [
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "5%",
		  				            header: new sap.m.Text({
		  				                text: "ID"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				        	hAlign: "Left",		
		  				        	width: "10%",
		  				            header: new sap.m.Text({
		  				                text: "Key"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "45%",
		  				            header: new sap.m.Text({
		  				                text: "Title"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "10%",
		  				            header: new sap.m.Text({
		  				                text: "Creator"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "10%",
		  				            header: new sap.m.Text({
		  				                text: "Processor"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "10%",
		  				            header: new sap.m.Text({
		  				                text: "Priority"
		  				            })
		  				        }),
		  				        new sap.m.Column({
		  				            hAlign: "Left",
		  				            width: "10%",
		  				            header: new sap.m.Text({
		  				                text: "Status"
		  				            })
		  				        })
		  				    ],
		  				    items: {
		  				        path: "testRequestModel>/issueList",
		  				        sorter: new sap.ui.model.Sorter("id", true),
		  				        template: new sap.m.ColumnListItem({
		  				            vAlign: sap.ui.core.VerticalAlign.Middle,
		  				            type: sap.m.ListType.Navigation,
		  				            cells: [
		  				                new sap.m.Text({
		  				                    text: "{testRequestModel>id}"
		  				                }),
		  				                new sap.m.Input({
		  				                    value: "{testRequestModel>jiraKey}"
		  				                }),
		  				                new sap.m.Text({
		  				                    text: "{testRequestModel>title}"
		  				                }),
		  				                new sap.m.Text({
		  				                	text: "{testRequestModel>creator}"
		  				        		}),
		  				                new sap.m.Text({
		  				                	text: "{testRequestModel>processor}"
		  				        		}),
		  				                new sap.m.Text({
		  				                	text: "{testRequestModel>priority}"
		  				        		}),
		  				                new sap.m.Text({
		  				                	text: "{testRequestModel>status}"
		  				        		})
		  				            ]
		  				        })
		  				    },
	  				        itemPress: function(oEvent){
	  				        	oController.onItemPress(oEvent);
	  				        }
		  				})
		              ]
		  		}) 
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button({
						text: "Refresh",
						press: function(oEvent){
							oController.onSave(oEvent);
						}
					}),
					new sap.m.Button({
						text: "New",
						press: function(oEvent){
							oController.onNew(oEvent);
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