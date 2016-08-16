sap.ui.jsview("sme.perf.ui.view.StatusUpdate", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.StatusUpdate";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		
		var oStatusUpdateEditForm = new sap.ui.layout.form.Form(this.createId("StatusUpdateEditForm"),{
			editable: true,
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
				Utility.createFormContainerDatePicker("Date", "statusUpdateModel>/date", true),
				 Utility.createFormContainerWithControl("Status",new sap.m.ComboBox(this.createId("statusComboBox"), {
	        			selectedKey: "{statusUpdateModel>/status}",
	                	items: {
	                    	path: 'statusEnumModel>/status',
	                    	template: new sap.ui.core.ListItem({
	                    		key: '{statusEnumModel>name}',
	                    		text: '{statusEnumModel>name}'
	                    	})
	                    }
	        		}), true),
                Utility.createFormContainerInput("Description", "{statusUpdateModel>/description}" ,2)            
            ]
        });
		
		var oStatusUpdateListForm = new sap.ui.layout.form.SimpleForm(this.createId('StatusUpdateListForm'), {
            content: [
				new sap.m.Table(this.createId('StatusUpdateTable'), {
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
				            width: "15%",
				            header: new sap.m.Text({
				                text: "Date"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "10%",
				            header: new sap.m.Text({
				                text: "Status"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "70%",
				            header: new sap.m.Text({
				                text: "Description"
				            })
				        })
				    ],
				    items: {
				        path: "statusUpdateListModel>/statusUpdateList",
				        sorter: new sap.ui.model.Sorter("id", true),
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
				                new sap.m.Text({
				                    text: "{statusUpdateListModel>id}"
				                }),
				                new sap.m.Text({
//				                    text: "{statusUpdateListModel>date}"
				                	text: {
				                		path: "statusUpdateListModel>date",
				                		formatter: oController.formatter.formatDateTimeStringToDateString
				                	}
				                }),
				                new sap.m.Text({
				                    text: "{statusUpdateListModel>status}"
				                }),
				                new sap.m.Text({
				                    text: "{statusUpdateListModel>description}"
				                })
				            ]
				        })
				    },
				    itemPress: function(oEvent){
				    	oController.onItemPress(oEvent);
				    }
				})
            ]
		});
 		return new sap.m.Page({
			title: "{i18n>statusUpdateTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.ui.layout.Grid({
			        defaultSpan: "L12 M12 S12",
			        position: "Center",
			        content: [
					    oStatusUpdateEditForm,
			            oStatusUpdateListForm
			        ]})
			],
			footer:new sap.m.Bar({
            	contentRight:[
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