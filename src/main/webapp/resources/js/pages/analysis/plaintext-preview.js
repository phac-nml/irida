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
  `Loaded ${convertFileSize(byte)}/${convertFileSize(fileSizeBytes)} (${(
    byte /
    fileSizeBytes *
    100
  ).toFixed(1)}%)`;

export function renderPlainTextPreview(
  $container,
  baseUrl,
  analysisSubmissionId,
  { outputName, id, filename, fileSizeBytes },
  MAX_TABLE_HEIGHT = 300,
  TEXT_CHUNK_SIZE = 8192
) {
  const $panel = $(`<div id="js-panel-${id}" class="panel panel-default"/>`);
  const $panelHeading = $(
    panelHeading(baseUrl, analysisSubmissionId, id, outputName, filename)
  );
  $panel.append($panelHeading);
  const $panelBody = $(`<div class="panel-body"></div>`);
  const elId = `js-text-${id}`;
  const $textEl = $(`<pre/>`, {
    id: elId
  });
  $textEl.css({
    "white-space": "pre-wrap",
    resize: "both",
    height: `${MAX_TABLE_HEIGHT}px`,
    width: "100%"
  });
  const apiUrl = analysisOutputFileApiUrl(baseUrl, analysisSubmissionId, id);
  /**
   * AnalysisOutputFile text content GET request parameters.
   * - `seek` is the byte to seek to and begin reading at
   * - `chunk` is the number of bytes to read from the file. If `chunk` is
   * @type {{seek: number, chunk: number}}
   */
  const params = {
    seek: 0,
    chunk: Math.min(fileSizeBytes, TEXT_CHUNK_SIZE)
  };
  let $showMore = $(
    `<p class="small pull-right">${statusText(0, fileSizeBytes)}</p>`
  );
  let showMoreUrl = `${apiUrl}?${$.param(params)}`;

  const getNewChunkSize = (filePosition, fileSizeBytes, chunkSize) =>
    Math.min(fileSizeBytes - filePosition, chunkSize);

  function onTextScroll() {
    if (params.chunk === 0) {
      return;
    }
    if (
      $(this).scrollTop() + $(this).innerHeight() >=
      $(this)[0].scrollHeight
    ) {
      showMoreUrl = `${apiUrl}?${$.param(params)}`;
      $.ajax({
        url: showMoreUrl,
        success: function({ text, filePointer }) {
          $textEl.text($textEl.text() + text);
          params.seek = filePointer;
          params.chunk = getNewChunkSize(
            params.seek,
            fileSizeBytes,
            TEXT_CHUNK_SIZE
          );
          showMoreUrl =
            params.chunk === 0 ? "" : `${apiUrl}?${$.param(params)}`;
          $showMore.text(statusText(params.seek, fileSizeBytes));
        }
      });
    }
  }

  $.ajax({
    url: showMoreUrl,
    success: ({ text, filePointer }) => {
      $textEl.text(text);
      params.seek = filePointer;
      params.chunk = getNewChunkSize(
        params.seek,
        fileSizeBytes,
        TEXT_CHUNK_SIZE
      );
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
