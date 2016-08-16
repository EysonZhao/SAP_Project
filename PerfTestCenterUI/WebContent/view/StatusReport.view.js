sap.ui.jsview("sme.perf.ui.view.StatusReport", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.StatusReport";
	},
	//get the databinding string
	getDBStr: function(modelIndex, bindingPath){
		return "{statusReportModel_" +  modelIndex + ">" + bindingPath + "}";
	},
	getDBPath: function(modelIndex, bindingPath){
		return "statusReportModel_" +  modelIndex + ">" + bindingPath;
	},	
	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	mPanels: [],
	getPanels: function(){
		return this.mPanels;
	},
	createContent : function(oController) {
		var queryForm = new sap.ui.layout.form.Form(this.createId("QueryForm"),{
			editable: true,
			width: "800px",
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
		    formContainers: [
		        Utility.createFormContainerWithControl("Start Date",new sap.m.DatePicker({
                	displayFormat: "yyyy-MM-dd",
                	valueFormat: "yyyy-MM-dd",
                	value: {
                    	path: "queryModel>/startDate",
                    	type: new sap.ui.model.type.Date({source: {pattern: "yyyy-MM-dd HH:mm:ss"}, pattern: "yyyy-MM-dd"})
                	}
				}), true),
		        Utility.createFormContainerWithControl("End Date", new sap.m.DatePicker({
                	displayFormat: "yyyy-MM-dd",
                	valueFormat: "yyyy-MM-dd",
                	value: {
                    	path: "queryModel>/endDate",
                    	type: new sap.ui.model.type.Date({source: {pattern: "yyyy-MM-dd HH:mm:ss"}, pattern: "yyyy-MM-dd"})
                	}
				}), true)
		    ]
		});
		
		for(var i=0 ; i<10 ; i++){
			var oPanelName = "Panel_" + i;
			var oNewPanel = new sap.m.Panel(this.createId(oPanelName), {
				headerToolbar: new sap.m.Toolbar({
					content: [
					    new sap.m.Title({
					    	text: this.getDBStr(i, "/projectName"),
					    	titleStyle: sap.ui.core.TitleLevel.H3
					    })
					]
				}),
				content:[
		    	    new sap.m.Title({
		    	    	text: "Status",
		    	    	titleStyle: sap.ui.core.TitleLevel.H4,
		    	    	visible: "{= $" + this.getDBStr(i, "/activeTestRequestsCount") + " > 0 ? true : false }"
		    	    })
		    	    ,new sap.m.List({
		    	    	items:{
		    	    		path: this.getDBPath(i, "/activeTestRequests"),
		    	    		template: new sap.m.StandardListItem({
		    	    			title: this.getDBStr(i, "testRequestName"),
		    	    			description: {
		    	    				path: this.getDBPath(i, "lastUpdateDate"),
		    	    				formatter: oController.formatter.formatDateTimeStringToDateString
		    	    			},
		    	    			info: this.getDBStr(i, "latestStatus")
		    	    		})
		    	    	},
		    	    	visible: "{= $" + this.getDBStr(i, "/activeTestRequestsCount") + " > 0 ? true : false }"
		    	    })
		    	    ,new sap.m.Title({
		    	    	text: "Plan",
		    	    	titleStyle: sap.ui.core.TitleLevel.H4,
		    	    	visible: "{= $" + this.getDBStr(i, "/planTestRequestsCount") + " > 0 ? true : false }"
		    	    }).addStyleClass("status-report-title")
		    	    ,new sap.m.List({
		    	    	items:{
		    	    		path: this.getDBPath(i, "/planTestRequests"),
		    	    		template: new sap.m.StandardListItem({
		    	    			title: this.getDBStr(i, "testRequestName"),
		    	    			description: {
		    	    				path: this.getDBPath(i, "priority"),
		    	    				formatter: oController.formatter.formatDateTimeStringToDateString
		    	    			},
		    	    			info: this.getDBStr(i, "latestStatus")
		    	    		})
		    	    	},
		    	    	visible: "{= $" + this.getDBStr(i, "/planTestRequestsCount") + " > 0 ? true : false }"
		    	    })
		    	    ,new sap.m.Title({
		    	    	text: "Blocked",
		    	    	titleStyle: sap.ui.core.TitleLevel.H4,
		    	    	visible: "{= $" + this.getDBStr(i, "/blockedTestRequestsCount") + " > 0 ? true : false }"
		    	    }).addStyleClass("status-report-title")
		    	    ,new sap.m.List({
		    	    	items:{
		    	    		path: this.getDBPath(i, "/blockedTestRequests"),
		    	    		template: new sap.m.StandardListItem({
		    	    			title: this.getDBStr(i, "testRequestName"),
		    	    			description: {
		    	    				path: this.getDBPath(i, "priority"),
		    	    				formatter: oController.formatter.formatDateTimeStringToDateString
		    	    			},
		    	    			info: this.getDBStr(i, "latestStatus")
		    	    		})
		    	    	},
		    	    	visible: "{= $" + this.getDBStr(i, "/blockedTestRequestsCount") + " > 0 ? true : false }"
		    	    })
		    	    /*
		    	    ,new sap.m.Title({
		    	    	text: "Issues",
		    	    	titleStyle: sap.ui.core.TitleLevel.H4
		    	    }).addStyleClass("status-report-title")
		    	    ,new sap.m.List({
		    	    	items:{
		    	    		path: this.getDBPath(i, "/issues"),
		    	    		template: new sap.m.StandardListItem({
		    	    			title: this.getDBStr(i, "title"),
		    	    			type: sap.m.ListType.Navigation,
		    	    			description: {
		    	    				path: this.getDBPath(i, "status"),
		    	    				formatter: oController.formatter.formatDateTimeStringToDateString
		    	    			},
		    	    			info: this.getDBStr(i, "priority")
		    	    		})
		    	    	},
		    	    	itemPress: function(oEvent){
		    	    		oController.onIssueItemPress(oEvent);
		    	    	}
		    	    }) */
		    	]
			});
			oNewPanel.setVisible(false);
			this.mPanels.push(oNewPanel);
		}
 	 	return new sap.m.Page({
 			title: "Status Report",
 			showNavButton: true,
 			navButtonPress: function (oEvent) {
 			    oController.onNavBack(oEvent);
 			},
			content: [
			    queryForm,
				new sap.ui.layout.Grid(this.createId('StatusReportForm'), {
					width: "800px",
					defaultSpan: "L12 M12 S12",
				    content: this.mPanels				    
				})
			],
			footer:new sap.m.Bar({
            	contentRight:[
            	    new sap.m.Button({
            	    	text: "Generate Report",
            	    	icon: "sap-icon://action",
            	    	press: function(oEvent){
            	    		oController.onGenerateReport(oEvent);
            	    	}
            	    })
				]
			})
 		})
	}

});