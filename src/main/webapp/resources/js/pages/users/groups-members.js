var groupMembersTable = (function(page, notifications) {
	function userNameLinkRow(data, type, full) {
		return "<a class='item-link' title='" + data + "' href='"
				+ page.urls.usersLink + full.subject.identifier + "'><span>" + data
				+ "</span></a>";
	};
	
	function renderGroupRole(data, type, full) {
		return page.i18n[data];
	};
	
	function removeUserButton(data, type, full) {
		return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.remove + "'><button type='button' data-toggle='modal' data-target='#removeUserModal' class='btn btn-default btn-xs remove-user-btn'><span class='fa fa-remove'></span></div>";
	};
	
	function deleteLinkCallback(row, data) {
		var row = $(row);
		row.find(".remove-user-btn").click(function () {
			$("#removeUserModal").load(page.urls.deleteModal+"#removeUserModalGen", { 'userId' : data.subject.identifier}, function() {
				var modal = $(this);
				modal.on("show.bs.modal", function () {
					$(this).find("#remove-user-button").off("click").click(function () {
						$.ajax({
							url     : page.urls.removeMember + data.subject.identifier,
							type    : 'DELETE',
							success : function (result) {
								oTable_groupMembersTable.ajax.reload();
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
				modal.modal('show');
			});
		});
		row.find('[data-toggle="tooltip"]').tooltip();
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
				$("#addUserModal").modal('hide');
				oTable_groupMembersTable.ajax.reload();
				notifications.show({
					'msg': result.result
				});
				$("#add-user-username").select2("val", "");
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
		removeUserButton : removeUserButton,
		deleteLinkCallback : deleteLinkCallback
	};
})(window.PAGE, window.notifications);