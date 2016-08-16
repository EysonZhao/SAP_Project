sap.ui.jsview("sme.perf.ui.view.ExecutionDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 

	createContent : function(oController) {
		var typeSelect = new sap.m.ActionSelect(this.createId('TypeSelect'),{
			items: [
					oItem0 = new sap.ui.core.Item({
						key: true,
						text: "Time order"
					}),
					oItem1 = new sap.ui.core.Item({
						key: false,
						text: "Number of times"
					})
				],
			change: function(oEvent){
	    		oController.onChangeType(oEvent);
	    	}
		})
		
		var oSelectTestRequestPanel = new sap.m.Panel(this.createId("SelectTestRequestPanel"), {
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:"Step 1: Name & Select Test Request"
				    })
				]
			}),
			content:[
			    new sap.ui.layout.form.SimpleForm({
			    	editable: true,
			    	layout: "ResponsiveGridLayout",
			    	content: [
			    	    new sap.m.Label({text: "Test Request"}),
			    	    new sap.m.Input(this.createId("TestRequestInput"),{
			    	    	value: "{executionModel>/testRequestName}",
			    	    	showValueHelp: true,
			    	    	valueHelpOnly: true,
			    	    	valueHelpRequest: function(oEvent){
			    	    		oController.onSelectTestRequest(oEvent);
			    	    	}
			    	    }),
			    	    new sap.m.Label({text: "Execution Name"}),
			    	    new sap.m.Input({
			    	    	value: "{executionModel>/name}"
			    	    }),
			    	    new sap.m.Label({
			    	    	text: "Analyse time(if analyse by times imput 0)",
			    	    	width:"60%",
			    	    }),
			    	    new sap.m.Input({
			    	    	width:"40%",
			    	    	value: "{executionModel>/isTimeAnalyse}"})
			    	    
			    	]
			    })
			]
		});
		
		var oSelectTestScenarioPanel = new sap.m.Panel(this.createId("SelectTestScenarioPanel"), {
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:"Step 2: Select Test Scenario"
				    })
				]
			}),
			content:[
			    new sap.ui.layout.form.SimpleForm({
			    	editable: true,
			    	layout: "ResponsiveGridLayout",
			    	content: [
			    	    new sap.m.Label({text: "Test Scenario"}),
			    	    new sap.m.Input(this.createId("TestScenarioInput"),{
			    	    	value: "{executionModel>/scenarioName}",
			    	    	showValueHelp: true,
			    	    	valueHelpOnly: true,
			    	    	valueHelpRequest: function(oEvent){
			    	    		oController.onSelectTestScenario(oEvent);
			    	    	}
			    	    })
			    	]
			    })
			]
		});
		//----------------------------Task List View -----------------------------------

		var oTaskForm = new sap.ui.layout.form.Form(this.createId("TaskForm"),{
			editable: true,
			title: 'Task: {taskModel>/sn}',
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
            formContainers: [
                Utility.createFormContainerWithControl("Package",new sap.m.ComboBox(this.createId("PackageSelect"), {
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
        		}), true),
                Utility.createFormContainerWithControl("Task", new sap.m.ComboBox(this.createId("TaskSelect"), {
        			selectedKey: "{taskModel>/className}",
        			items:{
        				path:'taskListModel>/classes',
        				template: new sap.ui.core.ListItem({
        					key: '{taskListModel>name}',
        					text: '{taskListModel>name}'
        				})
        			}
        		}), true),
                Utility.createFormContainerInput("Description", "{taskModel>/description}"),
            ]
		});

		var oTaskListForm = new sap.ui.layout.form.SimpleForm(this.createId('TaskListForm'), {
            content: [
				new sap.m.Table(this.createId('taskListTable'), {
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				        path: "executionModel>/tasks",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>sn}",
									tip:"SN: {executionModel>sn}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>packageName}",
									tip:"Name: {executionModel>packageName}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>className}",
									tip:"Name: {executionModel>className}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Name: {executionModel>description}",
					                wrapping: false
								}),
				                new sap.ui.core.Icon({
				                	src:"sap-icon://arrow-top",
				                    tip: "Move the task down",
				                    press: function(oEvent){
				                    	oController.onTaskListMoveUp(oEvent);
				                    }
				                }),
				                new sap.ui.core.Icon({
				                	src:"sap-icon://arrow-bottom",
				                    tip: "Move the task down",
				                    press: function(oEvent){
				                    	oController.onTaskListMoveDown(oEvent);
				                    }
				                })
				            ]
				        })
				    },
				    delete: function(oEvent){
				    	oController.onTaskListDelete(oEvent);
				    },
				    itemPress: function(oEvent){
				    	oController.onTaskListItemPress(oEvent);
				    }
				})
            ]
		});
		
		var oTaskListPanel = new sap.m.Panel(this.createId("TaskListPanel"),{
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text: "Step 3: Edit Task List"
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
			}),
			content:[oTaskForm,
			         oTaskListForm]
		});
		
		//--------------------------Task List - Host -------------------------------
		var oTaskHostPanel = new sap.m.Panel(this.createId("TaskHostPanel"),{
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:"Step 4: Select Host"
				    })
				]
			}),
			content: [
			    new sap.m.Table(this.createId("TaskHostTable"), {
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				                text: "Host List"
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
				        path: "executionModel>/hostsParameter/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Navigation,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>name}",
									tip:"Name: {executionModel>name}",
					                wrapping: true
								}),
								new sap.m.Text({
								    text: {
								    	path: 'executionModel>value', 
								    	formatter: oController.formatter.hostListText
								    },
									tip:"Name: {executionModel>value}",
					                wrapping: true
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Description: {executionModel>description}",
					                wrapping: true
								})
				            ]
				        })
				    },
				    itemPress: function(oEvent){
				    	oController.onHostParameterItemPress(oEvent);
				    }
			    })
			]
		});
		
		//--------------------------Task List - Parameter -------------------------------
		var oTaskParameterPanel = new sap.m.Panel(this.createId("TaskParameterPanel"),{
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:'Step 5: Set Parameters'
				    })
				]
			}),
			content: [
			    new sap.m.Panel(this.createId("IntervalPanel"),{
			    	headerText:"Interval Time",
			    	content:[
			    	     new sap.m.Input({
			    	    	 value:"{executionModel>/interval}"
			    	     })    
			    	]
			    })  ,    
			    new sap.m.Panel(this.createId("TimeoutPanel"),{
			    	headerText:"Execution Timeout",
			    	content:[
			    	     new sap.m.Input({
			    	    	 value:"{executionModel>/timeOut}"
			    	     })    
			    	]
			    })  ,  
			    new sap.m.Table(this.createId("ProjectParameterTable"), {
			    	headerText: "Project Parameters",
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				        path: "executionModel>/projectParameters/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>name}",
									tip:"Name: {executionModel>name}",
					                wrapping: false
								}),
								new sap.m.Input({
								    value: "{executionModel>value}",
									tip:"Value: {executionModel>value}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Description: {executionModel>description}",
					                wrapping: false
								})
				            ]
				        })
				    }
			    }),	          
			    new sap.m.Table(this.createId("TaskParameterTable"), {
			    	headerText: "Tasks Parameters",
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				        path: "executionModel>/tasksParameter/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>name}",
									tip:"Name: {executionModel>name}",
					                wrapping: false
								}),
								new sap.m.Input({
								    value: "{executionModel>value}",
									tip:"Value: {executionModel>value}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Description: {executionModel>description}",
					                wrapping: false
								})
				            ]
				        })
				    }
			    })
			]
		});
		var oSummaryPanel = new sap.m.Panel(this.createId("SummaryPanel"),{
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text:"Step 6: Summary"
				    })
				]
			}),
			content: [
				new sap.ui.layout.form.SimpleForm({
				  	editable: true,
				  	layout: "ResponsiveGridLayout",
				  	content: [
				  	    new sap.m.Label({text: "Execution Name"}),
				  	    new sap.m.Text({
				  	    	text: "{executionModel>/name}"
				  	    }),
				  	    new sap.m.Label({text: "Test Request"}),
				  	    new sap.m.Text({text: "{executionModel>/testRequestName}"}),
				  	    new sap.m.Label({text: "Test Scenario"}),
				  	    new sap.m.Text({ text: "{executionModel>/scenarioName}" }),
				  	    new sap.m.Label({text: "Interval time"}),
				  	    new sap.m.Text({text: "{executionModel>/interval}"}),
				  	    new sap.m.Label({text: "Execution Timeout"}),
				  	    new sap.m.Text({text: "{executionModel>/timeOut}"}),
				  	    new sap.m.Label({text: "Is time order"}),
				  	    new sap.m.Text({text: "{executionModel>/isTimeAnalyse}"}),
				  	]
				}),
						new sap.m.Table(this.createId('SummaryTaskListTable'), {
							headerText: "Task List",
						    growing: true,
						    growingScrollToLoad: false,
						    //growingThreshold: 8,
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
						        })
						    ],
						    items: {
						        path: "executionModel>/tasks",
						        template: new sap.m.ColumnListItem({
						            vAlign: sap.ui.core.VerticalAlign.Middle,
						            type: sap.m.ListType.Active,
						            cells: [
										new sap.m.Text({
										    text: "{executionModel>sn}",
											tip:"SN: {executionModel>sn}",
							                wrapping: false
										}),
										new sap.m.Text({
										    text: "{executionModel>packageName}",
											tip:"Name: {executionModel>packageName}",
							                wrapping: false
										}),
										new sap.m.Text({
										    text: "{executionModel>className}",
											tip:"Name: {executionModel>className}",
							                wrapping: false
										}),
										new sap.m.Text({
										    text: "{executionModel>description}",
											tip:"Name: {executionModel>description}",
							                wrapping: false
										})
						            ]
						        })
						    }
						}),
					    new sap.m.Table(this.createId("SummaryTaskHostTable"), {
					    	headerText: "Host List",
						    growing: true,
						    growingScrollToLoad: false,
						    //growingThreshold: 8,
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
						                text: "Host List"
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
						        path: "executionModel>/hostsParameter/parameters/entries",
						        template: new sap.m.ColumnListItem({
						            vAlign: sap.ui.core.VerticalAlign.Middle,
						            cells: [
										new sap.m.Text({
										    text: "{executionModel>name}",
											tip:"Name: {executionModel>name}",
							                wrapping: true
										}),
										new sap.m.Text({
										    text: {
										    	path: 'executionModel>value', 
										    	formatter: oController.formatter.hostListText
										    },
											tip:"Name: {executionModel>value}",
							                wrapping: true
										}),
										new sap.m.Text({
										    text: "{executionModel>description}",
											tip:"Description: {executionModel>description}",
							                wrapping: true
										})
						            ]
						        })
						    }
					    }),
//					]
//				}),						
			    new sap.m.Table(this.createId("SummaryProjectParameterTable"), {
			    	headerText: "Project Parameters",
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				        path: "executionModel>/projectParameters/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>name}",
									tip:"Name: {executionModel>name}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>value}",
									tip:"Value: {executionModel>value}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Description: {executionModel>description}",
					                wrapping: false
								})
				            ]
				        })
				    }
			    }),	          
			    new sap.m.Table(this.createId("SummaryTaskParameterTable"), {
			    	headerText: "Tasks Parameters",
				    growing: true,
				    growingScrollToLoad: false,
				    //growingThreshold: 8,
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
				        path: "executionModel>/tasksParameter/parameters/entries",
				        template: new sap.m.ColumnListItem({
				            vAlign: sap.ui.core.VerticalAlign.Middle,
				            type: sap.m.ListType.Active,
				            cells: [
								new sap.m.Text({
								    text: "{executionModel>name}",
									tip:"Name: {executionModel>name}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>value}",
									tip:"Value: {executionModel>value}",
					                wrapping: false
								}),
								new sap.m.Text({
								    text: "{executionModel>description}",
									tip:"Description: {executionModel>description}",
					                wrapping: false
								})
				            ]
				        })
				    }
			    })
			]
		});
 		return new sap.m.Page({
			title: "{i18n>executionDetailTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
                oSelectTestRequestPanel,
                oSelectTestScenarioPanel,
                oTaskListPanel,
                oTaskHostPanel,
                oTaskParameterPanel,
                oSummaryPanel
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button(this.createId("BackBtn"), {
						text: "Back",
						press: function(oEvent){
							oController.onBack(oEvent);
						}
					}),
					new sap.m.Button(this.createId("NextBtn"), {
						text: "Next",
						press: function(oEvent){
							oController.onNext(oEvent);
						}
					}),
					new sap.m.Button(this.createId("SaveBtn"), {
						text: "Save",
						press: function(oEvent){
							oController.onSave(oEvent);
						}
					}),
					new sap.m.Button(this.createId("DuplicateBtn"), {
						text: "Duplicate",
						press: function(oEvent){
							oController.onDuplicate(oEvent);
						}
					})
				]
			})
		});
	}
});
