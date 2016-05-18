var runsTable = (function(page) {
	function runLinkRow(data, type, full) {
		return '<a class="item-link run-id" title="' + data + '" href="' + page.urls.link
				+ full.id + '"><span>' + data + '</span></a>';		
	};
	
	return {
		runLinkRow : runLinkRow

	};
})(window.PAGE);
