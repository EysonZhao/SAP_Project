sap.ui.jsview("sme.perf.ui.view.TestRequestDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.TestRequestDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
		var panel = new sap.m.Panel({
			headerText:"Basic infomation for test",
			content:[
			        new sap.m.Text({
			        	text:"Codeline：{testRequestModel>/codeline}",
			        	width:"20%"
			        		}),
			        new sap.m.ComboBox(this.createId("combobox2"),{
			        	width:"25%",
						placeholder:"Codeline",
						items: {
	                    	path: 'AllIdsModel>/',
	                    	template: new sap.ui.core.ListItem({
	                    		key: '{AllIdsModel>name}',
	                    		text: '{AllIdsModel>name}'
	                    	})
	                    },
			        }),
			        new sap.m.Text({
			        	text:"PLNumber",
			        	width:"15%"
			        		}),
			        new sap.m.Input({
			        	width:"30%",
			        	value:"{testRequestModel>/plnumber}"
			        }),
			        new sap.m.Text({
			        	text:"CLNumber",
			        	width:"15%"
			        		}),
			        new sap.m.Input({
			        	width:"30%",
			        	value:"{testRequestModel>/clnumber}"
			        }),
			        new sap.m.CheckBox({
			        	selected:"{testRequestModel>/hana}",
			        	text:"IsHANARequest"
			        }),
			        new sap.m.CheckBox({
			        	selected:"{testRequestModel>/browserAccess}",
			        	text:"IsBrowserAccess"
			        })
			        
			]
			
		});
		
		var oForm2 = new sap.ui.layout.form.Form(this.createId("TestRequestDetailForm"),{
			title: new sap.ui.core.Title({text: "Test Request: {testRequestModel>/id}", tooltip: "Test Request {testRequestModel>/id}"}),
			editable: true,
			layout: new sap.ui.layout.form.GridLayout({defaultSpan: "L12 M12 S12"}),
			formContainers: [
				Utility.createFormContainerTitle("Overall Info"),
				Utility.createFormContainerInput("Name", 
						"{testRequestModel>/name}", undefined, true),
				Utility.createFormContainerComboBox("Project",  {
					comboBoxName: "projectComboBox",
					keyValuePath: "projectsModel>id",
					textValuePath: "projectsModel>name",
					itemsValuePath: "projectsModel>/projects",
					itemsValueModel: "projectsModel",
					parentValueModel: "testRequestModel",
					selectedField: "projectId"
				}, true),
				Utility.createFormContainerComboBox("Category",  {
					comboBoxName: "categoryComboBox",
					keyValuePath: "categoryModel>name",
					textValuePath: "categoryModel>name",
					itemsValuePath: "categoryModel>/categories",
					itemsValueModel: "categoryModel",
					parentValueModel: "testRequestModel",
					selectedField: "category"
				}, true),
				Utility.createFormContainerComboBox("Priority",  {
					comboBoxName: "priorityComboBox",
					keyValuePath: "priorityModel>name",
					textValuePath: "priorityModel>name",
					itemsValuePath: "priorityModel>/priorities",
					itemsValueModel: "priorityModel",
					parentValueModel: "testRequestModel",
					selectedField: "priority"
				}, true),
				Utility.createFormContainerInput("Background", "{testRequestModel>/background}", 4),
				Utility.createFormContainerInput("Overall Requirement", "{testRequestModel>/overallRequirement}", 4),
				
				Utility.createFormContainerTitle("Test Scenario"),
				
				Utility.createFormContainerInput("Company DB Name", "{testRequestModel>/companyDBName}", undefined, true),
				Utility.createFormContainerInput("Company DB Number", "{testRequestModel>/companyDBNumber}", undefined, true),
				
				Utility.createFormContainerInput("User Per Company", "{testRequestModel>/userPerCompany}", undefined, true),
				Utility.createFormContainerInput("Ramp up", "{testRequestModel>/rampupMode}", undefined, true),

				Utility.createFormContainerInput("Test Duration", "{testRequestModel>/testDuration}", undefined, true),
				Utility.createFormContainerInput("Think Time", "{testRequestModel>/thinktime}", undefined, true),

				Utility.createFormContainerInput("Detail Steps", "{testRequestModel>/detailStep}", 4),
				Utility.createFormContainerInput("Special Setting", "{testRequestModel>/specialSetting}", 4),
				Utility.createFormContainerInput("Others", "{testRequestModel>/others}", 4),
				
				Utility.createFormContainerTitle("Test Environment"),
				Utility.createFormContainerInput("Server", "{testRequestModel>/server}", 2),
				Utility.createFormContainerInput("Client", "{testRequestModel>/client}", 2),
				Utility.createFormContainerInput("Build", "{testRequestModel>/build}", undefined, true),
				
				Utility.createFormContainerTitle("Test Result"),
				Utility.createFormContainerInput("Monitor Object", "{testRequestModel>/monitorObject}", 4),
				Utility.createFormContainerInput("KPI", "{testRequestModel>/kpi}", 4),
				
				Utility.createFormContainerTitle("People"),
				Utility.createFormContainerInput("Requester", "{testRequestModel>/requester}", undefined, true),
				Utility.createFormContainerInput("Test Owner", "{testRequestModel>/testOwner}", undefined, true),
				Utility.createFormContainerInput("Stakeholders", "{testRequestModel>/stakeholder}", 2)
		    ]
		});
		var page = new sap.m.Page(this.createId('page'), {
		title: "{i18n>perfTestRequestDetailTitle}",
		showNavButton: true,
		navButtonPress: function (oEvent) {
		    oController.onNavBack(oEvent);
		},
		content: [
		          panel,
		         oForm2
		],
		footer: new sap.m.Bar({
			contentRight:[
			    new sap.m.Button({
			    	text: "New",
			    	press: function(){
			    		oController.onNew();
			    	}
			    }),
			    new sap.m.Button({
			    	text: "Duplicate",
		    	    	press: function(){
		    	    		oController.onDuplicate();
		    	    	}
		    	    }),
	    	    new sap.m.Button({
			    	text: "Save",
			    	press: function(){
			    		oController.onSave();
			    	}
			    })
		    	]
		    })
		});

return page;
	}

});