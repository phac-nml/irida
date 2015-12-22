/*
 * Used by the Analyses Listing Datatable (analyses.html)
 */
var analysesTable = (function(tl, moment, $http){
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

        var data = data + "<div class='progress analysis__state'><div class='progress-bar " + stateClass +"' role='progressbar' aria-valuenow='"+percent+"' aria-valuemin='0' aria-valuemax='100' style='width: "+percent+"%;'></div></div>";
        return data;
    }

    function downloadLink(data,type,full){
    	var buttons = "<div class='btn-group pull-right'>"
        if(full.submission.analysisState === 'COMPLETED'){
          buttons += "<a class='btn btn-default btn-xs download-analysis-btn' href='" + PAGE.URLS.download + full.id + "'><span class='fa fa-download' /></a>";
        }
    	
    	buttons += "<button type='button' data-toggle='modal' data-target='#deleteConfirmModal' class='btn btn-default btn-xs delete-analysis-btn' data-analysis-id='" + full.id + "'><span class='fa fa-trash'></button></div>";
    	return buttons;
    }
        
    function deleteLinkCallback(row, data, index) {
    	$(row).find(".delete-analysis-btn").click(function () {
    		$("#deleteConfirmModal").on("show.bs.modal", function (event) {
    			var modal = $(this);
    			var id = data.id;
    			modal.find("#delete-analysis-button").off("click").click(function() {
		    		$.ajax({
		    			url: PAGE.URLS.deleteUrl + id,
		    			type: 'DELETE',
		    			success: function (result) {
		    				oTable_analysisTable.ajax.reload();
		    				notifications.show({
		    					       'msg' : result.result
		    					     });
		    				modal.modal('hide');
		    			}
		    		});
    			});
    		});
    	});
    }

    return {
        createLinkButton: createLinkButton,
        getHumanTime: getHumanTime,
        displayProgress: displayProgress,
        downloadLink: downloadLink,
        deleteLinkCallback: deleteLinkCallback
    };
})(window.TL, window.moment);
