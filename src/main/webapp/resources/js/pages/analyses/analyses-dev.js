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
            return "<a class='btn btn-link crop' title='"+data+"' href='" + tl.BASE_URL + "analysis/" + full.id + "'>" + data + "</a>";
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
        //assign colours to the progress bar for complete and error
        var stateClasses ={
          'COMPLETED': "progress-bar-success",
          'ERROR': "progress-bar-danger"
        }

        var stateClass = "";
        if(stateClasses[full.submission.analysisState] != null){
          stateClass = stateClasses[full.submission.analysisState];
        }

        //if error, we want to show the full red bar
        var percent = full.percentComplete;
        if(full.submission.analysisState === 'ERROR'){
          percent = 100;
        }

        var data = data + "<div class='progress'><div class='progress-bar " + stateClass +"' role='progressbar' aria-valuenow='"+percent+"' aria-valuemin='0' aria-valuemax='100' style='width: "+percent+"%;'></div></div>";
        return data;
    }

    function downloadLink(data,type,full){
        if(full.submission.analysisState === 'COMPLETED'){
          return "<a class='btn btn-default btn-xs download-analysis-btn' href='" + PAGE.URLS.download + full.id + "'><i class='fa fa-download' /></a>";
        }
        else{
          return "";
        }
    }

    return {
        createLinkButton: createLinkButton,
        getHumanTime: getHumanTime,
        displayProgress: displayProgress,
        downloadLink: downloadLink
    };
})(window.TL);
