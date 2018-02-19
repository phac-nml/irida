import $ from "jquery";

/**
 * Internationalized text from div#messages.hidden
 * @type {Object} map of data attribute key name to i18n text
 */
const MESSAGES = $("#js-messages").data();

/**
 * Analysis output file path regex to capture filename with extension
 * @type {RegExp}
 */
const FILENAME_REGEX = /.*\/(.+\.\w+)/;

/**
 * URL to get analysis output file info via AJAX for a project
 * @type {string}
 */
const AJAX_URL = `${window.PAGE.URLS.base}projects/${
  window.project.id
}/ajax/analysis-outputs`;

/**
 * HTML container for dynamically generating UI for download of output files
 * @type {jQuery|HTMLElement}
 */
const $app = $("#app");

/**
 * Grouped analysis output file info - 2D map/dict
 * analysis type string => analysis output file key => analysis output file info
 * @type {null|Object}
 * @private
 */
let _data = null;

/**
 * Group list of maps of analysis output file info by analysis type and analysis
 * output file key name.
 * @param data List of maps of analysis output file info
 * @returns {{}} 2D dict of analysis output file info grouped by analysis type and output file key name
 */
function groupAnalysisOutput(data) {
  let groupedAnalysisOutput = {};
  for (const x of data) {
    const analysisType = x.analysis_type;
    const analysisOutputFileKey = x.analysis_output_file_key;
    if (groupedAnalysisOutput.hasOwnProperty(analysisType)) {
      if (
        groupedAnalysisOutput[analysisType].hasOwnProperty(
          analysisOutputFileKey
        )
      ) {
        groupedAnalysisOutput[analysisType][analysisOutputFileKey].push(x);
      } else {
        groupedAnalysisOutput[analysisType][analysisOutputFileKey] = [x];
      }
    } else {
      groupedAnalysisOutput[analysisType] = {};
      groupedAnalysisOutput[analysisType][analysisOutputFileKey] = [x];
    }
  }
  return groupedAnalysisOutput;
}

/**
 * Get filename from path
 * @param path File path
 */
function getFilename(path) {
  return path.replace(FILENAME_REGEX, "$1");
}

$.get(AJAX_URL)
  .done(data => {
    _data = groupAnalysisOutput(data);
    // for each analysis type
    Object.keys(_data).forEach((analysisType, i) => {
      const nameToOutputs = _data[analysisType];
      console.debug(analysisType, i, nameToOutputs.length);
      const $panel = $(
        `<div class="panel panel-default" id="panel-${i}"></div>`
      );
      const $panelHeading = $(
        `<div class="panel-heading"><h4>${
          MESSAGES.analysisType
        } - ${analysisType.toLocaleLowerCase()}</h4></div>`
      );
      const $panelBody = $(`<div class="panel-body"></div>`);
      // for each analysis output file key name
      Object.keys(nameToOutputs).forEach((analysisOutputName, j) => {
        const outputs = nameToOutputs[analysisOutputName];
        const firstOutput = outputs[0];
        const filename = getFilename(firstOutput.file_path);
        const $div = $(
          `<div><p>${
            MESSAGES.analysisOutputFileKey
          } - "${analysisOutputName}"</p></div>`
        );
        // Batch download button for analysis type and output file key
        const $button = $(
          `<button class="btn btn-default">${
            MESSAGES.download
          } ${filename} (N=${outputs.length})</button>`
        );
        // on button click, create temporary hidden download link for each
        // analysis output file and simulate click of download link
        $button.on("click", e => {
          e.preventDefault();
          const aLink = document.createElement("a");
          aLink.style.display = "none";
          document.body.appendChild(aLink);
          for (const output of outputs) {
            let url = `${window.PAGE.URLS.base}analysis/ajax/download/${
              output.analysis_submission_id
            }/file/${output.aof_id}`;
            const downloadName = `${output.sample_name}-sample_id-${
              output.sample_id
            }-submission_id-${
              output.analysis_submission_id
            }-${analysisOutputName}-${filename}`;
            url += "?" + $.param({ filename: downloadName });
            aLink.setAttribute("href", url);
            aLink.setAttribute("download", downloadName);
            aLink.click();
          }
          document.body.removeChild(aLink);
        });
        $div.append($button);
        $panelBody.append($div);
      });
      $panel.append($panelHeading);
      $panel.append($panelBody);
      $panel.appendTo($app);
    });
  })
  .fail(function(xhr, error, exception) {
    const $alert = $(
      `<div class="alert alert-danger"><h4>${MESSAGES.reqError}</h4></div>`
    );
    if (xhr !== null) {
      $alert.append($(`<p>${MESSAGES.statusCode}: ${xhr.status}</p>`));
      $alert.append($(`<p>${MESSAGES.requestUrl}: ${AJAX_URL}</p>`));
    }
    if (exception !== null) {
      $alert.append($(`<p>${MESSAGES.statusText}: ${exception}</p>`));
    }
    if (error !== null) {
      $alert.append($(`<p>${MESSAGES.error}: "${error}"</p>`));
    }
    $app.append($alert);
  });
