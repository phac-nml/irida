var groupMembersTable = (function(page) {
	function userNameLinkRow(data, type, full) {
		return "<a class='item-link' title='" + data + "' href='"
				+ page.urls.usersLink + full.subject.identifier + "'><span>" + data
				+ "</span></a>";
	};
	
	function renderGroupRole(data, type, full) {
		return page.i18n[data];
	};
	
	function removeUserButton(data, type, full) {
		return "<div class='btn-group pull-right'><button type='button' data-toggle='modal' data-target='#deleteConfirmModal' class='btn btn-default btn-xs' group-member-id='"
				+ full.identifier + "'><span class='fa fa-trash'></span></div>";
	};
	
	$("#add-user-username").select2({
	    minimumInputLength: 2,
	    ajax: {
	        url: page.urls.usersSelection,
	        dataType: 'json',
	        data: function(term) {
	            return {
	                term: term,
	                page_limit: 10
	            };
	        },
	        results: function(data, params) {
	            return {results: data.map(function(el) {
	        		return {"id": el["identifier"], "text": el["label"]};
	        	})};
	        }
	    }
	});
	
	$("#submitAddMember").click(function() {
		$.ajax({
			url: page.urls.addMember,
			method: 'POST',
			data: {
				"userId" : $("#add-user-username").val(),
				"groupRole" : $("#add-user-role").val()
			},
			success: function(result) {
				console.log(result);
				$("#addUserModal").modal('hide');
				oTable_groupMembersTable.ajax.reload();
				notifications.show({
					'msg': result.result
				});
			},
			error: function() {
				$("#addUserModal").modal('hide');
				notifications.show({
					'msg': page.i18n.unexpectedAddError,
					'type': 'error'
				})
			}
		})
	});
	
	return {
		userNameLinkRow : userNameLinkRow,
		renderGroupRole : renderGroupRole,
		removeUserButton : removeUserButton
	};
})(window.PAGE);