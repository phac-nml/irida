var projectMembersTable = (function(page, notifications) {
	function renderGroupRole(data, type, full) {
    var select = '<select id="' + full.object.identifier + '-role-select" class="form-control input-full project-role-select">';
    select += '<option value="PROJECT_USER" ' + (data == 'PROJECT_USER' ? 'selected="selected"' : '') + '>' + page.i18n.PROJECT_USER + '</option>';
    select += '<option value="PROJECT_OWNER" ' + (data == 'PROJECT_OWNER' ? 'selected="selected"' : '') + '>' + page.i18n.PROJECT_OWNER + '</option>';
    select += '</select>';
    return select;
	};
	
	function renderGroupRoleAsText(data, type, full) {
		return page.i18n[data];
	};
	
	function memberNameLinkRow(data, type, full) {
		return "<a class='item-link' title='" + data + "' href='"
				+ page.urls.usersLink + full.object.identifier + "'><span class='col-names'>" + full.object.label
				+ "</span></a>";
	};
	
	function removeMemberButton(data, type, full) {
		return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.remove + "'><button id='remove-member-" + full.object.identifier + "' type='button' data-toggle='modal' data-target='#removeUserModal' class='btn btn-default btn-xs remove-member-btn'><span class='fa fa-remove'></span></div>";
	};
	
	function rowRenderedCallback(row, data) {
		var row = $(row);
		row.find(".remove-member-btn").click(function () {
			$("#removeMemberModal").load(page.urls.deleteModal+"#removeMemberModalGen", { 'memberId' : data.object.identifier }, function() {
				var modal = $(this);
				modal.on("show.bs.modal", function () {
					$(this).find("#remove-member-button").off("click").click(function () {
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
		var originalRole;
		row.find('.project-role-select').on('focus', function() {
			originalRole = this.value;
		}).change(function() {
			var select = $(this);
			$.ajax({
				url: page.urls.updateRole + data.object.identifier,
				type: 'POST',
				data: {
					'projectRole': select.val()
				},
				success : function(result) {
					if (result.success) {
						originalRole = select.val();
						notifications.show({'msg': result.success});
					} else if (result.failure) {
						select.val(originalRole);
						notifications.show({
							'msg' : result.failure,
							'type': 'error'
						})
					}
				}
			});
		});
	};
	
	
	$("#add-member-membername").select2({
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
				"memberId" : $("#add-member-membername").val(),
				"projectRole" : $("#add-member-role").val()
			},
			success: function(result) {
				$("#addMemberModal").modal('hide');
				oTable_usersTable.ajax.reload();
				notifications.show({
					'msg': result.result
				});
				$("#add-member-membername").select2("val", "");
			},
			error: function() {
				$("#addMemberModal").modal('hide');
				notifications.show({
					'msg': page.i18n.unexpectedAddError,
					'type': 'error'
				})
			}
		})
	});
	
	return {
		renderGroupRole : renderGroupRole,
		renderGroupRoleAsText : renderGroupRoleAsText,
		memberNameLinkRow : memberNameLinkRow,
		removeMemberButton : removeMemberButton,
		rowRenderedCallback : rowRenderedCallback
	};
})(window.PAGE, window.notifications);
