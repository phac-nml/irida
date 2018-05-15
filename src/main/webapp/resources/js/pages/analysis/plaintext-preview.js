import $ from "jquery";
import { analysisOutputFileApiUrl, panelHeading } from "./preview.utils";
import { convertFileSize } from "../../utilities/file.utilities";

/**
 * Status text for showing how many bytes of a file have been loaded.
 * @param {number} byte Number of bytes of file currently fetched from server
 * @param {number} fileSizeBytes File size in bytes
 * @returns {string}
 */
const statusText = (byte, fileSizeBytes) =>
  `${convertFileSize(byte)} / ${convertFileSize(fileSizeBytes)} (${(
    byte /
    fileSizeBytes *
    100
  ).toFixed(1)}%)`;

/**
 * Convert some JS Object, Array or scalar value to basic HTML recursively.
 * @param {Object|Array|number|string} x JS Object, Array or scalar
 * @param {string} acc Accumulator for recursion.
 * @returns {string} HTML string representation of `x`
 */
function jsToHtml(x, acc = "") {
  const DIV_MARGIN = `<div class="border-1-gray" style="margin-left: 10px">`;
  const DIV = `<div class="pad5 border-1-gray">`;
  if ($.isArray(x)) {
    if (x.length === 1) {
      acc += jsToHtml(x[0], "");
    } else {
      acc += DIV_MARGIN;
      for (const item of x) {
        acc += `<div>${jsToHtml(item, "")}</div>`;
      }
      acc += "</div>";
    }
  } else if ($.isPlainObject(x)) {
    acc += DIV_MARGIN;
    for (const [k, v] of Object.entries(x)) {
      acc += `${DIV}<b>${k}:</b> ${jsToHtml(v, "")}</div>`;
    }
    acc += `</div>`;
  } else {
    acc += `${x}`;
  }

  return acc;
}

/**
 * Render a preview of a plain-text AnalysisOutputFile
 * @param {jQuery|HTMLElement} $container Container element to render preview in
 * @param {string} baseUrl Base AJAX URL (e.g. /analysis/ajax/)
 * @param {Object} aof AnalysisOutputFile info
 * @param {number} height Preview container height
 * @param {number} chunk_size Number of bytes to read from AnalysisOutputFile at a time
 */
export function renderPlainTextPreview(
  $container,
  baseUrl,
  aof,
  height = 300,
  chunk_size = 8192
) {
  const { id, fileSizeBytes, fileExt } = aof;
  const $panel = $(`<div id="js-panel-${id}" class="panel panel-default"/>`);
  const $panelHeading = $(panelHeading(baseUrl, aof));
  $panel.append($panelHeading);
  const $panelBody = $(`<div class="panel-body"></div>`);
  const elId = `js-text-${id}`;
  const $textEl = $(`<pre/>`, {
    id: elId
  });
  $textEl.css({
    "white-space": "pre-wrap",
    resize: "both",
    height: `${height}px`,
    width: "100%"
  });
  const apiUrl = analysisOutputFileApiUrl(baseUrl, aof);
  /**
   * AnalysisOutputFile text content GET request parameters.
   * - `seek` is the byte to seek to and begin reading at
   * - `chunk` is the number of bytes to read from the file. If `chunk` is
   * @type {{seek: number, chunk: number}}
   */
  const params = {
    seek: 0,
    chunk: Math.min(fileSizeBytes, chunk_size)
  };
  let $showMore = $(
    `<p class="small pull-right">${statusText(0, fileSizeBytes)}</p>`
  );
  let showMoreUrl = `${apiUrl}?${$.param(params)}`;

  const getNewChunkSize = (filePosition, fileSizeBytes, chunkSize) =>
    Math.min(fileSizeBytes - filePosition, chunkSize);

  function onTextScroll() {
    if (this.fetching) {
      return;
    }
    if (params.chunk === 0) {
      return;
    }
    this.fetching = false;
    if (
      $(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight &&
      getNewChunkSize(params.seek, fileSizeBytes, chunk_size) >= 0
    ) {
      let that = this;
      showMoreUrl = `${apiUrl}?${$.param(params)}`;
      that.fetching = true;
      $.ajax({
        url: showMoreUrl,
        success: function({ text, filePointer }) {
          that.fetching = false;
          const moreText = $textEl.text() + text;
          if (fileExt === "json") {
            try {
              $textEl.html(jsToHtml(JSON.parse(moreText)));
              that.fetching = true;
            } catch (e) {
              $textEl.text(moreText);
              that.fetching = false;
            }
          } else {
            $textEl.text(moreText);
            that.fetching = false;
          }
          params.seek = filePointer;
          params.chunk = getNewChunkSize(
            params.seek,
            fileSizeBytes,
            chunk_size
          );
          showMoreUrl =
            params.chunk === 0 ? "" : `${apiUrl}?${$.param(params)}`;
          if (showMoreUrl === "") {
            that.fetching = true;
          }
          $showMore.text(statusText(params.seek, fileSizeBytes));
        }
      });
    }
  }

  $.ajax({
    url: showMoreUrl,
    success: ({ text, filePointer }) => {
      if (fileExt === "json") {
        try {
          $textEl.html(jsToHtml(JSON.parse(text)));
        } catch (e) {
          $textEl.text(text);
        }
      } else {
        $textEl.text(text);
      }
      params.seek = filePointer;
      params.chunk = getNewChunkSize(params.seek, fileSizeBytes, chunk_size);
      $showMore.text(statusText(params.seek, fileSizeBytes));
      // if next chunk to fetch is 0, then no need to setup fetching of more
      // file text on text scroll
      if (params.chunk === 0) {
        return;
      }
      // fetch more text data on scroll event
      $textEl.on("scroll", onTextScroll);
    }
  });

  $panelBody.append($textEl);
  $panelBody.append($showMore);
  $panel.append($panelBody);
  $container.append($panel);
}
