sap.ui.jsview("sme.perf.ui.view.TestIssueReport", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.TestIssueReport";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		
        var oSearchField = new sap.m.MultiInput(this.createId("search"), {
            placeholder: "Search...",
            showValueHelp: true,
            valueHelpRequest: function () {
            	oController.operateSearch();
            },
            tokenChange: function (oEvent) {
                if (oEvent.getParameter("type") == "tokensChanged") {
                	oController.operateSearch();
                }
            }
        });

        oSearchField._getValueHelpIcon = function () {
            var c = sap.ui.core.IconPool;
            var t = this;
            if (!this._oValueHelpIcon) {
                var u = c.getIconURI("search");
                this._oValueHelpIcon = c.createControlByURI({
                    id: this.getId() + "__vhi",
                    src: u
                });
                this._oValueHelpIcon.addStyleClass("sapMInputValHelpInner");
                this._oValueHelpIcon.attachPress(function (e) {
                    if (!t.getValueHelpOnly()) {
                        t.fireValueHelpRequest({
                            fromSuggestions: false
                        })
                    }
                })
            }
            return this._oValueHelpIcon
        };

        oSearchField.addValidator(function (args) {
            return args.asyncCallback(new sap.m.Token({ key: args.text, text: args.text }));
        });

        var oTestIssueTable = new sap.ui.table.Table(this.createId("testIssueTable"), {
        	selectionMode: sap.ui.table.SelectionMode.Single,
   			visibleRowCount:  parseInt(($(window).height() - 144) / 48 -2), //10,
            selectionBehavior: sap.ui.table.SelectionBehavior.RowOnly,
            navigationMode: sap.ui.table.NavigationMode.Paginator,
//            rowSelectionChange: function(oEvent){
//            	oController.handleItemPressed(oEvent);
//            },
            columns:[
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "ID"}),
                	template: new sap.ui.commons.Link({
		                text: "{id}",
		                tooltip: "ID: {id}",
		                wrapping: false,
		                press: function(oEvent){
		                	event.stopPropagation();	//prevent the event bubble up
		                	oController.handleShowDetail(oEvent);
		                }
		            }),
//                	template: new sap.m.Text({
//		                text: "{id}",
//		                tooltip: "Project: {id}",
//		                wrapping: false
//		            }),
                	sortProperty: "id", 
                	filterProperty: "id",
                	width: "2rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "JiraKey"}),
                	template: new sap.m.Text({
		                text: "{jiraKey}",
		                tooltip: "JiraKey: {jiraKey}",
		                wrapping: false
		            }),
                	sortProperty: "jiraKey", 
                	filterProperty: "jiraKey",
                	width: "5rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Title"}),
                	template: new sap.m.Text({
		                text: "{title}",
		                tooltip: "Title: {title}",
		                wrapping: true
		            }),
                	sortProperty: "title", 
                	filterProperty: "title",
                	width: "18rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Project"}),
                	template: new sap.m.Text({
		                text: "{projectName}",
		                tooltip: "Project: {projectName}",
		                wrapping: false
		            }),
                	sortProperty: "projectName", 
                	filterProperty: "projectName",
                	width: "6rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "TestRequest"}),
                	template: new sap.m.Text({
		                text: "{requestName}",
		                tooltip: "TestRequest: {requestName}",
		                wrapping: true
		            }),
                	sortProperty: "requestName", 
                	filterProperty: "requestName",
                	width: "18rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Creator"}),
                	template: new sap.m.Text({
                    	text: "{creator}",
		                tooltip: "Creator: {creator}",
		                wrapping: false
                    }),
                	sortProperty: "creator", 
                	filterProperty: "creator",
                	width: "5rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Processor"}),
                	template: new sap.m.Text({
                    	text: "{processor}",
		                tooltip: "Processor: {processor}",
		                wrapping: false
                    }),
                	sortProperty: "processor", 
                	filterProperty: "processor",
                	width: "5rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Priority"}),
                	template: new sap.m.Text({
                    	text: "{priority}",
		                tooltip: "Priority: {priority}",
		                wrapping: false
                    }),
                	sortProperty: "priority", 
                	filterProperty: "priority",
                	width: "4rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Status"}),
                	template: new sap.m.Text({
                		text: "{status}",
		                tooltip: "Status: {status}",
		                wrapping: false
                    }),
                	sortProperty: "status", 
                	filterProperty: "status",
                	width: "4rem"
                })
            ]
        });	

		
 		return new sap.m.Page({
			title: "Test Issue Report",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
            subHeader: new sap.m.Toolbar({
                content: oSearchField
            }),
			content: [
			    oTestIssueTable
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button({
						text: "Sync",
						press: function(oEvent){
							oController.onSync(oEvent);
						}
					})
				]
			})
		}).addStyleClass("sapUiSizeCozy");
	}

});