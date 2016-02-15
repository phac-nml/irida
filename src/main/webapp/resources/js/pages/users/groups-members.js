(function ($, page) {
	function timestampRender(data) {
		return '<span data-livestamp="' + ( data / 1000 ) + '"></span>';
	};
	
    $(function () {
        $('#groupMembersTable').DataTable({
            dom: "<'top'lf>rt<'bottom'ip><'clear'>",
            processing: true,
            serverSide: true,
            deferRender: true,
            ajax: page.urls.table,
            stateSave: true,
            stateDuration: -1,
            order: [[1, "desc"]],
            columns: [
                {"data": "subject.username"},
                {"data": "role"},
                {"data": "createdDate"}
            ],
            columnDefs: [
                {
                    'render': function (data, type, row) {
                        return '<a href="' + page.urls.usersLink
                            + row['subject']['identifier'] + '">' + data + '</a>';
                    },
                    'targets': 0
                },
                {
                    'render': timestampRender,
                    'targets': 2
                }
            ]
        });
    });
})(window.jQuery, window.PAGE);