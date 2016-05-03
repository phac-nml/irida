(function ($, page) {
  $(function () {
    $('#clientsTable').DataTable({
      dom: "<'top'lf>rt<'bottom'ip><'clear'>",
      processing: true,
      serverSide: true,
      deferRender: true,
      ajax: page.urls.list,
      stateSave: true,
      stateDuration: -1,
      order: [[1, "desc"]],
      columnDefs: [
        {
          'className': "clientIdCol",
          'targets': 0
        },
        {
          'render': function (data, type, row) {
            return '<a class="clientLink" href="' +
                page.urls.clients
              + row[0] + '">' + data + '</a>';
          },
          'targets': 1
        },
        {
          'sortable': false,
          'targets': 4
        }
      ]
    });
  });
})(window.jQuery, window.PAGE);