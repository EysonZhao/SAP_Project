sap.ui.jsview("sme.perf.ui.view.Home", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.Home";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
        var oList = new sap.m.List(this.createId("menu_list"), {
            mode: "None",
            items: [
                new sap.m.GroupHeaderListItem({
                    title: 'Modules'
                }),
                new sap.m.StandardListItem({
                    title: "Test Request",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "testrequestlist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "Execution",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "executionlist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "ExecutionGroup",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "executiongrouplist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "Result",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "executionresultlist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "Analysis Templates",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "analysistemplatelist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
	            new sap.m.GroupHeaderListItem({
		            title: 'Report'
		        }),
		        new sap.m.StandardListItem({
		        	title: "Status Report",
		        	type: sap.m.ListType.Active,
		        	customData: [
		        	    new sap.ui.core.CustomData({
		        	    	key: "route",
		        	    	value: "statusreport",
		        	    	writeToDom: false
		        	    })
		        	],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
		        }),
		        new sap.m.StandardListItem({
		        	title: "Test Issue Report",
		        	type: sap.m.ListType.Active,
		        	customData: [
		        	    new sap.ui.core.CustomData({
		        	    	key: "route",
		        	    	value: "testissuereport",
		        	    	writeToDom: false
		        	    })
		        	],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
		        }),
		        new sap.m.StandardListItem({
		        	title: "Final Issue Report",
		        	type: sap.m.ListType.Active,
		        	customData: [
		        	    new sap.ui.core.CustomData({
		        	    	key: "route",
		        	    	value: "zissuereport",
		        	    	writeToDom: false
		        	    })
		        	],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
		        }),
                new sap.m.GroupHeaderListItem({
                    title: 'Administration'
                }),
                new sap.m.StandardListItem({
                    title: "Project",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "projectlist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "Scenario",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "scenariolist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.StandardListItem({
                    title: "Host",
                    type: sap.m.ListType.Active,
                    customData:[
                        new sap.ui.core.CustomData({
                        	key: "route",
                        	value: "hostlist",
                        	writeToDom: false
                        })
                    ],
                    press: function (oEvent) {
                        oController.handleHomeListItemPress(oEvent)
                    }
                }),
                new sap.m.GroupHeaderListItem({
                    title: 'Miscellaneous'
                }),
                new sap.m.StandardListItem({
                    title: "Perf Test Center V1.0",
                    type: sap.m.ListType.Active,
                    press: function (oEvent) {
                        window.open('http://10.58.120.189/PerformanceTestingCenter/')
                    }
                })
            ]
        });

        //Page
        var page = new sap.m.Page(this.createId("page"), {
            title: "{i18n>appTitle}",
            customHeader: new sap.m.Bar({
                contentLeft: [
                    new sap.m.Button('button_pin', {
                        icon: sap.ui.core.IconPool.getIconURI("pushpin-off"),
                        tooltip: 'Hide Master',
                        press: function () {
                        	oController.pinOrUnpin();
                        }
                    })
                ],
                contentMiddle: [
                    new sap.m.Label({
                        text: "{i18n>appTitle}"
                    })
                ]
            }),
            content: [
                oList
            ],
            footer: new sap.m.Bar()
        });

        return page;
	}

});
