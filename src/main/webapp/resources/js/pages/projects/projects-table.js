oTable_projectsTable.on('search.dt', function (e, settings) {
  settings.ajax.data = {
    name: $("#nameFilter").val()
  };
});