(function ($, page) {
    $(function () {
        $('#usersTable').DataTable({
            dom: "<'top'lf>rt<'bottom'ip><'clear'>",
            processing: true,
            serverSide: true,
            deferRender: true,
            ajax: page.urls.table,
            stateSave: true,
            stateDuration: -1,
            order: [[1, "desc"]],
            columnDefs: [
                {
                    'render': function (data, type, row) {
                        return '<a href="' + page.urls.link
                            + row[0] + '">' + data + '</a>';
                    },
                    'targets': 1
                },
                {
                    'render': function (data, type, row) {
                        return '<span data-livestamp="' + data + '"></span>';
                    },
                    'targets': 7
                }
            ]
        });
    });
})(window.jQuery, window.PAGE);