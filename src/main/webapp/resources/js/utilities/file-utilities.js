/**
 * DEPRECATED - this can be replaced by using the download attribute on an anchor element.
 *
 * Creates a new iFrame to download a file into.
 * @param {string} url to download file from.
 */
export function download(url) {
  const iframe = document.createElement("iframe");
  iframe.src = url;
  iframe.style.display = "none";
  document.body.appendChild(iframe);
}

/**
 * Download a file using a post request.
 * This is good for when large amounts of data are required ot create the download
 * @param {string} url - Url to send the info to.
 * @param {object} contents - Any details that need to be including in the request.
 */
export function downloadPost(url, contents) {
  const form =
    document.querySelector("#submit-download") ||
    document.createElement("form");

  form.setAttribute("id", "submit-download");
  form.setAttribute("method", "POST");
  form.setAttribute("action", url);
  form.setAttribute("target", "_self");
  form.setAttribute("style", "display: none");

  for (const [name, value] of Object.entries(contents)) {
    const field = document.createElement("input");
    field.setAttribute("name", name);
    field.setAttribute("value", value);
    form.appendChild(field);
  }

  document.body.appendChild(form);
  form.submit();
  document.body.removeChild(form);
}

/**
 * Convert file size from bytes to larger unit.
 * @param {number} bytes size of file.
 * @return {string} converted file size;
 */
export function convertFileSize(bytes) {
  if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {
    return bytes;
  }
  const thresh = 1024;
  if (Math.abs(bytes) < thresh) {
    return bytes + " B";
  }
  const units = ["kB", "MB", "GB"];
  let u = -1;
  do {
    bytes /= thresh;
    ++u;
  } while (Math.abs(bytes) >= thresh && u < units.length - 1);
  return bytes.toFixed(1) + " " + units[u];
}

/**
 *
 * Returns how much of a file has been loaded into preview div
 * @param {number} byte Number of bytes of file currently fetched from server
 * @param {number} fileSizeBytes File size in bytes
 * @returns {string} Returns file size loaded (x of y bytes as well as the percentage of the file)
 */
export function fileSizeLoaded(byte, fileSizeBytes) {
  return `${convertFileSize(byte)} / ${convertFileSize(fileSizeBytes)} (${(
    (byte / fileSizeBytes) *
    100
  ).toFixed(1)}%)`;
}

/**
 *
 * @param {number} filePosition Starting byte in file
 * @param {number} fileSizeBytes Total file size
 * @param {number} chunkSize Number of bytes to retrieve from filePosition
 * @returns {number} Size of chunk to retrieve from file in bytes
 */
export function getNewChunkSize(filePosition, fileSizeBytes, chunkSize) {
  return Math.min(fileSizeBytes - filePosition, chunkSize);
}
