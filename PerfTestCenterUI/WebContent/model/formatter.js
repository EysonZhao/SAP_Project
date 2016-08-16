sap.ui.define([], function () {
	"use strict";
	return {
		testRequestStatusText: function (sStatus) {
			if(sStatus){
				return sStatus;
			}
			else{
				return "New";
			}
		},
		hostListText: function(hostList){
			if(hostList && hostList.length > 0){
				var str = "";
				for(var i=0 ; i<hostList.length ; i++){
					str += hostList[i].ip + ";"
				}
				return str;
			}
			else {
				return "";
			}
		},
		formatDateTimeStringToDateString: function(dateTime){
			if(dateTime){
				return dateTime.slice(0,10);
			}
		},
		formatDateObjectToDateTimeString: function(dateObj){
			var year = dateObj.getFullYear();
			var month = dateObj.getMonth() + 1;
			var date = dateObj.getDate();
			var hour = dateObj.getHours();
			var min = dateObj.getMinutes();
			var sec = dateObj.getSeconds();
			var str = year + '-';
			str += month >= 10 ? month : "0" + month;
			str += "-";
			str += date >= 10 ? date : "0" + date;
			str += ' ';
			str += hour >= 10? hour: "0" + hour;
			str += ":";
			str += min >= 10? min : "0" + min;
			str += ":";
			str += sec >= 10 ? sec : "0" + sec;
			return str;
		}
	};
});
