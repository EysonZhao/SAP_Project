sap.ui.jsfragment("sme.perf.ui.fragment.TestRequestSelectDialog",{
	createContent: function(oController){
		
		var oSelectDialog = new sap.m.SelectDialog({
			title: 'Test Request',
			rememberSelections: true,
			items: {
				path: '/testRequests',
				template: new sap.m.StandardListItem({
					title: '{name}',
					description: '{projectName}',
					type:'Active'
				})
			},
			confirm: function(oEvent){
				oController.onTestRequestSelectConfirm(oEvent);
			},
			search: function(oEvent){
				oController.onTestRequestSelectSearch(oEvent);
			}
		});
		oSelectDialog.setModel(oController._mTestRequestListModel);
		return oSelectDialog;
	}
});