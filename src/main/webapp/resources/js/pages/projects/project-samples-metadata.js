var tableCreator = (function() {
  function transformHeaders(headers) {
    return headers.map(function(h) {
      return {title: h, data: h};
    });
  }
  return function createTable(data) {
    var nameCol = data.headers.indexOf('NLEP #');
    var headers = transformHeaders(data.headers);
    $('#metadata-table').DataTable({
      scrollX: true,
      data: data.metadata,
      columns: headers,
      createdRow: function(row, data) {
        console.log(data)
        if (data.sampleId === '') {
          $(row).addClass('bg-danger');
        }
      },
      columnDefs: [
        {
          targets: nameCol,
          render: function(data, type, row) {
            var id = row.sampleId;
            if (id !== '') {
              return '<a href="/samples/' + id + '">' + data + '</a>';
            } else {
              return data;
            }
          }
        }
      ]
    });
  };
}());

Dropzone.options.metadataDropzone = {
  paramName: 'file',
  acceptedFiles: '.xlx,.xlsx',
  init: function() {
    var data;
    this.on('success', function(file, result) {
      data = result;
    });
    this.on('complete', function() {
      $('#metadataDropzone').fadeOut(200, function() {
        $(this).remove();
        tableCreator(data);
      });
    });
  }
};