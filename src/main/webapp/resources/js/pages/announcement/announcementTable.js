
var announcementTable = (function(page) {

    /**
     * Renders button for each row in Announcement table that redirects to the details page for that announcement on click
     * @param data
     * @param type
     * @param full
     * @returns {string}
     */
    function renderDetailsButton(data, type, full) {
        return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.edit + "'>" +
            "<button type='button' class='btn btn-default btn-xs details-btn'><span class='fa fa-pencil'></span></div>";
    }

    /**
     * Renders date that the announcement was read
     * @param data
     * @param type
     * @param full
     * @returns {string}
     */
    function renderDate(data, type, full) {
        var date = "";
        if (full.join != null) {
            date = " " + datatable.formatDate(full.createdDate);
        }
        return "<div><span>" + date + "</span></div>";
    }

    /**
     * Render read status for user
     * @param data
     * @param type
     * @param full
     * @returns {string}
     */
    function renderStatus(data, type, full) {
        var icon;
        if (full.join != null) {
            icon = "fa fa-check";
        } else {
            icon = "fa fa-times";
        }
        return "<div><span class='" + icon + "'></span></div>";
    }

    /**
     * Initializes extras for each row of the data table
     * @param row
     * @param data
     */
    function detailsCallback(row, data) {
        var row = $(row);
        row.find(".details-btn").click(function () {
            window.location.href = page.urls.link + data.identifier + "/details";
        });
        row.find('[data-toggle="tooltip"]').tooltip();
    }

    return {
        renderDetailsButton : renderDetailsButton,
        renderDate : renderDate,
        renderStatus : renderStatus,
        detailsCallback : detailsCallback
    };
})(window.PAGE);