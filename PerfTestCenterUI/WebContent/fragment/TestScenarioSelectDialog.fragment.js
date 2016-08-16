sap.ui.jsfragment("sme.perf.ui.fragment.TestScenarioSelectDialog",{
	createContent: function(oController){
		
		var oSelectDialog = new sap.m.SelectDialog({
			title: 'Test Scenario',
			rememberSelections: true,
			items: {
				path: '/testScenarios',
				template: new sap.m.StandardListItem({
					title: '{projectName}  {name}',
					description: '{description}',
					type:'Active'
				})
			},
			confirm: function(oEvent){
				oController.onTestScenarioSelectConfirm(oEvent);
			},
			search: function(oEvent){
				oController.onTestScenarioSelectSearch(oEvent);
			}
		});
		oSelectDialog.setModel(oController._mTestScenarioListModel);
		return oSelectDialog;
	}
});