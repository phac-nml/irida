var projectMembersTable = (function(page, notifications) {
	function renderGroupRole(data, type, full) {
		var select ='<select id="' + full.object.identifier + '-role-select" class="form-control input-full project-role-select">';
		select += '<option value="PROJECT_USER" ' + (data == 'PROJECT_USER' ? 'selected="selected"' : '') + '>' + page.i18n.PROJECT_USER +  '</option>';
		select += '<option value="PROJECT_OWNER" ' + (data == 'PROJECT_OWNER' ? 'selected="selected"' : '') + '>' + page.i18n.PROJECT_OWNER +  '</option>';
		select += '</select>';
		return select;
	};
	
	function userNameLinkRow(data, type, full) {
		return "<a class='item-link' title='" + data + "' href='"
				+ page.urls.usersLink + full.object.identifier + "'><span class='col-names'>" + full.object.label
				+ "</span></a>";
	};
	
	function removeUserButton(data, type, full) {
		return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.remove + "'><button id='remove-user-" + full.object.identifier + "' type='button' data-toggle='modal' data-target='#removeUserModal' class='btn btn-default btn-xs remove-user-btn'><span class='fa fa-remove'></span></div>";
	};
	
	function rowRenderedCallback(row, data) {
		var row = $(row);
		row.find(".remove-user-btn").click(function () {
			$("#removeUserModal").load(page.urls.deleteModal+"#removeUserModalGen", { 'userId' : data.object.identifier}, function() {
				var modal = $(this);
				modal.on("show.bs.modal", function () {
					$(this).find("#remove-user-button").off("click").click(function () {
						$.ajax({
							url     : page.urls.removeMember + data.object.identifier,
							type    : 'DELETE',
							success : function (result) {
								if (result.success) {
									oTable_usersTable.ajax.reload();
									notifications.show({
										'msg': result.success
									});
								} else if (result.failure) {
									notifications.show({
										'msg' : result.failure,
										'type': 'error'
									});									
								}
								modal.modal('hide');
							}
						});
					});
				});
				modal.modal('show');
			});
		});
		row.find('[data-toggle="tooltip"]').tooltip();
		row.find('.project-role-select').change(function() {
			$.ajax({
				url: page.urls.updateRole + data.object.identifier,
				type: 'POST',
				data: {
					'projectRole': $(this).val()
				},
				success : function(result) {
					if (result.success) {
						notifications.show({'msg': result.success});
					} else if (result.failure) {
						notifications.show({
							'msg' : result.failure,
							'type': 'error'
						})
					}
				}
			});
		});
	};
	
	
	$("#add-user-username").select2({
	    minimumInputLength: 1,
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
				"projectRole" : $("#add-user-role").val()
			},
			success: function(result) {
				$("#addUserModal").modal('hide');
				oTable_usersTable.ajax.reload();
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
		renderGroupRole : renderGroupRole,
		userNameLinkRow : userNameLinkRow,
		removeUserButton : removeUserButton,
		rowRenderedCallback : rowRenderedCallback
	};
})(window.PAGE, window.notifications);
