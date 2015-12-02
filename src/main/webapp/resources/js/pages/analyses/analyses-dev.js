/*
 * Used by the Investigations Listing Datatable (analyses.html)
 */
var analysesTable = (function(tl){
    /**
     * Fomat the content of the Analysis Name column to link to the analysis.
     * @param data - data for the name cell.
     * @param type - string - the type of data
     * @param full - The complete object represented in the row.
     * @returns {string} - The link to put into the table.
     */
    function createLinkButton(data, type, full) {
        if(full.id) {
            return "<a class='btn btn-link' href='" + tl.BASE_URL + "analysis/" + full.id + "'>" + data + "</a>";
        }
        return data;
    }

    function getHumanTime(data, type, full) {
        if(data) {
            return moment.duration(parseInt(data)).humanize();
      }
      return "";
    }

    function displayProgress(data, type, full) {
        var data = data + "<div class='progress'><div class='progress-bar' role='progressbar' aria-valuenow='"+full.percentComplete+"' aria-valuemin='0' aria-valuemax='100' style='width: "+full.percentComplete+"%;'></div></div>";
        return data;
    }


    return {
        createLinkButton: createLinkButton,
        getHumanTime: getHumanTime,
        displayProgress: displayProgress
    };
})(window.TL);
