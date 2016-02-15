(function ($, page) {
	function timestampRender(data) {
		return '<span data-livestamp="' + ( data / 1000 ) + '"></span>';
	};
	
    $(function () {
        $('#groupsTable').DataTable({
            dom: "<'top'lf>rt<'bottom'ip><'clear'>",
            processing: true,
            serverSide: true,
            deferRender: true,
            ajax: page.urls.table,
            stateSave: true,
            stateDuration: -1,
            order: [[1, "desc"]],
            columns: [
                {"data": "identifier"},
                {"data": "name"},
                {"data": "createdDate"},
                {"data": "modifiedDate"}
            ],
            columnDefs: [
                {
                    'render': function (data, type, row) {
                        return '<a href="' + page.urls.link
                            + row['identifier'] + '">' + row['identifier'] + '</a>';
                    },
                    'targets': 0
                },
                {
                    'render': timestampRender,
                    'targets': [ 2, 3 ]
                }
            ]
        });
    });
})(window.jQuery, window.PAGE);