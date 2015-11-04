/*global oTable_projectsTable, filterForm */

var filterFormHandler = (function ($, _) {
  var fields = filterForm.elements,
      labels = {
        name: $('#nameFilterPreview'),
        organism: $('#organismFilterPreview')
      };

  function getFieldValues() {
    var vals = Object.create(null);
    vals.name = fields.nameFilter.value;
    vals.organism = fields.organismFilter.value;
    return vals;
  }

  function updateFilterLabels() {
    _.forEach(getFieldValues(), updateField);
  }

  function clearFields () {
    filterForm.reset();
    oTable_projectsTable.search("");
  }

  function updateField(value, name) {
    if (!labels[name]) {return;}
    var parent = labels[name].parent();
    if(_isValid(value)) {
      labels[name].html(value);
      parent.removeClass('hidden').bind('click', function (){
        fields[name + "Filter"].value = "";
        $(this).unbind('click');
        parent.addClass('hidden');
        oTable_projectsTable.ajax.reload();
      });
    }
    else {
      if(!parent.hasClass('hidden')){
        parent.addClass('hidden');
      }
    }
  }

  function _isValid(fieldValue) {
    return (fieldValue !== undefined && fieldValue.length > 0);
  }

  return {
    getFieldValues: getFieldValues,
    updateFilterLabels: updateFilterLabels,
    clearFields: clearFields
  };
})(window.jQuery, window._);

oTable_projectsTable.on('search.dt', function (e, settings) {
  settings.aoServerParams.push({
    "sName": "user",
    "fn"   : function (aoData) {
      var vals = filterFormHandler.getFieldValues();
      aoData.columns[1].search.value = vals.name;
      aoData.columns[2].search.value = vals.organism;
    }
  });
  filterFormHandler.updateFilterLabels();
});

$("#clearFilterBtn").on('click', function () {
  filterFormHandler.clearFields();
  oTable_projectsTable.ajax.reload();
});