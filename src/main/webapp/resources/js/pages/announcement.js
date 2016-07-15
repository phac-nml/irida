
var announcementTable = (function(page) {


    function detailsButton(data, type, full) {
        if (full.admin) {
            return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.remove + "'><button type='button' class='btn btn-default btn-xs'><span class='fa fa-pencil'></span></div>";
        } else {
            return "";
        }
    }

})