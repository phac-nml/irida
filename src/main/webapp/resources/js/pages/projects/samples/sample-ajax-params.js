function samplesAjaxParams() {
  return {
    data: {
      name: function () {
        return PAGE.ajaxParam.name
      },
      minDate: function () {
        return PAGE.ajaxParam.startDate;
      },
      endDate: function () {
        return PAGE.ajaxParam.endDate;
      }
    }
  }
}