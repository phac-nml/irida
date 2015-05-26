function getProjects(sSource, aoData, fnCallback, oSettings) {
  console.log("getPRojects Called");
  oSettings.jqXHR = $.ajax({
    'dataType': 'json',
    'type': 'GET',
    'url': sSource,
    'data': aoData,
    'success': function (json) {
      console.log(json);
      fnCallback(json);
    }
  });
}
