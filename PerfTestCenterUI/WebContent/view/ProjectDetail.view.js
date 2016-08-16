sap.ui.jsview("sme.perf.ui.view.ProjectDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ProjectDetail";
	},
	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		var oProjectEditForm = new sap.ui.layout.form.Form(this.createId("ProjectEditForm"),{
			editable: true,
			title: 'Project: {projectModel>/id}',
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerInput("Name", "{projectModel>/name}", undefined, true),
                Utility.createFormContainerInput("Description", "{projectModel>/description}" ,2)
            ]
        });
		
		var oParameterEditForm = new sap.ui.layout.form.Form(this.createId("ParameterEditForm"),{
			editable: true,
			title: "Parameter Detail",
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerInput("Name", "{parameterModel>/name}", undefined, true),
                Utility.createFormContainerInput("Value", "{parameterModel>/value}", undefined, true),
                Utility.createFormContainerInput("Description", "{parameterModel>/description}" ,2)
            ]
        });
				
		var oParameterListForm = new sap.ui.layout.form.SimpleForm(this.createId('ParameterListForm'), {
			title: "Parameter List",
            content: [
				new sap.m.Table(this.createId('table'), {
				    growing: true,
				    growingScrollToLoad: false,
				    growingThreshold: 8,
				    mode: sap.m.ListMode.Delete,
				    columns: [
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "20%",
				            header: new sap.m.Text({
				                text: "Name"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "40%",
				            header: new sap.m.Text({
				                text: "Value"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "40%",
				            header: new sap.m.Text({
				                text: "Description"
				            })
				        })
				    ],
				    items: {
				        path: "parameterListModel>/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
				                new sap.m.Input({
				                    value: "{parameterListModel>name}",
				                	tip:"Name: {parameterListModel>name}"
				                }),
				                new sap.m.Input({
				                    value: "{parameterListModel>value}",
				                    tip: "Value: {parameterListModel>value}"
				                }),
				                new sap.m.Input({
				                    value: "{parameterListModel>description}",
				                    tip: "Description: {parameterListModel>description}"
				                })
				            ]
				        })
				    },
				    delete: function(oEvent){
				    	oController.onDelete(oEvent);
				    }
				})
            ]
		});
 		return new sap.m.Page({
			title: "{i18n>projectDetailTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.ui.layout.Grid({
			        defaultSpan: "L12 M12 S12",
			        position: "Center",
			        content: [
					    oProjectEditForm,
//					    oParameterEditForm,
			            oParameterListForm,
			        ]})
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button({
						text: "New Parameter",
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