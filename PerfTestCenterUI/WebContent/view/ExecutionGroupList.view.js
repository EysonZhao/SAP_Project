sap.ui.jsview("sme.perf.ui.view.ExecutionGroupList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionGroupList";
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

        var oExecutionGroupTable = new sap.ui.table.Table(this.createId("executionGroupTable"), {
        	selectionMode: sap.ui.table.SelectionMode.Single,
   			visibleRowCount:  parseInt(($(window).height() - 144) / 48 -2), //10,
            selectionBehavior: sap.ui.table.SelectionBehavior.RowOnly,
            navigationMode: sap.ui.table.NavigationMode.Paginator,
            columns:[
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "ID"}),
                	template: new sap.ui.commons.Link({
		                text: "{id}",
		                tooltip: "ID: {id}",
		                wrapping: false,
		                press: function(oEvent){
		                	event.stopPropagation();	//prevent the event bubble up
		                	oController.handleShowDetail(this.getText());
		                }
		            }),
                	sortProperty: "id", 
                	filterProperty: "id",
                	width: "2rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Name"}),
                	template: new sap.m.Text({
		                text: "{name}",
		                tooltip: "{name}",
		                wrapping: true
		            }),
                	sortProperty: "name", 
                	filterProperty: "name",
                	width: "20rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "State"}),
                	template: new sap.m.Text({
		                text: "{state}",
		                tooltip: "{state}",
		                wrapping: false
		            }),
                	sortProperty: "state", 
                	filterProperty: "state",
                	width: "4rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "ExecutionIdList"}),
                	template: new sap.m.Text({
		                text: "{executionIdList}",
		                tooltip: "{executionIdList}",
		                wrapping: true
		            }),
                	sortProperty: "executionIdList", 
                	filterProperty: "executionIdList",
                	width: "18rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "StartDate"}),
                	template: new sap.m.Text({
		                text: "{startDate}",
		                tooltip: "{startDate}",
		                wrapping: false
		            }),
                	sortProperty: "startDate", 
                	filterProperty: "startDate",
                	width: "10rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "EndDate"}),
                	template: new sap.m.Text({
		                text: "{endDate}",
		                tooltip: "{endDate}",
		                wrapping: false
		            }),
                	sortProperty: "endDate", 
                	filterProperty: "endDate",
                	width: "10rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Run"}),
                	template: new sap.ui.core.Icon({
                    	src: sap.ui.core.IconPool.getIconURI('add-process'),
                    	tooltip: 'Run',
                    	size: '1.5em',
                    	visible: "{= ${state} === 'New' ? true : false }",                    	
                    	customData: [
							new sap.ui.core.CustomData({
								key: "executionGroupId",
								value: "{id}",
								writeToDom: false
							})
						],
                    	press: function(oEvent){
                    		oController.onRunBtnPress(oEvent);
                    	}
                	}),
                	width: "3rem"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Stop"}),
                	template: new sap.ui.core.Icon({
                    	src: sap.ui.core.IconPool.getIconURI('stop'),
                    	tooltip: 'Stop',
                    	size: '1.5em',
                    	visible: "{= ${state} === 'Running' ? true : false }",
                    	customData: [
							new sap.ui.core.CustomData({
								key: "executionGroupId",
								value: "{id}",
								writeToDom: false
							})
						],
                    	press: function(oEvent){
                    		oController.onStopBtnPress(oEvent);
                    	}
                	}),
                	width: "3rem"
                })
            ]
        });	

 		return new sap.m.Page({
			title: "Execution Group List",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
            subHeader: new sap.m.Toolbar({
                content: oSearchField
            }),
			content: [
			    oExecutionGroupTable
			],
			footer:new sap.m.Bar({
            	contentRight:[
					new sap.m.Button({
						text: "New",
						press: function(oEvent){
							oController.onNew(oEvent);
						}
					})
				]
			})
		}).addStyleClass("sapUiSizeCozy");
	}

});