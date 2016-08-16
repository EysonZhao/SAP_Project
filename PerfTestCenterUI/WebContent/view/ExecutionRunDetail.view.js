sap.ui.jsview("sme.perf.ui.view.ExecutionRunDetail", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.Template
	*/ 
	getControllerName : function() {
		return "sme.perf.ui.controller.ExecutionRunDetail";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.Template
	*/ 
	createContent : function(oController) {
 		return new sap.m.Page({
			title: "ExecutionRunDetail",
			showNavButton: true,
			navButtonPress: function (oEvent) {
			    oController.onNavBack(oEvent);
			},
			content: [
			    new sap.ui.table.TreeTable(this.createId("table"), {
			    	columns: [
	                    new sap.ui.table.Column({
	                        label: "Name",
	                        template: 
	                        	new sap.m.Text({
	                        	text: "{className}"
	                        }),
	                        width: "40%"
	                    }),
	                    new sap.ui.table.Column({
	                        label: "Result",
	                        template: "result",
	                        width: "10%"
	                    }),
	                    new sap.ui.table.Column({
	                        label: "StartTime",
	                        template: "startTime",
	                        width: "15%"
	                    }),
	                    new sap.ui.table.Column({
	                        label: "EndTime",
	                        template: "endTime",
	                        width: "15%"
	                    }),
	                    new sap.ui.table.Column({
	                        label: "Duration",
	                        template: "durationStr",
	                        width: "20%"
	                    })
	                ],
	                selectionMode: sap.ui.table.SelectionMode.None,
	                enableColumnReordering: false,
	                expandFirstLevel: true,
//	                visibleRowCount: 20,
	                visibleRowCountMode: sap.ui.table.VisibleRowCountMode.Auto,
	                toggleOpenState: function(oEvent) {

	                }
			    }).addStyleClass("sapUiSizeCompact")
			],
			footer:new sap.m.Bar({
            	contentRight:[
					
				]
			})
			
		});
	}

});