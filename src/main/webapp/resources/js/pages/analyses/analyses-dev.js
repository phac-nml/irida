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

    return {
        createLinkButton: createLinkButton
    };
})(window.TL);