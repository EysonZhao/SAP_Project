sap.ui.jsview("sme.perf.ui.view.HostList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.HostList";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		var oSearchField = Utility.createSearchField(oController.refresh.bind(oController), oController.search.bind(oController));
        
        var oScenarioTable = new sap.m.Table(this.createId('table'), {
            growing: true,
            growingScrollToLoad: false,
            growingThreshold: 50,
            columns: [
                new sap.m.Column({
                    hAlign: "Left",
                    width: "5%",
                    header: new sap.m.Label({
                        text: "ID"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "HostName"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "IP"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "Mode"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "Memory(GB)"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "CPU Core"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "15%",
                    header: new sap.m.Label({
                        text: "OS"
                    })
                })
            ],
            items: {
                path: "hostListModel>/hostList",
                sorter: new sap.ui.model.Sorter("id", true),
                template: new sap.m.ColumnListItem({
                	type: sap.m.ListType.Navigation,
                    vAlign: sap.ui.core.VerticalAlign.Middle,
                    cells: [
			            new sap.m.Text({
			                text: "{hostListModel>id}",
			                tooltip: "ID: {hostListModel>id}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>hostName}",
			                tooltip: "HostName: {hostListModel>name}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>ip}",
			                tooltip: "IP: {hostListModel>ip}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>mode}",
			                tooltip: "Mode: {hostListModel>mode}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>memoryGB}",
			                tooltip: "Memory(GB): {hostListModel>memoryGB}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>cpuCore}",
			                tooltip: "CPU Core: {hostListModel>cpuCore}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{hostListModel>operationSystem}",
			                tooltip: "OS: {hostListModel>operationSystem}",
			                wrapping: false
			            })
		            ]
                })
            },
            itemPress: function (oEvent) {
                oController.onItemPress(oEvent);
            }
        });
        
        return new sap.m.Page(this.createId("page"), {
            title: "{i18n>hostListTitle}",
            subHeader: new sap.m.Toolbar({
                content: oSearchField
            }),
            content: [
                oScenarioTable
            ],
            footer: new sap.m.Bar({
            	contentRight:[
      	            	    new sap.m.Button({
      	            	    	text: "New",
      	            	    	press: function(){
      	            	    		oController.onNew();
      	            	    	}
      	            	    })
      	            	]
            })
        });
	}

});
