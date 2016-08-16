/**
 * 
 */

Utility = new Object();
Utility.createFormContainerComboBox = function(textValue, comboBoxSetting, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
	formElements: [
		new sap.ui.layout.form.FormElement({
			label: new sap.m.Label({text: textValue}),
			fields: [
				new sme.perf.ui.controller.ComboBoxEx(comboBoxSetting)
			]
		})],
		layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
	});
};
Utility.createFormContainerInput= function(textValue, dataBinding, lineNumber, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
	formElements: [
		new sap.ui.layout.form.FormElement({
			label: new sap.m.Label({text: textValue}),
			fields: [
			    lineNumber == undefined ? new sap.m.Input({value: dataBinding}): new sap.m.TextArea({value: dataBinding, rows:lineNumber })
			]
		})],
		layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
	});
};
Utility.createFormContainerDatePicker = function(textValue, dataBindingPath, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
	    formElements: [
			new sap.ui.layout.form.FormElement({
				label: new sap.m.Label({text: textValue}),
				fields: [
				    new sap.m.DatePicker({ 
                	displayFormat: "yyyy-MM-dd",
                	valueFormat: "yyyy-MM-dd",
                	value: {
                    	path: dataBindingPath,
                    	type: new sap.ui.model.type.Date({source: {pattern: "yyyy-MM-dd HH:mm:ss"}, pattern: "yyyy-MM-dd"})
                    }})]
			})],
		layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
	});
};
Utility.createFormContainerText = function(textValue, dataBinding, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
		formElements: [
			new sap.ui.layout.form.FormElement({
				label: new sap.m.Label({text: textValue}),
				fields: [
				    new sap.m.Text({value: dataBinding})
				]
			})],
			layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
		});
};
Utility.createFormContainerSelect = function(textValue, selectControl, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
	    formElements: [
			new sap.ui.layout.form.FormElement({
				label: new sap.m.Label({text: textValue}),
				fields: [selectControl]
			})],
		layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
	});
};
Utility.createFormContainerWithControl = function(textValue, oControl, isHalfGrid){
	return new sap.ui.layout.form.FormContainer({
	    formElements: [
			new sap.ui.layout.form.FormElement({
				label: new sap.m.Label({text: textValue}),
				fields: [oControl]
			})],
		layoutData: new sap.ui.layout.form.GridContainerData({halfGrid: isHalfGrid == undefined ? false : isHalfGrid})
	});
};
Utility.createFormContainerTitle= function(title){
	return new sap.ui.layout.form.FormContainer({
		title: title
	});
};

Utility.createSearchField = function(refreshCallback, searchCallback){
	var oSearchField = new sap.m.MultiInput({
        placeholder: "Search...",
        showValueHelp: true,
        valueHelpRequest: function () {
        	refreshCallback();
        },
        tokenChange: function (oEvent) {
            if (oEvent.getParameter("type") == "tokensChanged") {
                var values = [];
                var tokens = this.getTokens();
                for (var i = 0; i < tokens.length; i++) {
                    values.push(tokens[i].getKey());
                }
                if (values.length == 0) {
                	refreshCallback(true);
                }
                else {
                	searchCallback(values);
                }
            }
        }
    });

    oSearchField._getValueHelpIcon = function () {
        var c = sap.ui.core.IconPool;
        var t = this;
        if (!this._oValueHelpIcon) {
            var u = c.getIconURI("synchronize");
            this._oValueHelpIcon = c.createControlByURI({
                id: this.getId() + "__vhi",
                src: u
            });
            this._oValueHelpIcon.addStyleClass("sapMInputValHelpInner");
            this._oValueHelpIcon.attachPress(function (e) {
                if (!t.getValueHelpOnly()) {
                    t.fireValueHelpRequest({
                        fromSuggestions: false
                    })
                }
            })
        }
        return this._oValueHelpIcon
    };

    oSearchField.addValidator(function (args) {
        return args.asyncCallback(new sap.m.Token({ key: args.text, text: args.text }));
    });
    
    return oSearchField;
}
