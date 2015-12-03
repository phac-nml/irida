/*global oTable_analysisTable, filterForm */

var filterFormHandler = (function ($, _) {

  var fields = filterForm.elements,
      labels = {
        name: $('#nameFilterPreview'),
        analysisState: $('#oanalysisStateFilterPreview')
      };

  function getFieldValues() {
    var vals = Object.create(null);
    vals.name = fields.nameFilter.value;
    vals.analysisState = fields.analysisStateFilter.value;
    return vals;
  }

  function updateFilterLabels() {
    _.forEach(getFieldValues(), updateField);
  }

  function clearFields () {
    filterForm.reset();
    oTable_analysisTable.search("");
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
        oTable_analysisTable.ajax.reload();
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

oTable_analysisTable.on('search.dt', function (e, settings) {
  settings.aoServerParams.push({
    "sName": "user",
    "fn"   : function (aoData) {
      var vals = filterFormHandler.getFieldValues();
      aoData.columns[1].search.value = vals.name;
      aoData.columns[0].search.value = vals.analysisState;
    }
  });
  filterFormHandler.updateFilterLabels();
});

$("#clearFilterBtn").on('click', function () {
  filterFormHandler.clearFields();
  oTable_analysisTable.ajax.reload();
});

(function ($) {
  var $filterBtn = $('#filterAnalysesBtn');

  $filterBtn.on('click', function () {
    oTable_analysisTable.ajax.reload();
  });

  $('#filterForm').on('keydown', function (e) {
    if(e.which === 13) {
      $filterBtn.click();
    }
  });
})(window.jQuery);
