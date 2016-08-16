sap.ui.jsview("sme.perf.ui.view.ExecutionModule", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionModule";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
 		return new sap.m.Page({
			title: "Execution Module",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
//			    new sap.m.IconTabBar(this.createId("tabs", {
//			    	select : function(oEvent){
//			    		oController.onSelect(oEvent);
//			    	},
//			    	items:[
//			    	    new sap.m.IconTabFilter(this.createId("DetaiTab"),{
//			    	    	text: "Detail",
//			    	    	key: "detail",
//			    	    	content: [
//			    	    	     sap.ui.jsview("sme.perf.ui.view.ExecutionDetail")   
////			    	    	     new sap.ui.core.mvc.JSView({
////			    	    	    	 viewName: "sme.perf.ui.view.ExecutionDetail"
////			    	    	     })
//			    	    	]
//			    	    })
//			    	]
//			    }))
				sap.ui.jsview("sme.perf.ui.view.ExecutionDetail")
			],
			footer:new sap.m.Bar({
            	contentRight:[
//					new sap.m.Button({
//						text: "New",
//						press: function(oEvent){
//							oController.onNew(oEvent);
//						}
//					})
				]
			})
		});
	}

});