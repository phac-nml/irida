var fields = filterForm.elements;
oTable_projectsTable.on('search.dt', function (e, settings) {
  settings.aoServerParams.push({"sName": "user",
    "fn": function (aoData) {
      aoData.columns[1].search.value = fields.nameFilter.value
    }})
});