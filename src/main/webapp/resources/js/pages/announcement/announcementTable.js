
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
    };

    /**
     * Renders status icon for a user and the current announcement, in the Announcement Details table
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

    function renderStatus(data, type, full) {
        var icon;
        if (full.join != null) {
            icon = "fa fa-check";
        } else {
            icon = "fa fa-times";
        }
        return "<div><span class='" + icon + "'></span></div>";
    }

    function renderToggleStatusSwitch(data, type, full) {
        return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.markRead + "'>" +
            "<button type='button' class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='fa fa-check'></span></div>";
    }

    function detailsCallback(row, data) {
        var row = $(row);
        row.find(".details-btn").click(function () {
            window.location.href = page.urls.link + data.identifier + page.urls.details;
        });
        row.find('[data-toggle="tooltip"]').tooltip();
    };

    return {
        renderDetailsButton : renderDetailsButton,
        renderDate : renderDate,
        renderStatus : renderStatus,
        renderToggleStatusSwitch : renderToggleStatusSwitch,
        detailsCallback : detailsCallback
    };
})(window.PAGE);