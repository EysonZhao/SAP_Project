sap.ui.jsview("sme.perf.ui.view.ProjectList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ProjectList";
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
            selectionMode: sap.ui.table.SelectionMode.Single,
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
                    width: "20%",
                    header: new sap.m.Label({
                        text: "Name"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "30%",
                    header: new sap.m.Label({
                        text: "Description"
                    })
                }),
                new sap.m.Column({
                    hAlign: "Left",
                    width: "45%",
                    header: new sap.m.Label({
                        text: "Parameter"
                    })
                })
            ],
            items: {
                path: "projectListModel>/projectList",
                sorter: new sap.ui.model.Sorter("id", true),
                template: new sap.m.ColumnListItem({
                    type: sap.m.ListType.Active,
                    vAlign: sap.ui.core.VerticalAlign.Middle,
                    cells: [
			            new sap.ui.commons.Link({
			                text: "{projectListModel>id}",
			                tooltip: "ID: {projectListModel>id}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{projectListModel>name}",
			                tooltip: "Name: {projectListModel>name}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{projectListModel>description}",
			                tooltip: "Description: {projectListModel>description}",
			                wrapping: false
			            }),
                        new sap.m.Text({
                        	text: "{projectListModel>parameterTemplateJson}",
			                tooltip: "Parameter: {projectListModel>parameterTemplateJson}",
			                wrapping: false
                        })
		            ]
                })
            },
            itemPress: function (oEvent) {
                oController.onItemPress(oEvent);
            }
        });
        
        var page = new sap.m.Page(this.createId("page"), {
            title: "{i18n>projectListTitle}",
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
        
        return page;
	}

});