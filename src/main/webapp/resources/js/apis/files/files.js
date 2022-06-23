import axios from "axios";
import { UploadProgressNotification } from "../../components/files/UploadProgressNotification";

/**
 * Upload a list of files to the server.
 *
 * @param {array} files - List of files to upload
 * @param {string} url - Url to upload to
 * @returns {Promise<AxiosResponse<any>>}
 */
export function uploadFiles({ files, url, onSuccess, onError }) {
  /**
   * Function called when window onbeforeunload is called when a file is
   * uploading, since leaving the page would cause the upload to cancel.
   * This prompts the user if they want to continue leaving the site.
   * @param event
   */
  const listener = (event) => {
    // Cancel the event as stated by the standard.
    event.preventDefault();
    // Chrome requires returnValue to be set.
    event.returnValue = window.confirm(i18n("FileUploader.listener-warning"));
  };
  window.addEventListener("beforeunload", listener);

  const names = files.map((f) => f.name);
  const formData = new FormData();
  files.forEach((f, i) => formData.append(`files[${i}]`, f));

  const { CancelToken } = axios;
  const source = CancelToken.source();

  const notification = new UploadProgressNotification({
    names,
    request: source,
  });

  return axios
    .post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      cancelToken: source.token,
      onUploadProgress: function (progressEvent) {
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
          notification.show(progress);
        }
      },
    })
    .then(({ data }) => onSuccess(data))
    .catch((error) => {
      if (!axios.isCancel(error)) {
        onError();
      }
    })
    .then(() => {
      window.removeEventListener("beforeunload", listener);
    });
}
