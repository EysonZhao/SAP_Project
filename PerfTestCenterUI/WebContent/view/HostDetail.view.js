sap.ui.jsview("sme.perf.ui.view.HostDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.HostDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {

		var oHostForm = new sap.ui.layout.form.SimpleForm({
			title: "Host: {hostModel>/id}",
			editable: true,
			layout: "ResponsiveGridLayout",
			content: [
			    new sap.m.Label({
			    	text: "Host Name"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/hostName}"
			    }),
			    new sap.m.Label({
			    	text: "IP"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/ip}"
			    }),
			    new sap.m.Label({
			    	text: "Mode"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/mode}"
			    }),
			    new sap.m.Label({
			    	text: "Memory (GB)"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/memoryGB}"
			    }),
			    new sap.m.Label({
			    	text: "CPU Cores"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/cpuCore}"
			    }),
			    new sap.m.Label({
			    	text: "Freqency (GHz)"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/frequency}"
			    }),
			    new sap.m.Label({
			    	text: "DISK (GB)"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/diskGB}"
			    }),
			    new sap.m.Label({
			    	text: "OS"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/operationSystem}"
			    }),
			    new sap.m.Label({
			    	text: "UserName"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/userName}"
			    }),
			    new sap.m.Label({
			    	text: "Password"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/userPassword}"
			    }),
			    new sap.m.Label({
			    	text: "Description"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/description}"
			    }),
			    new sap.m.Label({
			    	text: "ILO Address"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/iloAddress}"
			    }),
			    new sap.m.Label({
			    	text: "ILO User"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/iloUser}"
			    }),
			    new sap.m.Label({
			    	text: "ILO Password"
			    }),
			    new sap.m.Input({
			    	value: "{hostModel>/iloPassword}"
			    })
//			    ,
//			    Utility.createFormContainerInput("HostName", "{hostModel>/hostName}", undefined, true),
//			    Utility.createFormContainerInput("IP", "{hostModel>/ip}", undefined, true),
//			    Utility.createFormContainerInput("Mode", "{hostModel>/mode}", undefined, true),
//			    Utility.createFormContainerInput("Memory (GB)", "{hostModel>/memoryGB}", undefined, true),
//			    Utility.createFormContainerInput("CPU Cores", "{hostModel>/cpuCore}", undefined, true),
//			    Utility.createFormContainerInput("DISK (GB)", "{hostModel>/diskGB}", undefined, true),
//			    Utility.createFormContainerInput("OS", "{hostModel>/operationSystem}", undefined, true),
//			    Utility.createFormContainerInput("UserName", "{hostModel>/userName}", undefined, true),
//			    Utility.createFormContainerInput("Password", "{hostModel>/userPassword}", undefined, true),
//			    Utility.createFormContainerInput("Description", "{hostModel>/description}", undefined)
			]
		});
		
 		return new sap.m.Page({
			title: "{i18n>hostDetailTitle}",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			          oHostForm
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
	}

});
