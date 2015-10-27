oTable_projectsTable.on('search.dt', function (e, settings) {
  console.log(settings);
  settings.ajax.data = {
    name: $("#nameFilter").val()
  };
});