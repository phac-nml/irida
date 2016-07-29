
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
    function renderDateAndStatus(data, type, full) {
        var iconClass;
        var date;
        if (full.join != null) {
            iconClass = "fa fa-check";
            date = " " + datatable.formatDate(full.createdDate);
        } else {
            iconClass = "fa fa-times";
            date = "";
        }

        return "<div><span class='" + iconClass + "'></span><span>" + date + "</span></div>";
    }

    function renderToggleStatusButtons(data, type, full) {
        return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.markRead + "'>" +
            "<button type='button' class='btn btn-default btn-xs toggle-read'><span class='fa fa-check'></span></div>" +
            "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.markUnread + "'>" +
            "<button type='button' class='btn btn-default btn-xs toggle-unread'><span class='fa fa-times'></span></div>";
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
        renderDateAndStatus : renderDateAndStatus,
        renderToggleStatusButtons : renderToggleStatusButtons,
        detailsCallback : detailsCallback
    };
})(window.PAGE);