var fields = filterForm.elements;
oTable_projectsTable.on('search.dt', function (e, settings) {
  settings.aoServerParams.push({"sName": "user",
    "fn": function (aoData) {
      aoData.columns[1].search.value = fields.nameFilter.value;
      aoData.columns[2].search.value = fields.organismFilter.value;
    }})
});

$("#clearFilterBtn").on('click', function () {
  fields.nameFilter.value = "";
  fields.organismFilter.value = "";
  $("#projectsTable_filter").find("input[type=search]").val("");
  oTable_projectsTable.ajax.reload();
});