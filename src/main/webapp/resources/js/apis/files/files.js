import axios from "axios";

/**
 * Upload a list of files to the server.
 *
 * @param {array} files - List of files to upload
 * @param {string} url - Url to upload to
 * @param {function} onProgressUpdate - allows caller to tap into the upload progress.
 * @returns {Promise<AxiosResponse<any>>}
 */
export function uploadFiles({ files, url, onProgressUpdate = () => {} }) {
  const formData = new FormData();
  files.forEach((f, i) => formData.append(`files[${i}]`, f));

  return axios
    .post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      },
      onUploadProgress: function(progressEvent) {
        const totalLength = progressEvent.lengthComputable
          ? progressEvent.total
          : progressEvent.target.getResponseHeader("content-length") ||
            progressEvent.target.getResponseHeader(
              "x-decompressed-content-length"
            );
        if (totalLength !== null) {
          const progress = Math.round(
            (progressEvent.loaded * 100) / totalLength
          );
          onProgressUpdate(progress);
        }
      }
    })
    .then(({ data }) => data)
    .catch(({ data }) => data);
}
