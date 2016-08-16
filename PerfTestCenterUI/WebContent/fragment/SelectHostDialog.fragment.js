sap.ui.jsfragment("sme.perf.ui.fragment.SelectHostDialog",{
	createContent: function(oController){
		
		var oSelectDialog = new sap.m.SelectDialog({
			title: 'Select Host',
			rememberSelections: false,
			multiSelect: true,
			items: {
				path: '/hostList',
				template: new sap.m.StandardListItem({
					title: '{ip}  {operationSystem}',
					description: '{description}',
					type:'Active'
				})
			},
			confirm: function(oEvent){
				oController.onHostSelectConfirm(oEvent);
			},
			search: function(oEvent){
				oController.onHostSelectSearch(oEvent);
			}
		});
		oSelectDialog.setModel(oController._mHostListModel);
		return oSelectDialog;
	}
});