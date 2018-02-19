import $ from "jquery";

/**
 * Internationalized text from div#messages.hidden
 * @type {Object} map of data attribute key name to i18n text
 */
const MESSAGES = $("#js-messages").data();

const FILENAME_REGEX = /.*\/(.+\.\w+)/;

const AJAX_URL = `${window.PAGE.URLS.base}projects/${
  window.project.id
}/ajax/analysis-outputs`;

const $app = $("#app");

let _data = null;

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

$.get(AJAX_URL, function(data) {
  _data = groupAnalysisOutput(data);
  Object.keys(_data).forEach((analysisType, i) => {
    const nameToOutputs = _data[analysisType];
    console.log(analysisType, i, nameToOutputs.length);
    const $panel = $(`<div class="panel panel-default" id="panel-${i}"></div>`);
    const $panelHeading = $(
      `<div class="panel-heading"><h4>${
        MESSAGES.analysisType
      } - ${analysisType.toLocaleLowerCase()}</h4></div>`
    );
    const $panelBody = $(`<div class="panel-body"></div>`);

    Object.keys(nameToOutputs).forEach((analysisOutputName, j) => {
      const outputs = nameToOutputs[analysisOutputName];
      const firstOutput = outputs[0];
      const filename = firstOutput.file_path.replace(FILENAME_REGEX, "$1");
      const $div = $(
        `<div><p>${
          MESSAGES.analysisOutputFileKey
        } - "${analysisOutputName}"</p></div>`
      );
      const $button = $(
        `<button class="btn btn-default">${MESSAGES.download} ${filename} (N=${
          outputs.length
        })</button>`
      );
      $button.on("click", e => {
        console.debug("CLICK", analysisOutputName, outputs);
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
          console.debug("output", url, downloadName, output);
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
});
