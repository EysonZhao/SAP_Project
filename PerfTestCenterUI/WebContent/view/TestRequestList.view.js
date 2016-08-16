sap.ui.jsview("sme.perf.ui.view.TestRequestList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.TestRequestList";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {

        var oSearchField = new sap.m.MultiInput(this.createId("sf"), {
            placeholder: "Search...",
            showValueHelp: true,
            valueHelpRequest: function () {
                oController.refresh();
            },
            tokenChange: function (oEvent) {
                if (oEvent.getParameter("type") == "tokensChanged") {
                    var values = [];
                    var tokens = this.getTokens();
                    for (var i = 0; i < tokens.length; i++) {
                        values.push(tokens[i].getKey());
                    }
                    if (values.length == 0) {
                        oController.refresh(true);
                    }
                    else {
                        oController.operateSearch(values);
                    }
                }
            }
        });

        oSearchField._getValueHelpIcon = function () {
            var c = sap.ui.core.IconPool;
            var t = this;
            if (!this._oValueHelpIcon) {
                var u = c.getIconURI("synchronize");
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

//        var oTestRequestTable = new sap.m.Table(this.createId('table'), {
//            growing: true,
//            growingScrollToLoad: false,
//            growingThreshold: 50,
//            selectionMode: sap.ui.table.SelectionMode.Single,
//            columns: [
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "5%",
//                    header: new sap.m.Label({
//                        text: "ID"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "40%",
//                    header: new sap.m.Label({
//                        text: "Name"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "10%",
//                    header: new sap.m.Label({
//                        text: "Project"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "10%",
//                    header: new sap.m.Label({
//                        text: "Category"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "10%",
//                    header: new sap.m.Label({
//                        text: "Prority"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "10%",
//                    header: new sap.m.Label({
//                        text: "Owner"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "5%",
//                    header: new sap.m.Label({
//                        text: "Status"
//                    })
//                }),
//                new sap.m.Column({
//                    hAlign: "Left",
//                    width: "5%",
//                    header: new sap.m.Label({
//                        text: "Issue"
//                    })
//                }),
//                new sap.m.Column({
//                	hAlign: "Left",
//                    width: "5%",
//                    header: new sap.m.Label({
//                        text: "Attachment"
//                    })
//                })
//            ],
//            items: {
//                path: "/TestRequestList",
//                sorter: new sap.ui.model.Sorter("id", true),
//                template: new sap.m.ColumnListItem({
//                	type: sap.m.ListType.Navigation,
//                    vAlign: sap.ui.core.VerticalAlign.Middle,
//                    cells: [
//			            new sap.ui.commons.Link({
//			                text: "{id}",
//			                tooltip: "ID: {id}",
//			                wrapping: false,
//			                press: function(oEvent){
//        						event.stopPropagation();	//prevent the event bubble up
//			                	oController.handleShowDetail(this.getText());
//			                }
//			            }),
//			            new sap.m.Text({
//			                text: "{name}",
//			                tooltip: "Name: {name}",
//			                wrapping: false
//			            }),
//			            new sap.m.Text({
//			                text: "{projectName}",
//			                tooltip: "Project: {projectName}",
//			                wrapping: false
//			            }),
//                        new sap.m.Text({
//                        	text: "{category}",
//			                tooltip: "Category: {category}",
//			                wrapping: false
//                        }),
//                        new sap.m.Text({
//                        	text: "{priority}",
//			                tooltip: "Priority: {priority}",
//			                wrapping: false
//                        }),
//                        new sap.m.Text({
//                        	text: "{testOwner}",
//			                tooltip: "Owner: {testOwner}",
//			                wrapping: false
//                        }),
//                        new sap.ui.commons.Link({
//                        	text: { path: 'latestStatus', formatter: oController.formatter.testRequestStatusText },
//			                tooltip: "Status: {latestStatus}",
//			                wrapping: false,
//			                press: function(oEvent){
//			                	event.stopPropagation();	//prevent the event bubble up
//			                	oController.handleStatusUpdate(oEvent);
//			                }
//                        }),
//                        new sap.ui.commons.Link({
//                        	text:  "( {issueCount} )",
//			                tooltip: "Goto test issue edit",
//			                wrapping: false,
//			                press: function(oEvent){
//			                	event.stopPropagation();	//prevent the event bubble up
//			                	oController.onIssueLinkClick(oEvent);
//			                }
//                        }),
//                        new sap.ui.commons.Link({
//                        	text: "( {attachmentCount} )",
//			                tooltip: "Upload attchment",
//			                wrapping: false,
//			                press: function(oEvent){
//			                	event.stopPropagation();	//prevent the event bubble up
//			                	oController.onAttachmentLinkClick(oEvent);
//			                }
//                        })
//		            ]
//                })
//            },
//            itemPress: function (oEvent) {
//                oController.handleItemPressed(oEvent);
//            }
//        });

        var oSortableTestRequestTable = new sap.ui.table.Table(this.createId("sortableTestRequestTable"), {
        	selectionMode: sap.ui.table.SelectionMode.Single,
   			visibleRowCount:  parseInt(($(window).height() - 144) / 48 -2), //10,
            selectionBehavior: sap.ui.table.SelectionBehavior.RowOnly,
            navigationMode: sap.ui.table.NavigationMode.Paginator,
//            rowSelectionChange: function (oEvent) {
//                oController.handleItemPressed(oEvent);
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
		                	oController.handleShowDetail(this.getText());
		                }
		            }),
                	sortProperty: "id", 
                	filterProperty: "id",
                	width: "5%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Name"}),
                	template: new sap.m.Text({
		                text: "{name}",
		                tooltip: "Name: {name}",
		                wrapping: false
		            }),
                	sortProperty: "name", 
                	filterProperty: "name",
                	width: "40%"
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
                	width: "10%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Category"}),
                	template: new sap.m.Text({
                    	text: "{category}",
		                tooltip: "Category: {category}",
		                wrapping: false
                    }),
                	sortProperty: "category", 
                	filterProperty: "category",
                	width: "10%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Prority"}),
                	template: new sap.m.Text({
                    	text: "{priority}",
		                tooltip: "Priority: {priority}",
		                wrapping: false
                    }),
                	sortProperty: "priority", 
                	filterProperty: "priority",
                	width: "10%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Owner"}),
                	template: new sap.m.Text({
                    	text: "{testOwner}",
		                tooltip: "Owner: {testOwner}",
		                wrapping: false
                    }),
                	sortProperty: "testOwner", 
                	filterProperty: "testOwner",
                	width: "10%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Status"}),
                	template: new sap.ui.commons.Link({
                    	text: { path: 'latestStatus', formatter: oController.formatter.testRequestStatusText },
		                tooltip: "Status: {latestStatus}",
		                wrapping: false,
		                press: function(oEvent){
		                	event.stopPropagation();	//prevent the event bubble up
		                	oController.handleStatusUpdate(oEvent);
		                }
                    }),
                	sortProperty: "latestStatus", 
                	filterProperty: "latestStatus",
                	width: "5%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Issue"}),
                	template: new sap.ui.commons.Link({
                    	text:  "( {issueCount} )",
		                tooltip: "Goto test issue edit",
		                wrapping: false,
		                press: function(oEvent){
		                	event.stopPropagation();	//prevent the event bubble up
		                	oController.onIssueLinkClick(oEvent);
		                }
                    }),
                	sortProperty: "issueCount", 
                	filterProperty: "issueCount",
                	width: "5%"
                }),
                new sap.ui.table.Column({
                	label: new sap.m.Label({text : "Attachment"}),
                	template: new sap.ui.commons.Link({
                    	text: "( {attachmentCount} )",
		                tooltip: "Upload attchment",
		                wrapping: false,
		                press: function(oEvent){
		                	event.stopPropagation();	//prevent the event bubble up
		                	oController.onAttachmentLinkClick(oEvent);
		                }
                    }),
                	sortProperty: "attachmentCount", 
                	filterProperty: "attachmentCount",
                	width: "5%"
                })
            ]
        });	
        //Page
        var page = new sap.m.Page(this.createId("page"), {
            title: "{i18n>perfTestRequestTitle}",
            subHeader: new sap.m.Toolbar({
                content: oSearchField
            }),
            content: [
//                oTestRequestTable,
                oSortableTestRequestTable
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
        }).addStyleClass("sapUiSizeCozy");
        return page;
	}

});
