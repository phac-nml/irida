var groupsTable = (function(page) {
	function groupLinkRow(data, type, full) {
		return '<a class="item-link" title="' + data + '" href="' + page.urls.link
				+ '/' + full.identifier + '"><span>' + data + '</span></a>';		
	}
	;

	return {
		groupLinkRow : groupLinkRow
	};
})(window.PAGE);
