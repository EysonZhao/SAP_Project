sap.ui.define([
   "sap/ui/core/UIComponent",
   "sap/ui/model/json/JSONModel"
], function (UIComponent, JSONModel) {
   "use strict";
   return UIComponent.extend("sme.perf.ui.Component", {
      metadata : {
    	  manifest: "json"
      },
      
      init : function () {
          var globalConfig = {};
          globalConfig.serviceBaseUrl = this.getMetadata().getConfig().serviceBaseUrl;
          
          var globalConfigModel = new sap.ui.model.json.JSONModel(globalConfig);
          this.setModel(globalConfigModel, "globalConfigModel");
          
         // call the init function of the parent
         UIComponent.prototype.init.apply(this, arguments);
         this._router = this.getRouter();
         this._router.initialize();

         if (location.href.indexOf('#/') == -1 || location.href.endsWith('#/home')) {
             this._router.navTo("testrequestlist");
         }
      },
      
      createContent: function(){
          var oView = sap.ui.view({
              id: "perfTestCenterApp",
              viewName: "sme.perf.ui.view.App",
              type: "JS",
              viewData: { component: this }
          });

          return oView;
      }
   });
});