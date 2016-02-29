var groupsTable = (function(page) {
	function groupLinkRow(data, type, full) {
		return '<a class="item-link" title="' + data + '" href="' + page.urls.link
				+ '/' + full.group.identifier + '"><span>' + data + '</span></a>';		
	};
	
	function descriptionRow(data, type, full) {
		return '<p class="crop">' + full.group.description + '</p>';
	};
	
	function removeGroupButton(data, type, full) {
		if (full.groupOwner || full.admin) {
			return "<div class='btn-group pull-right'><button type='button' data-toggle='modal' data-target='#removeGroupModal' class='btn btn-default btn-xs remove-group-btn'><span class='fa fa-remove' uib-tooltip='" + page.i18n.remove + "'></span></div>";
		} else {
			return "";
		}
	};
	
	function deleteLinkCallback(row, data) {
		$(row).find(".remove-group-btn").click(function () {
			$("#removeGroupModal").on("show.bs.modal", function () {
				var modal = $(this);
				modal.find("#remove-group-button").off("click").click(function () {
					$.ajax({
						url     : page.urls.deleteGroup + data.group.identifier,
						type    : 'DELETE',
						success : function (result) {
							oTable_groupsTable.ajax.reload();
							notifications.show({
								'msg': result.result
							});
							modal.modal('hide');
						}, error: function () {
							notifications.show({
								'msg' : page.i18n.unexpectedRemoveError,
								'type': 'error'
							});
							modal.modal('hide');
						}
					});
				});
			});
		});
	};

	return {
		groupLinkRow : groupLinkRow,
		removeGroupButton : removeGroupButton,
		deleteLinkCallback : deleteLinkCallback,
		descriptionRow : descriptionRow
	};
})(window.PAGE);
