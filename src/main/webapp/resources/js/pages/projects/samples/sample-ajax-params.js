function samplesAjaxParams() {
  return {
    data: {
      associated: function () {
        var associated = PAGE.ajaxParam.associated;
        return Array.isArray(associated) ? associated : [];
      },
      sampleNames: function () {
        var names = PAGE.ajaxParam.sampleNames;
        return Array.isArray(names) ? names : [];
      },
      name       : function () {
        var name = PAGE.ajaxParam.name;
        return typeof name === "string" ? name : "";
      },
      organism: function() {
        var organism = PAGE.ajaxParam.organism;
        return typeof organism === "string" ? organism : "";
      },
      minDate    : function () {
        var minDate = PAGE.ajaxParam.date.min;
        return typeof minDate === "number" ? minDate : "";
      },
      endDate    : function () {
        var endDate = PAGE.ajaxParam.date.end;
        return typeof endDate === "number" ? endDate : "";
      }
    }
  };
}