
var announcementTable = (function(page) {

    function renderDetailsButton(data, type, full) {
        return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.edit + "'>" +
            "<button type='button' class='btn btn-default btn-xs details-btn'><span class='fa fa-pencil'></span></div>";
    };

    function detailsCallback(row, data) {
        var row = $(row);
        row.find(".details-btn").click(function () {
            window.location.href = page.urls.link + data.identifier + page.urls.details;
        });
    };

    return {
        renderDetailsButton : renderDetailsButton,
        detailsCallback : detailsCallback
    };
})(window.PAGE);