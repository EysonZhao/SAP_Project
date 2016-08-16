sap.ui.jsview("sme.perf.ui.view.ScenarioDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ScenarioDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		var oScenarioEditForm = new sap.ui.layout.form.Form(this.createId("ScenarioEditForm"),{
			editable: true,
			title: '{i18n>scenarioDetailTitle}: {scenarioModel>/id}',
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerInput("Name", "{scenarioModel>/name}", undefined, true),
                Utility.createFormContainerWithControl("Project", new sap.m.ComboBox({
                	selectedKey: "{scenarioModel>/projectId}",
                	items: {
                		path:'projectListModel>/projects',
                		template: new sap.ui.core.ListItem({
                			key:'{projectListModel>id}',
                			text: '{projectListModel>name}'
                		})
                	}
                }), true),
                Utility.createFormContainerInput("Description", "{scenarioModel>/description}" ,2)
            ]
        });
		
		var oPackageSelect = new sap.m.ComboBox(this.createId("PackageSelect"), {
			selectedKey: "{taskModel>/packageName}",
        	items: {
            	path: 'packageListModel>/packages',
            	template: new sap.ui.core.ListItem({
            		key: '{packageListModel>name}',
            		text: '{packageListModel>name}'
            	})
            },
            change: function(oEvent){
            	oController.onPackageSelectChange(oEvent);
            }
		});
		
		var oTaskSelect = new sap.m.ComboBox(this.createId("TaskSelect"), {
			selectedKey: "{taskModel>/className}",
			items:{
				path:'taskListModel>/classes',
				template: new sap.ui.core.ListItem({
					key: '{taskListModel>name}',
					text: '{taskListModel>name}'
				})
			}
		});
		
		var oTaskForm = new sap.ui.layout.form.Form(this.createId("TaskForm"),{
			editable: true,
			title: 'Task: {taskModel>/sn}',
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerWithControl("Package",oPackageSelect, true),
                Utility.createFormContainerWithControl("Task", oTaskSelect, true),
                Utility.createFormContainerInput("Description", "{taskModel>/description}"),
            ]
		});

		var oTaskListForm = new sap.ui.layout.form.SimpleForm(this.createId('TaskListForm'), {
//			title: "Task List",
            content: [
				new sap.m.Table(this.createId('table'), {
				    growing: true,
				    growingScrollToLoad: false,
				    growingThreshold: 8,
				    mode: sap.m.ListMode.Delete,
				    columns: [
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "5%",
				            header: new sap.m.Text({
				                text: "#"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "20%",
				            header: new sap.m.Text({
				                text: "Package"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "30%",
				            header: new sap.m.Text({
				                text: "Task"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "40%",
				            header: new sap.m.Text({
				                text: "Description"
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "5%",
				            header: new sap.m.Text({
				                text: ""
				            })
				        }),
				        new sap.m.Column({
				            hAlign: "Left",
				            width: "5%",
				            header: new sap.m.Text({
				                text: ""
				            })
				        })
				    ],
				    items: {
				        path: "scenarioTaskListModel>/tasks",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{scenarioTaskListModel>sn}",
									tip:"SN: {scenarioTaskListModel>sn}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{scenarioTaskListModel>packageName}",
									tip:"Package Name: {scenarioTaskListModel>packageName}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{scenarioTaskListModel>className}",
									tip:"Class Name: {scenarioTaskListModel>className}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{scenarioTaskListModel>description}",
									tip:"Description: {scenarioTaskListModel>description}",
					                wrapping: false
								}),
				                new sap.ui.core.Icon({
				                	src:"sap-icon://arrow-top",
				                    tip: "Move the task down",
				                    press: function(oEvent){
				                    	oController.onMoveUp(oEvent);
				                    }
				                }),
				                new sap.ui.core.Icon({
				                	src:"sap-icon://arrow-bottom",
				                    tip: "Move the task down",
				                    press: function(oEvent){
				                    	oController.onMoveDown(oEvent);
				                    }
				                })
				            ]
				        })
				    },
				    delete: function(oEvent){
				    	oController.onDelete(oEvent);
				    },
				    itemPress: function(oEvent){
				    	oController.onItemPress(oEvent);
				    }
				})
            ]
		});
		
		var oTaskListPanel = new sap.m.Panel({
			content:[oTaskListForm],
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:"Task List"
				    }),
				    new sap.m.ToolbarSpacer(),      
					new sap.m.Button({
						text: "Add",
						press: function(oEvent){
							oController.onTaskAdd(oEvent);
						}
					}),
					new sap.m.Button({
						text: "Update",
						press: function(oEvent){
							oController.onTaskUpdate(oEvent);
						}
					}),
				]
			})
		});
 		return new sap.m.Page({
			title: "{i18n>scenarioDetailTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.ui.layout.Grid({
			        defaultSpan: "L12 M12 S12",
			        position: "Center",
			        content: [
			            oScenarioEditForm,
			            oTaskForm,
			            oTaskListPanel,
//			            oTaskListForm
			        ]})
			],
			footer:new sap.m.Bar({
            	contentRight:[
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