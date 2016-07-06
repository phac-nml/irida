function samplesAjaxParams() {
  return {
    data: {
      name: function () {
        var name = PAGE.ajaxParam.name;
        return typeof name === "string" ? name : "";
      },
      minDate: function () {
        var minDate = PAGE.ajaxParam.minDate;
        return typeof minDate === "number" ? minDate : "";
      },
      endDate: function () {
        var endDate = PAGE.ajaxParam.endDate;
        return typeof endDate === "number" ? endDate : "";
      }
    }
  }
}