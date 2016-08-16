sap.ui.define([
	"sme/perf/ui/controller/BaseController"
], function (BaseController) {
	"use strict";
	return BaseController.extend("sme.perf.ui.controller.Home", {
		isPinned: true,
		onInit: function () {
			
		},
		handleHomeListItemPress: function(oEvent){
			var customDataArray = oEvent.getSource().getCustomData();
			for(var i=0 ; i<customDataArray.length ; i++){
				var customData = customDataArray[i];
				if(customData.getKey() == 'route'){
					this.getRouter().navTo(customData.getValue());
					break;
				}
			}
		},
		pinOrUnpin:function(){
            var button = sap.ui.getCore().byId('button_pin');
            var myApp = sap.ui.getCore().byId('mySplitApp');
            if (this.isPinned) {
                this.isPinned = false;
                myApp.hideMaster();
                button.setIcon(sap.ui.core.IconPool.getIconURI("pushpin-on"));
                button.setTooltip('Pin Master');
                myApp.setMode(sap.m.SplitAppMode.HideMode);
            }
            else {
            	this.isPinned = true;
                button.setIcon(sap.ui.core.IconPool.getIconURI("pushpin-off"));
                myApp.setMode(sap.m.SplitAppMode.ShowHideMode);
                button.setTooltip('Hide Master');
                myApp.rerender();
            }
		}
	});
});
