sap.ui.define([
	"sap/ui/core/Control",
	"sap/m/ComboBox"
], function (Control, ComboBox) {
	"use strict";
	return Control.extend("sme.perf.ui.controller.ComboBoxEx", {
		metadata : {
			properties : {
				value: 	{type : "long", defaultValue : 0},
				comboBoxName : {type: "string"},
				keyValuePath: {type: "string"},
				textValuePath:  {type: "string"},
				itemsValuePath: {type: "string"},
				itemsValueModel: {type: "string"},
				parentValueModel: {type: "string"},
				selectedField: {type: "string"}
			},
			aggregations : {
				_comboBox : {type : "sap.m.ComboBox", multiple: false, visibility : "hidden"}
			},
			events : {
				change : {
					parameters : {
						value : {type : "long"}
					}
				}
			}
		},
		
		init : function () {
		/*
			as the in the init function, cannot get the properties, so move the code into renderer function
			*/
		},
		
		renderer : function (oRM, oControl) {
			var that = this;
			if(oControl.getAggregation("_comboBox") == undefined 
					|| oControl.getAggregation("_comboBox") == null){
				var oItemTemplate = new sap.ui.core.ListItem({
					key: "{" + oControl.getKeyValuePath() + "}", 	//customer field keyValuePath ("{projectsModel>id}"), if the string include '{' and '}' the setting will be failed. workaround dirty solution
					text: "{" + oControl.getTextValuePath() + "}"	//customer field textValuePath ("{projectsModel>name}")
				});
				
				var oComboBox = new sap.m.ComboBox(oControl.getComboBoxName(), {
					items: {
						path: oControl.getItemsValuePath(),	//customer field itemsValuePath ("projectsModel>/projects")
						model: oControl.getItemsValueModel(),	//customer field itemsValueModel ("projectsModel")
						template: oItemTemplate
					},

					selectionChange: function(oControlEvent) {
						var oModel = this.getModel(oControl.getParentValueModel());	//customer field parentValueModel ("testRequestModel") 
						if(oModel){
							var selectedKey = this.getSelectedKey();
							if(selectedKey){
								oModel.oData[oControl.getSelectedField()] = selectedKey;	//customer field selectedField ("projectId")
							}
						}
					}
				});
				oControl.setAggregation("_comboBox", oComboBox);
				
				//bind the selected value
				oComboBox.onAfterRendering = function(){
					if (sap.m.ComboBox.prototype.onAfterRendering) {
					  sap.m.ComboBox.prototype.onAfterRendering.apply(this);
					}
					var oSelectedValueModel = this.getModel(oControl.getParentValueModel());	//customer field parentValueModel ("testRequestModel") 
					if(oSelectedValueModel){
						var selectedValue = oSelectedValueModel.oData[oControl.getSelectedField()];	//customer field selectedField ("projectId")
						if(selectedValue){
							this.setSelectedKey(selectedValue);
						}
						else{
							this.setSelectedKey("");
						}
					}
				};
			}
		
			oRM.write("<div");
			oRM.writeControlData(oControl);
			oRM.write(">");
			oRM.renderControl(oControl.getAggregation("_comboBox"));
			oRM.write("</div>");
		}
	});
});