sap.ui.jsview("sme.perf.ui.view.AnalysisTemplateList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.AnalysisTemplateList";
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

        var oTable = new sap.ui.table.Table(this.createId("Table"), {
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
		                	oController.handleShowDetail(oEvent);
		                }
		            }),
                	sortProperty: "id", 
                	filterProperty: "id",
                	width: "5%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Download"}),
                	template: new sap.ui.core.Icon({
                    	src: sap.ui.core.IconPool.getIconURI('download'),
                    	tooltip: 'Download',
                    	size: '1em',
                    	customData: [
							new sap.ui.core.CustomData({
								key: "Id",
								value: "{id}",
								writeToDom: false
							})
						],
                    	press: function(oEvent){
                    		oController.onDownload(oEvent);
                    	}
                    }),
                    width: "5%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Name"}),
                	template: new sap.m.Text({
		                text: "{name}",
		                tooltip: "{name}",
		                wrapping: false
		            }),
                	sortProperty: "name", 
                	filterProperty: "name",
                	width: "15%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Owner"}),
                	template: new sap.m.Text({
		                text: "{owner}",
		                tooltip: "{owner}",
		                wrapping: false
		            }),
                	sortProperty: "owner", 
                	filterProperty: "owner",
                	width: "10%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "FileName"}),
                	template: new sap.m.Text({
		                text: "{fileName}",
		                tooltip: "{fileName}",
		                wrapping: true
		            }),
                	sortProperty: "fileName", 
                	filterProperty: "fileName",
                	width: "25%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Description"}),
                	template: new sap.m.Text({
		                text: "{description}",
		                tooltip: "{description}",
		                wrapping: true
		            }),
                	sortProperty: "description", 
                	filterProperty: "description",
                	width: "40%"
                })
            ]
        });	
        
 		return new sap.m.Page({
			title: "Analysis Template List",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    oSearchField,
			    oTable
			],
			footer:new sap.m.Bar({
				contentLeft:[
				    new sap.m.Button({
				    	text:"Transaction",
				    	press: function(oEvent){
				    		oController.onTest(oEvent);
				    	}
				    })
				],
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