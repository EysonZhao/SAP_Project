sap.ui.jsview("sme.perf.ui.view.ZIssueReport", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	panels:[],
	getPanels: function(){
		return this.panels;
	},
	_mCountModel:new sap.ui.model.json.JSONModel(),
	getDBStr: function(modelIndex, bindingPath){
		return "{issueReportModel_" +  modelIndex + ">" + bindingPath + "}";
	},
	getDBPath: function(modelIndex, bindingPath){
		return "issueReportModel_" +  modelIndex + ">" + bindingPath;
	},	
	getControllerName : function() {
		return "sme.perf.ui.controller.ZIssueReport";
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
				}), true),
				
		        
		    ]
		});
		
		var optionPanel= new sap.m.Panel({
			headerToolbar: new sap.m.Toolbar({
				content: [
				    new sap.m.Title({
				    	text: "Options",
				    	titleStyle: sap.ui.core.TitleLevel.H4
				    })
				]
			}),
			content:[
//			         new sap.m.Label({
//			        	 text:"{queryModel>/period}",
//			        	 width:"100%"
//			         }),
			         new sap.m.CheckBox({
			        	 width:"100%",
			        	 text:"Display earlier items",
			        	 selected: "{DefaultOptionModel>/checked}"
			        		 
			         }),
//			         new sap.m.Label({
//			        	 textAlign: "End",
//			        	 width:"10%",
//			        	 text:"Period",
//			        	 visible:"{DefaultOptionModel>/checked}",
//			         }),
			         new sap.m.ActionSelect(this.createId('PeriodSelect'),{
							width:"20%",
								items: [
										oItem0 = new sap.ui.core.Item("default_select",{
											key: "30",
											text: "One Month"
										}),
										oItem1 = new sap.ui.core.Item({
											key: "90",
											text: "Three Months"
										}),
										oItem2 = new sap.ui.core.Item({
											key: "180",
											text: "Half One Year"
										}),
										oItem3 = new sap.ui.core.Item({
											key: "365",
											text: "One Year"
										})
									],
								change: function(oEvent){
		            	    		oController.onChangePeriod(oEvent);
		            	    	},
								visible:"{DefaultOptionModel>/checked}",
						}),//end actionselect

				
			         ]
		})
		
		for(var i=0;i<10;i++){
			var panel=new sap.m.Panel({
				headerToolbar: new sap.m.Toolbar({
					content: [
					    new sap.m.Title({
					    	text: "{issueReportModel_"+i+">/projectName}",
					    	titleStyle: sap.ui.core.TitleLevel.H3
					    })
					]
				}),
				content:[

				new sap.m.Panel({
					content:[
					         new sap.m.Title({
								text: "Issues in the select period",
								level: sap.ui.core.TitleLevel.H3,
								titleStyle: sap.ui.core.TitleLevel.H4,
								visible: this.getDBStr(i, "/nearlyvisible")
					         }),
					         new sap.m.List({
									items:{
					    	    		path: this.getDBPath(i, "/nearlySubmittedList"),
					    	    		template: new sap.m.CustomListItem({
						                	content: [
						                          new sap.m.Label({
						                        	  width:"20%",
						                        	  text:"[{issueReportModel_"+i+">jiraKey}]",
						                        	  tooltip:"Create Date : {issueReportModel_"+i+">createDate} Status : {issueReportModel_"+i+">status}"
						                          })  ,
						                          new sap.m.Text({
						                        	  width:"70%",
						                        	  text:"{issueReportModel_"+i+">title}",
						                        	  tooltip:"Create Date : {issueReportModel_"+i+">createDate} Status : {issueReportModel_"+i+">status}"
						                          })  ,
						                          new sap.m.Text({
						                        	  width:"10%",
						                        	  text:"({issueReportModel_"+i+">priority})"
						                        	  
						                          })  ,
					    	    			]
					    	    		})
					    	    	},
					    	    	visible: this.getDBStr(i, "/nearlyvisible") 
					    	    })
					         ]
					}),
					new sap.m.Panel({
							content:[
						new sap.m.Title({
							text: "Earlier Submitted in {TimeModel>/time}",
							titleStyle: sap.ui.core.TitleLevel.H4,
						}),
						new sap.m.List({
							items:{
			    	    		path: this.getDBPath(i, "/olderSubmittedList"),
			    	    		template: new sap.m.CustomListItem({
				                	content: [
				                          new sap.m.Label({
				                        	  width:"20%",
				                        	  text:"[{issueReportModel_"+i+">jiraKey}] ",
				                        	  tooltip:"Create Date : {issueReportModel_"+i+">createDate} Status : {issueReportModel_"+i+">status}"
				                          })  ,
				                          new sap.m.Text({
				                        	  width:"70%",
				                        	  text:"{issueReportModel_"+i+">title}",
				                        	  tooltip:"Create Date : {issueReportModel_"+i+">createDate} Status : {issueReportModel_"+i+">status}"
				                          })  ,
				                          new sap.m.Text({
				                        	  width:"10%",
				                        	  text:"({issueReportModel_"+i+">priority})"
				                          })  ,
			    	    			]
			    	    		})
			    	    	},
			    	    }),
			    	    ],
			    	    visible: (true&&this.getDBStr(i,"oldervisible"))&&(true&&"{DefaultOptionModel>/checked}")
					}),
			    ],
			    visible:false
			});
			
		this.panels.push(panel);
		}
		return new sap.m.Page({
				title: "Test Issue Report",
				showNavButton: true,
				navButtonPress: function (oEvent) {
				    oController.onNavBack(oEvent);
				},
				content: [
				    queryForm,
				    optionPanel,
				    this.panels
				],
				footer:new sap.m.Bar({

					contentLeft:[
				            	    new sap.m.Button({
				            	    	text: "Generate Report",
				            	    	icon: "sap-icon://post",
				            	    	press: function(){
				            	    		oController.toReport();
				            	    	}
				            	    }),
				            	    new sap.m.Button({
				            	    	text: "Download Report",
				            	    	icon: "sap-icon://download",
				            	    	press: function(){
				            	    		oController.onDownload();
				            	    	}
				            	    })
					             ],
	            	contentRight:[
	            	    new sap.m.Button({
	            	    	text: "Issue Report",

	            	    	icon: "sap-icon://action",
	            	    	press: function(oEvent){
	            	    		oController.onGenerateReport(oEvent);
	            	    	}
	            	    })
					]
				})
				
	});
	}
});