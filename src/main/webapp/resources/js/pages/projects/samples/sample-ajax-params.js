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
      minDate    : function () {
        var minDate = PAGE.ajaxParam.minDate;
        return typeof minDate === "number" ? minDate : "";
      },
      endDate    : function () {
        var endDate = PAGE.ajaxParam.endDate;
        return typeof endDate === "number" ? endDate : "";
      }
    }
  };
}