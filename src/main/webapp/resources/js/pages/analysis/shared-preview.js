import { convertFileSize } from "../../utilities/file.utilities";

/**
 * Status text for showing how many bytes of a file have been loaded.
 * @param {number} byte Number of bytes of file currently fetched from server
 * @param {number} fileSizeBytes File size in bytes
 * @returns {string}
 */
export function statusText(byte, fileSizeBytes) {
  return `${convertFileSize(byte)} / ${convertFileSize(fileSizeBytes)} (${(
    (byte / fileSizeBytes) *
    100
  ).toFixed(1)}%)`;
}

export function getNewChunkSize(filePosition, fileSizeBytes, chunkSize) {
  return Math.min(fileSizeBytes - filePosition, chunkSize);
}
