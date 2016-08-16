sap.ui.jsfragment("sme.perf.ui.fragment.TaskSelectDialog",{
	createContent: function(oController){
		var oDialog = new sap.m.TableSelectDialog({
			oDataText: '',
			title: '',
			items: {
				path:'',
				sorter: {
					path: '',
					descending: false
				},
				columns: [
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "45%",
	                    header: new sap.m.Label({
	                        text: "Name"
	                    })
	                }),
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "10%",
	                    header: new sap.m.Label({
	                        text: "Project"
	                    })
	                }),
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "10%",
	                    header: new sap.m.Label({
	                        text: "Category"
	                    })
	                }),
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "10%",
	                    header: new sap.m.Label({
	                        text: "Prority"
	                    })
	                }),
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "10%",
	                    header: new sap.m.Label({
	                        text: "Status"
	                    })
	                }),
	                new sap.m.Column({
	                    hAlign: "Left",
	                    width: "10%",
	                    header: new sap.m.Label({
	                        text: "Owner"
	                    })
	                })
			    ],
				template: new sap.m.ColumnListItem({
                    type: sap.m.ListType.Active,
                    vAlign: sap.ui.core.VerticalAlign.Middle,
                    cells: [
			            new sap.ui.commons.Link({
			                text: "{id}",
			                tooltip: "ID: {id}",
			                wrapping: false,
			                press: function(oEvent){
			                	oController.handleShowDetail(this.getText());
			                }
			            }),
			            new sap.m.Text({
			                text: "{name}",
			                tooltip: "Name: {name}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{projectName}",
			                tooltip: "Project: {projectName}",
			                wrapping: false
			            }),
                        new sap.m.Text({
                        	text: "{category}",
			                tooltip: "Category: {category}",
			                wrapping: false
                        }),
                        new sap.m.Text({
                        	text: "{priority}",
			                tooltip: "Priority: {priority}",
			                wrapping: false
                        }),
                        new sap.ui.commons.Link({
                        	text: { path: 'status', formatter: oController.formatter.testRequestStatusText },
			                tooltip: "Status: {status}",
			                wrapping: false,
			                press: function(oEvent){
			                	event.stopPropagation();	//prevent the event bubble up
			                	oController.handleStatusUpdate(oEvent);
			                }
                        }),
                        new sap.m.Text({
                        	text: "{testOwner}",
			                tooltip: "Owner: {testOwner}",
			                wrapping: false
                        })
		            ]
                })
			},
			
			search: function(oEvent){
				oController.onTaskSelectSearch(oEvent);
			},
			confirm: function(oEvent){
				oController.onTaskSelectConfirm(oEvent);
			}
		});
		
		return oDialog;
	}
});