sap.ui.jsview("sme.perf.ui.view.AnalysisTransaction", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.AnalysisTransaction";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		var mtable = new sap.m.Table(this.createId("TransactionTable"),{
			headerText: "Transaction Report",
		    growing: true,
		    hAlign:"Center",
		    growingScrollToLoad: false,
		    columns:[
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"25%",
		        	  header: new sap.m.Text({
		        		  text:"Name"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"15%",
		        	  header: new sap.m.Text({
		        		  text:"AvgRspTime"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"15%",
		        	  header: new sap.m.Text({
		        		  text:"MaxRspTime"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"15%",
		        	  header: new sap.m.Text({
		        		  text:"MinRspTime"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"10%",
		        	  header: new sap.m.Text({
		        		  text:"Pass"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"10%",
		        	  header: new sap.m.Text({
		        		  text:"Fail"
		        	  })
		          })
		          
		             ],
		             items:{
		            	 path:"TransactionModel>/",
		            	 template:
		            		 new sap.m.ColumnListItem({
		            			 vAlign: sap.ui.core.VerticalAlign.Middle,
			            		 type:sap.m.ListType.Active,
			            		 cells:[
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>transactionName}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>avgRspTm}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>maxRspTm}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>minRspTm}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>pass}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{TransactionModel>fail}"
			            	        })
			            	        ]
		            		 })
		            		 
		            	 
		             }
		});
		
		var mtable2 = new sap.m.Table(this.createId("ThroughputTable"),{
			headerText: "Throughput Report",
		    growing: true,
		    hAlign:"Center",
		    growingScrollToLoad: false,
		    columns:[
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"12%",
		        	  header: new sap.m.Text({
		        		  text:"N"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"22%",
		        	  header: new sap.m.Text({
		        		  text:"StartTime"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"22%",
		        	  header: new sap.m.Text({
		        		  text:"TransactionName"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"22%",
		        	  header: new sap.m.Text({
		        		  text:"AvgResponseTime"
		        	  })
		          }),
		          new sap.m.Column({
		        	  hAlign:"Center",
		        	  width:"22%",
		        	  header: new sap.m.Text({
		        		  text:"TPS"
		        	  })
		          })		          
		         ],
		             items:{
		            	 path:"ThroughputModel>/",
		            	 template:
		            		 new sap.m.ColumnListItem({
		            			 vAlign: sap.ui.core.VerticalAlign.Middle,
			            		 type:sap.m.ListType.Active,
			            		 cells:[
			            	        new sap.m.Text({
			            	        	text:"{ThroughputModel>n}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{ThroughputModel>startTime}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{ThroughputModel>transactionName}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{ThroughputModel>avgResponseTime}"
			            	        }),
			            	        new sap.m.Text({
			            	        	text:"{ThroughputModel>tps}"
			            	        })
			            	        ]
		            		 })
		            		 
		            	 
		             }
		});
		
		var panel = new sap.m.Panel({
			content:[
						new sap.m.Bar({
							contentLeft:[
							             new sap.m.CheckBox({
							            	 text:"Use view as source",
							            	 selected:"{SelectModel>/select}"
							             })
							],
		 					contentMiddle:[
								new sap.m.ComboBox(this.createId("combobox"),{
									placeholder:"ResultSessionId",
									items: {
				                    	path: 'AllIdsModel>/',
				                    	template: new sap.ui.core.ListItem({
				                    		key: '{AllIdsModel>resultSessionId}',
				                    		text: '{AllIdsModel>resultSessionId}'
				                    	})
				                    },
								})
		 					],
		 					 contentRight:[
		 					    new sap.m.Button({
		 					    	text:"Analyse",
		 					    	press: function(){
		 					    		oController.onShow();
		 					    	}
		 					    })            
		 					]              
		 				}),
		 				mtable
			         ]
			        
		});
		
		var viz = new sap.viz.ui5.controls.VizFrame(this.createId("vizframe"), {
			height:'70%', 
			width:"80%", 
			vizType:'line',
			dataset:[
			    new sap.viz.ui5.data.FlattenedDataset({
			    	dimensions :[{
			    		name:'time',
		    			value:'{date}',
		    			type:'timeAxis'
			    	}	
			    	],
			    	measures :[{
			    		name:'user numbers',
		    			value:'{userNumber}'
			    	}
			    	],
			    	data:{
			    		path:"usersModel>/"
			    	}
			    }) 
			],
			feeds:[
			       new sap.viz.ui5.controls.common.feeds.FeedItem({
			    	   'uid':"valueAxis",
			    	   'type':"Measure",
			    	   'values':["user numbers"]
			       }),
			       new sap.viz.ui5.controls.common.feeds.FeedItem({
			    	   'uid':"categoryAxis",
			    	   'type':"Dimension",
			    	   'values':["time"]
			       })
			]
			
		});
		
		var panel2 = new sap.m.Panel({
			content:[
					new sap.m.Bar({
						contentLeft:[
						    new sap.m.ComboBox(this.createId("combobox2"),{
								placeholder:"ResultSessionId",
								items: {
					            	path: 'AllIdsModel>/',
					            	template: new sap.ui.core.ListItem({
					            		key: '{AllIdsModel>resultSessionId}',
					            		text: '{AllIdsModel>resultSessionId}'
					            	})
					            },
							})         
						],
						contentMiddle:[
							new sap.m.Input(this.createId("input1"),{
								placeholder:"Granularity",
							})
						],
						 contentRight:[
							new sap.m.Input(this.createId("input2"),{
								placeholder:"Transaction Name",
							})      
						]              
					}),
					
					new sap.m.Bar({
						 contentRight:[
							new sap.m.Button({
						        text:"Get Throughput",
						        press:function(){
						        	oController.onThrouput();
						        }
					     })     
						 ]  	
					}),
					
			         mtable2,
	 			     
			]
		});
		
		var panel3 = new sap.m.Panel({
			title: new sap.m.Title({
				text:"User Numbers"
			}),
			content:[
			         new sap.m.Bar({
			        	 contentRight:[
					         new sap.m.Button({
					        	 text:"Generate Users Chart",
					        	 press:function(){
					        		oController.onChart();
					        	 }
				        	 }) 
			        	 ]
			         })    
			]
			
		});
		
 		return new sap.m.Page({
 			title : "Analysis Transaction",
 			content:[
 			         panel,
 			         panel2,
 			         panel3,
 			         viz
 			         ],
 			footer:new sap.m.Bar({})
 				
 			});
 		}	

});