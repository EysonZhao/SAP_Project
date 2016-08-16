sap.ui.jsview("sme.perf.ui.view.ExecutionGroupDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionGroupDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {

				var paratable = new sap.m.Table(this.createId("ProjectParameterTable"), {
					    	headerText: "Project Parameters",
						    growing: true,
						    growingScrollToLoad: false,
						    //growingThreshold: 8,
						    columns: [
						        new sap.m.Column({
						            hAlign: "Center",
						            width: "35%",
						            header: new sap.m.Text({
						                text: "Name"
						            })
						        }),
						        new sap.m.Column({
						            hAlign: "Left",
						            width: "55%",
						            header: new sap.m.Text({
						                text: "Value"
						            })
						        })
						    ],
						    items: {
						        path: "paramsModel>/",
						        template: new sap.m.ColumnListItem({
						            vAlign: sap.ui.core.VerticalAlign.Middle,
						            type: sap.m.ListType.Active,
						            cells: [
										new sap.m.Text({
										    text: "{paramsModel>name}",
											tip:"Name: {paramsModel>name}",
							                wrapping: true
										}),
										new sap.m.Input({
										    value: "{paramsModel>value}",
											tip:"Value: {paramsModel>value}",
							                wrapping: true
										})
						            ]
						        })
						    }
					    });
				
 		return new sap.m.Page({
			title: "Execution Group Detail",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.ui.layout.form.SimpleForm({
			    	editable: true,
			    	layout: "ResponsiveGridLayout",
			    	content: [
			    	    new sap.m.Label({text: "Name"}),
			    	    new sap.m.Input({
			    	    	value: "{executionGroupModel>/name}"
			    	    }),
			    	    new sap.m.Label({text: "Execution ID List"}),
			    	    new sap.m.Input({
			    	    	value: "{executionGroupModel>/executionIdList}"
			    	    }),
			    	    new sap.m.Label({text: "Single execution timeout"}),
			    	    new sap.m.Input({
			    	    	value: "{executionGroupModel>/timeout}"
			    	    })
			    	]
			    }),
			    new sap.m.Button({
			    	text:"Show the task parameters of the ExecutionGroup",
			    	width:"80%",
			    	press:function(oEvent){
			    		oController.onShowParams(oEvent);
			    	}
			    }),
			    paratable
			],
			footer:new sap.m.Bar({
            	contentRight:[
  					new sap.m.Button({
						text: "Duplicate",
						press: function(oEvent){
							oController.onDuplicate(oEvent);
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