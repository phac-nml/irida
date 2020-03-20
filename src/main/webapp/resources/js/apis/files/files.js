import axios from "axios";

/**
 * Upload a list of files to the server.
 * @param {array} files - List of files to upload
 * @param {string} url - Url to upload to
 * @returns {Promise<AxiosResponse<any>>}
 */
export function uploadFiles({ files, url }) {
  const formData = new FormData();
  files.forEach((f, i) => formData.append(`files[${i}]`, f));

  return axios
    .post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
    .then(({ data }) => data)
    .catch(({ data }) => data);
}
