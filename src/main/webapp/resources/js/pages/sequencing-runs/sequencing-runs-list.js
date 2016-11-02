var runsTable = (function(page) {
	function runLinkRow(data, type, full) {
		return '<a class="item-link run-id" title="' + data + '" href="' + page.urls.link
				+ full.id + '"><span>' + data + '</span></a>';		
	};

	function runUserRow(data, type, full) {
		if(data != null){
			return '<a class="item-link" title="' + data.label + '" href="' + page.urls.users + data.identifier + '"><span>' + data.label + '</span></a>';
		}
		else{
			return "";
		}	
	};
	
	return {
		runLinkRow : runLinkRow,
		runUserRow : runUserRow

	};
})(window.PAGE);
