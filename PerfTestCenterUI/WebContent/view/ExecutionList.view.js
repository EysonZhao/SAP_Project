sap.ui.jsview("sme.perf.ui.view.ExecutionList", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionList";
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
                        oController.search(values);
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

        var fileUploader = new sap.ui.commons.FileUploader(this.createId("fileupload"),{
			name: "upload",
			multiple: false,
			visible:"{newControlModel>/uploadEnable}",
			//Only pressed NEW button then the fileUploder part can be used 
			width:"22%",
			change:function(){
				oController.uploadContentChanged();
			},
			placeholder:"Choose a file for {newControlModel>/id}...",
			uploadOnChange: false,
			fileSizeExceed: function (oEvent) {
				var sName = oEvent.getParameter("fileName");
				var fSize = oEvent.getParameter("fileSize");
				var fLimit = oFileUploader2.getMaximumFileSize();
				sap.ui.commons.MessageBox.show("File: " + sName + " is of size " + fSize + " MB which exceeds the file size limit of " + fLimit + " MB.", "ERROR", "File size exceeded");
			},
			uploadComplete: function (oEvent) {
				sap.m.MessageToast.show("Upload Completed.");
			}
	    });
        
        var oTestRequestTable = new sap.m.Table(this.createId('table'), {
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
                          width: "22%",
                          header: new sap.m.Label({
                              text: "Name"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "8%",
                          header: new sap.m.Label({
                              text: "State"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "9%",
                          header: new sap.m.Label({
                              text: "CreateDate"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "9%",
                          header: new sap.m.Label({
                              text: "StartDate"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "9%",
                          header: new sap.m.Label({
                              text: "EndDate"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Run"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Pause"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Continue"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Stop"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Log"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Duration"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Export"
                          })
                      }),
                      new sap.m.Column({
                          hAlign: "Left",
                          width: "auto",
                          header: new sap.m.Label({
                              text: "Import"
                          })
                      })
                      
            ],
            items: {
                path: "executionListModel>/",
                sorter: new sap.ui.model.Sorter("id", true),
                template: new sap.m.ColumnListItem({
                    type: sap.m.ListType.Navigation,
                    vAlign: sap.ui.core.VerticalAlign.Middle,
                    cells: [
			            new sap.ui.commons.Link({
			                text: "{executionListModel>id}",
			                tooltip: "ID: {executionListModel>id}",
			                wrapping: false,
			                press: function(oEvent){
			                	oController.handleShowDetail(this.getText());
			                }
			            }),
			            
			            
			            new sap.m.Text({
			                text: "{executionListModel>name}",
			                tooltip: "Name: {executionListModel>name}",
			                wrapping: false
			            }),
			            new sap.m.Text({
			                text: "{executionListModel>state}",
			                tooltip: "State: {executionListModel>state}",
			                wrapping: false
			            }),
                        new sap.m.Text({
                        	text: "{executionListModel>createDate}",
			                tooltip: "createDate: {executionListModel>createDate}",
			                wrapping: false
                        }),
                        new sap.m.Text({
                        	text: "{executionListModel>startDate}",
			                tooltip: "StartDate: {executionListModel>startDate}",
			                wrapping: false
                        }),
                        new sap.m.Text({
                        	text: "{executionListModel>endDate}",
			                tooltip: "EndDate: {executionListModel>endDate}",
			                wrapping: false
                        }),
                        new sap.ui.core.Icon({
                        	src: sap.ui.core.IconPool.getIconURI('add-process'),
                        	tooltip: 'Run',
                        	size: '2em',
                        	visible: "{= ${executionListModel>state} === 'New' ? true : false }",
                        	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
                        	press: function(oEvent){
                        		oController.onRunBtnPress(oEvent);
                        	}
                        }),
                        new sap.ui.core.Icon({
                        	src: sap.ui.core.IconPool.getIconURI('media-pause'),
                        	tooltip: 'Pause',
                        	size: '2em',
                        	visible: "{= ${executionListModel>state} === 'Running' ? true : false }",
                        	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
                        	press: function(oEvent){
                        		oController.onPauseBtnPress(oEvent);
                        	}
                        }),
                        new sap.ui.core.Icon({
                        	src: sap.ui.core.IconPool.getIconURI('media-play'),
                        	tooltip: 'Continue',
                        	size: '2em',
                        	visible: "{= ${executionListModel>state} === 'Paused' ? true : false }",
                        	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
                        	press: function(oEvent){
                        		oController.onContinueBtnPress(oEvent);
                        	}
                        }),
                        new sap.ui.core.Icon({
                        	src: sap.ui.core.IconPool.getIconURI('stop'),
                        	tooltip: 'Stop',
                        	size: '2em',
                        	visible: "{= ${executionListModel>state} === 'Running' ? true : false }",
                        	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
                        	press: function(oEvent){
                        		oController.onStopBtnPress(oEvent);
                        	}
                        }),
//                        new sap.m.Button({
//                        	text: "Run",
//                        	enabled: "{= ${executionListModel>state} === 'New' ? true : false }",
//                        	customData: [
//                        	     new sap.ui.core.CustomData({
//                                	key: "executionId",
//                                	value: "{executionListModel>id}",
//                                	writeToDom: false
//                                })
//                        	],
//                        	press: function(oEvent){
//                        		oController.onRunBtnPress(oEvent);
//                        	}
//                        }),
                        new sap.ui.core.Icon({
                        	src: sap.ui.core.IconPool.getIconURI('notes'),
                        	tooltip: 'Log',
                        	size: '2em',
                        	visible: "{= ${executionListModel>state} === 'New' ? false : true}",
                        	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
                        	press: function(oEvent){
                        		oController.onLogBtnPress(oEvent);
                        	}
                          }),
//                        new sap.m.Button({
//                        	text: "Log",
//                        	enabled: "{= ${executionListModel>state} === 'New' ? false : true}",
//                        	customData: [
//                        	     new sap.ui.core.CustomData({
//                                	key: "executionId",
//                                	value: "{executionListModel>id}",
//                                	writeToDom: false
//                                })
//                        	],
//                        	press: function(oEvent){
//                        		oController.onLogBtnPress(oEvent);
//                        	}
//                        }),
	                      new sap.ui.core.Icon({
	                      	src: sap.ui.core.IconPool.getIconURI('time-entry-request'),
	                      	tooltip: 'Duration',
	                      	size: '2em',
	                      	visible: "{= ${executionListModel>state} === 'Finished' ? true : false}",
	                      	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
	                      	press: function(oEvent){
	                      		oController.onDurationBtnPress(oEvent);
	                      	}
                        }),
//                        new sap.m.Button({
//                        	text: "Duration",
//                        	enabled: "{= ${executionListModel>state} === 'Finished' ? true : false}",
//                        	customData: [
//                        	     new sap.ui.core.CustomData({
//                                	key: "executionId",
//                                	value: "{executionListModel>id}",
//                                	writeToDom: false
//                                })
//                        	],
//                        	press: function(oEvent){
//                        		oController.onDurationBtnPress(oEvent);
//                        	}
//                        }),
	                      new sap.ui.core.Icon({
		                      	src: sap.ui.core.IconPool.getIconURI('outbox'),
		                      	tooltip: 'Export',
		                      	size: '2em',
		                      	visible: "{= ${executionListModel>state} === 'New' ? true : false}",
		                      	customData: [
									new sap.ui.core.CustomData({
										key: "executionId",
										value: "{executionListModel>id}",
										writeToDom: false
									})
								],
		                      	press: function(oEvent){
		                      		oController.onExportBtnPress(oEvent);
		                      	}
	                        }),
//                        new sap.m.Button({
//                        	text: "Export",
//                        	enabled: "{= ${executionListModel>state} === 'New' ? true : false}",
//                        	customData: [
//                        	     new sap.ui.core.CustomData({
//                                	key: "executionId",
//                                	value: "{executionListModel>id}",
//                                	writeToDom: false
//                                })
//                        	],
//                        	press: function(oEvent){
//                        		oController.onExportBtnPress(oEvent);
//                        	}
//                        }),
                      new sap.ui.core.Icon({
	                      	src: sap.ui.core.IconPool.getIconURI('inbox'),
	                      	tooltip: 'Import',
	                      	size: '2em',
	                      	visible:  "{= ${executionListModel>state} === 'New' ? true : false}",
	                      	customData: [
								new sap.ui.core.CustomData({
									key: "executionId",
									value: "{executionListModel>id}",
									writeToDom: false
								})
							],
	                      	press: function(oEvent){
	                      		oController.onImportBtnPress(oEvent);
	                      	}
                        })
//                        new sap.m.Button({
//                        	text: "Import",
//                        	enabled: "{= ${executionListModel>state} === 'New' ? true : false}",
//                        	customData: [
//                        	     new sap.ui.core.CustomData({
//                                	key: "executionId",
//                                	value: "{executionListModel>id}",
//                                	writeToDom: false
//                                })
//                        	],
//                        	press: function(oEvent){
//                        		oController.onImportBtnPress(oEvent);
//                        	}
//                        })			            
		            ]
                })
            },
            itemPress: function (oEvent) {
                oController.onItemPress(oEvent);
            }
        });
        var optionpanel = new sap.m.Panel({
        	visible:"{ShowOptionModel>/show}",
        	content:[
        	         new sap.m.Panel({
        	        	 content:[
        	        	          new sap.m.Label({text:"State"}),
        	         	         new sap.m.CheckBox({
        	         	        	 text:"Finished",
        	         	        	 selected:"{filterModel>/Finished}"
        	         	         }),
        	         	         new sap.m.CheckBox({
        	         	        	 text:"Failed",
        	         	        	 selected:"{filterModel>/Failed}"
        	         	         }),
        	         	         new sap.m.CheckBox({
        	         	        	 text:"Running",
        	         	        	 selected:"{filterModel>/Running}"
        	         	         }),
        	         	         new sap.m.CheckBox({
        	         	        	 text:"New",
        	         	        	 selected:"{filterModel>/New}"
        	         	         }),
        	         	         new sap.m.CheckBox({
        	         	        	 text:"Ready",
        	         	        	 selected:"{filterModel>/Ready}"
        	         	         }),
        	         	        new sap.m.CheckBox({
	       	         	        	 text:"Terminated",
	       	         	        	 selected:"{filterModel>/Terminated}"
        	         	        }),
       	         	         new sap.m.CheckBox({
    	         	        	 text:"Paused",
    	         	        	 selected:"{filterModel>/Paused}"
    	         	         }),
        	        	          ]
        	         }),
        	         new sap.m.ActionSelect(this.createId('dateTypeSelect'),{
        	        	 width:"20%",
								items: [
										oItem0 = new sap.ui.core.Item("default_select",{
											key: "createDate",
											text: "CreateDate"
										}),
										oItem1 = new sap.ui.core.Item({
											key: "startDate",
											text: "StartDate"
										}),
										oItem2 = new sap.ui.core.Item({
											key: "endDate",
											text: "EndDate"
										})
									],
								change: function(oEvent){
		            	    		oController.onChangePeriod(oEvent);
		            	    	}
						}),//end actionselect
						new sap.m.Label({
							textAlign:"Right",
							text:"From Date:",
							width:"7%"
						}),
						new sap.m.DatePicker(this.createId("fromDatePicker"),{
							width:"33%",
		                	displayFormat: "yyyy-MM-dd",
		                	valueFormat: "yyyy-MM-dd",
		                	value: {
		                    	path: "queryModel>/from",
		                    	type: new sap.ui.model.type.Date({source: {pattern: "yyyy-MM-dd HH:mm:ss"}, pattern: "yyyy-MM-dd"})
		                	}
						}),
						new sap.m.Label({
							textAlign:"Right",
							text:"To Date:",
							width:"7%"
						}),
						new sap.m.DatePicker(this.createId("toDatePicker"),{
							width:"33%",
		                	displayFormat: "yyyy-MM-dd",
		                	valueFormat: "yyyy-MM-dd",
		                	value: {
		                    	path: "queryModel>/to",
		                    	type: new sap.ui.model.type.Date({source: {pattern: "yyyy-MM-dd HH:mm:ss"}, pattern: "yyyy-MM-dd"})
		                	}
						}),
						
						new sap.m.Bar({
							contentRight:[
							     new sap.m.Button({
							    	 text:"Refresh",
							    	 press:function(oEvent){
							    		 oController.onFilter();
							    	 }
							     })
							]
						})
						
        	]
        
        });
        
        //Page
        var page = new sap.m.Page(this.createId("page"), {
            title: "{i18n>executionListTitle}",
            subHeader: new sap.m.Toolbar({
                content: [oSearchField,
                          new sap.m.CheckBox(this.createId("showOptions"),{
                        	  text:"Show further filters...",
                        	  selected: "{ShowOptionModel>/show}"
                          })
                          ]
            }),
            content: [
                      optionpanel,
                      new sap.m.Panel({
                    	  content:[ oTestRequestTable]                    	 
                      })
            ],
            footer: 
            	new sap.m.Bar({
            		
            	contentLeft:[
            	        fileUploader,	
						new sap.m.Button(this.createId("UploadButton"),{
						text: "Upload",
						icon: "sap-icon://inbox",
						width: "10%",
						enabled:"{newControlModel>/newEnable}",
						visible:"{newControlModel>/uploadEnable}",
						press: function(){
							oController.onUploadFile(fileUploader);
						}
						})
            	             ],	
            	contentRight:[
					new sap.m.Button({
						text: "Refresh",
						icon: "sap-icon://refresh",
						press: function(){
							oController.refresh();
						}
					}),      	              
            	    new sap.m.Button({
            	    	text: "New",
            	    	press: function(){
            	    		oController.onNew();
            	    	}
            	    })
      	         ]
            })
        })
        return page;
	}

});
