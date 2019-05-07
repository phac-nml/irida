import $ from "jquery";
import { analysisOutputFileApiUrl, trim } from "../preview.utils";

// Example bio_hansel results:
// {
//   "all_subtypes": "1; 2.2.1.1.2",
//   "are_subtypes_consistent": false,
//   "avg_tile_coverage": 27.3961038961,
//   "file_path": [
//   "SRR1203042_1.fastq",
//   "SRR1203042_2.fastq"
// ],
//   "inconsistent_subtypes": [
//   "1",
//   "2.2.1.1.2"
// ],
//   "n_tiles_matching_all": 153,
//   "n_tiles_matching_all_expected": "202",
//   "n_tiles_matching_positive": 3,
//   "n_tiles_matching_positive_expected": "18",
//   "n_tiles_matching_subtype": 1,
//   "n_tiles_matching_subtype_expected": "4",
//   "qc_message": "FAIL: Missing Tiles Error 1: 25.25% missing tiles; more than 5.00% missing tiles threshold. Okay coverage depth (27.6 >= 20.0 expected), but this may be the wrong serovar or species for scheme \"heidelberg\" | FAIL: Mixed Sample Error 2: Mixed subtypes found: \"1; 2.2.1.1.2\".",
//   "qc_status": "FAIL",
//   "sample": "SRR1203042",
//   "scheme": "heidelberg",
//   "scheme_version": "0.5.0",
//   "subtype": "2.2.1.1.2",
//   "tiles_matching_subtype": "2592097-2.2.1.1.2"
// }
// Only rendering basic bio_hansel info targeted for use by technicians

/**
 * Generate HTML representation of bio_hansel results
 * @param {Object} vm bio_hansel results and i18n messages
 * @returns {string} HTML representation of basic bio_hansel results
 */
function toHtml(vm) {
  const {
    avg_tile_coverage,
    sample,
    scheme,
    scheme_version,
    subtype,
    qc_status,
    qc_message
  } = vm.results;
  const { i18n } = vm;

  const QC_CLASS = { FAIL: "danger", WARNING: "warning", PASS: "success" };

  const msgs = qc_message.split("|");

  function qcMsgClass(msg) {
    if (msg.startsWith("FAIL")) {
      return "text-danger";
    }
    if (msg.startsWith("WARNING")) {
      return "text-warning";
    }
    return "";
  }

  const msgHtml = msgs
    .map(trim)
    .map(msg =>
      msg.length
        ? `<li class="${qcMsgClass(msg)}"><span>${msg}</span></li>`
        : ""
    )
    .join("");

  const qcMsgTableRow =
    qc_message === ""
      ? ""
      : `<tr>
      <th>${i18n.qc_message}</th>
      <td><ul>${msgHtml}</ul></td>
    </tr>`;

  return `
  <table class="table table-bordered table-condensed">
    <thead>
    </thead>
    <tbody>
      <tr>
        <th>${i18n.sample}</th>
        <td>${sample}</td>
      </tr>
      <tr>
        <th>${i18n.scheme} (${i18n.scheme_version})</th>
        <td>${scheme} (v${scheme_version})</td>
      </tr>
      <tr>
        <th>${i18n.subtype}</th>
        <td>${subtype}</td>
      </tr>
      <tr>
        <th>${i18n.avg_tile_coverage}</th>
        <td>${avg_tile_coverage}</td>
      </tr>
      <tr>
        <th>${i18n.qc_status}</th>
        <td class="${QC_CLASS[qc_status]}">${qc_status}</td>
      </tr>
      ${qcMsgTableRow}
    </tbody>
  </table>`;
}

/**
 * bio_hansel Angular controller
 *
 * Using literal string templating to generate basic HTML table of bio_hansel
 * results for technicians.
 *
 * @param analysisService Angular service component for getting bio_hansel analysis JSON output file contents
 */
export function BioHanselController(analysisService) {
  const BIO_HANSEL_RESULTS = "bio_hansel-results.json";
  const vm = this;
  vm.results = null;
  const $cntr = $(".js-bio-hansel");
  vm.i18n = $(".js-bio-hansel-messages").data();
  // Get info about each analysis output file for a bio_hansel analysis submission
  // and get all textual contents for `bio_hansel-results.json`, parse JSON and
  // render basic HTML table
  analysisService.getOutputsInfo().then(outputInfos => {
    for (const outputInfo of outputInfos) {
      const { filename, fileSizeBytes } = outputInfo;
      if (filename !== BIO_HANSEL_RESULTS) {
        continue;
      }
      const apiUrl = analysisOutputFileApiUrl(
        analysisService.baseAjaxUrl,
        outputInfo
      );
      const params = { seek: 0, chunk: fileSizeBytes };
      const getUrl = `${apiUrl}?${$.param(params)}`;
      $.ajax({
        url: getUrl,
        success: function({ text }) {
          const j = JSON.parse(text);
          if ($.isArray(j) && j.length === 1 && $.isPlainObject(j[0])) {
            vm.results = j[0];
            $cntr.html(toHtml(vm));
          }
        }
      });
    }
  });
}
