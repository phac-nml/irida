/*globals getApiStatus*/
(function ($, page) {
    $(document).ready(function () {
        $('#remoteapiTable').DataTable({
            dom: "<'row filter-row'<'col-sm-6'l><'col-sm-6'0f>><'row datatables-active-filters'1><'panel panel-default''<'row'<'col-sm-12'tr>>><'row'<'col-sm-5'i><'col-sm-7'p>>",
            processing: true,
            serverSide: true,
            deferRender: true,
            ajax: page.urls.table,
            stateSave: true,
            stateDuration: -1,
            order: [[0, "desc"]],
            columns: [
                {
                    "data": "name",
                    "render": function (data, type, row) {
                        return '<a class="api-name" href="' + page.urls.linkBase + row.id + '">' + data + '</a>';
                    }
                },
                {
                    "data": "createdDate"
                },
                {
                    "sortable": false,
                    "render": function (data, type, row) {
                        var span = "<div data-api-id='" + row.id + "' class='connection-status' id=" + row.id + ">" + page.lang.statusText + "</div>";
                        return span;
                    }
                },
                {
                    "sortable": false,
                    render: function (data, type, row) {
                        return "<button id='connect-button-" + row.id + "' class='oauth-connect-link btn btn-default btn-xs hidden' data-api-id='" + row.id + "'>" + page.lang.connectText + "</a>";
                    }
                }

            ],
            drawCallback: function () {
                $(".connection-status").each(function () {
                    var resultDiv = "#" + $(this).attr("id");
                    var apiId = $(this).data("api-id");
                    var buttonId = "#connect-button-" + apiId;

                    getApiStatus(apiId, resultDiv, buttonId, null);
                });

            }

        });
    });
})(window.jQuery, window.PAGE);