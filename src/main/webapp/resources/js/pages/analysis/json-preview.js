import $ from "jquery";
import { analysisOutputFileApiUrl, panelHeading } from "./preview.utils";
import { convertFileSize } from "../../utilities/file.utilities";

/**
 * Given some malformed JSON string, return a list of tokens missing their pairs.
 *
 * Each {, [, " needs their closing ", ], }
 *
 * Add dummy key-value if necessary.
 *
 * Examples:
 *
 * > missingTokensStack('{"hell') => ["{", ":"..."", """]
 * > missingTokensStack('[{"') => ["[", "{", "key":"...""]
 *
 * @param {string} malformedJSON Malformed JSON string
 * @returns {Array} JSON tokens missing pairs.
 */
function missingTokensStack(malformedJSON) {
  // stack to keep track of JSON significant tokens
  const stack = [];
  // Generator to look through string character by character
  function* gen(x) {
    yield* x;
  }
  const genJson = gen(malformedJSON);
  /**
   * Last non-whitespace
   * @type {string}
   */
  let last = null;
  /**
   * Current character in iteration.
   * @type {string}
   */
  let curr = null;
  /**
   * Are we in the middle of a map key?
   * @type {boolean}
   */
  let isInKey = false;
  while (true) {
    const next = genJson.next();
    curr = next.value;
    const lastItem = stack[stack.length - 1];
    /**
     * Are we in the middle of a string?
     * @type {boolean}
     */
    const isInQuote = lastItem === '"';
    switch (curr) {
      case "[":
        if (isInQuote) break;
        stack.push(curr);
        break;
      case "{":
        if (isInQuote) break;
        stack.push(curr);
        break;
      case "]":
        if (isInQuote) break;
        stack.pop();
        break;
      case "}":
        if (isInQuote) break;
        stack.pop();
        break;
      case '"':
        // escaped string?
        if (last === "\\") break;
        // closing quote?
        if (isInQuote) {
          stack.pop();
          break;
        }
        // if the previous non-whitespace character was a `,` or `{`, then we
        // are in a map key
        if ((last === "," || last === "{") && lastItem === "{") {
          isInKey = true;
        }
        stack.push(curr);
        break;
      case ":":
        if (isInQuote) break;
        isInKey = false;
        break;
      default:
        break;
    }
    // keep track of last non-whitespace character
    if (/\S/.test(curr)) {
      last = curr;
    }
    if (next.done) break;
  }
  // depending on what the last character is in the malformed JSON string and
  // the last token on the stack or if we're in a map key at the end of the
  // string, then add a dummy key
  const lastChar = malformedJSON[malformedJSON.length - 1];
  const lastStackItem = stack[stack.length - 1];
  if (isInKey && lastChar !== '"') {
    // if at the end of the JSON string, we're in a map key and the last
    // character isn't a quote `"`, then insert a dummy value for the truncated
    // key
    const tmp = stack.pop();
    stack.push(':"..."');
    stack.push(tmp);
  } else if (isInKey && lastChar === '"') {
    // if the last character is a quote and we're in a map key, insert a dummy
    // key-value pair
    stack.pop();
    // notice no beginning quote for "key\"...", the beginning quote is
    // accounted for by the `lastChar` being "
    stack.push('key":"..."');
  } else if (lastChar === "," && lastStackItem === "{") {
    // if we're in a map and the last character is a comma, insert a full dummy
    // key-value pair
    stack.push('"key":"..."');
  } else if (lastChar === ":" && !isInKey && lastStackItem === "{") {
    stack.push("0");
  }
  return stack;
}

function appendMissingTokens(malformedJson, missingTokens) {
  const tokenSubMap = {
    "{": "}",
    "[": "]",
    '"': '"'
  };
  return (
    malformedJson +
    missingTokens
      .map(x => {
        if (tokenSubMap.hasOwnProperty(x)) {
          return tokenSubMap[x];
        }
        return x;
      })
      .reverse()
      .join("")
  );
}

function repairMalformedJSON(malformedJSON) {
  const missingTokens = missingTokensStack(malformedJSON);
  return JSON.parse(appendMissingTokens(malformedJSON, missingTokens));
}

/**
 * Status text for showing how many bytes of a file have been loaded.
 * @param {number} byte Number of bytes of file currently fetched from server
 * @param {number} fileSizeBytes File size in bytes
 * @returns {string}
 */
const statusText = (byte, fileSizeBytes) =>
  `${convertFileSize(byte)} / ${convertFileSize(fileSizeBytes)} (${(
    (byte / fileSizeBytes) *
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
 * Render a preview of a JSON AnalysisOutputFile
 * @param {jQuery|HTMLElement} $container Container element to render preview in
 * @param {string} baseUrl Base AJAX URL (e.g. /analysis/ajax/)
 * @param {Object} aof AnalysisOutputFile info
 * @param {number} height Preview container height
 * @param {number} chunk_size Number of bytes to read from AnalysisOutputFile at a time
 */
export function renderJsonPreview(
  $container,
  baseUrl,
  aof,
  height = 300,
  chunk_size = 8192
) {
  const { id, fileSizeBytes } = aof;
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

  let savedText = "";

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
          savedText += text;
          const moreText = savedText;
          try {
            $textEl.html(jsToHtml(JSON.parse(moreText)));
            that.fetching = true;
          } catch (e) {
            try {
              $textEl.html(jsToHtml(repairMalformedJSON(moreText)));
            } catch (eep) {
              console.warn(moreText.substr(moreText.length - 100));
              console.error(eep);
              $textEl.text(moreText);
            }
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
      savedText = text;
      try {
        $textEl.html(jsToHtml(JSON.parse(savedText)));
      } catch (e) {
        try {
          $textEl.html(jsToHtml(repairMalformedJSON(savedText)));
        } catch (eep) {
          console.warn(savedText.substr(savedText.length - 100));
          console.error(eep);
          $textEl.text(savedText);
        }
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
