import React, { useRef } from "react";
import { uploadFiles } from "../../apis/files/files";
import {
  checkForBadFileTypes,
  showBadFilesNotification
} from "./file-upload-utilities";

/**
 * Generic file uploader.  Handles single and multiple files.
 *
 *
 * @param {element} children - Button or dropdown element
 * @param {string} url - Url to upload files to
 * @param {string} label - Text to display on the button
 * @param {string} allowedTypes - Input accepts attribute
 * @param {function} onUpload - what the parent component should do during upload
 * @param {function} onSuccess - what to do after successful upload
 * @param {function} onError - what to do if there is an error uploading
 * @param {function} onBadFiles - what to do if there are files not match acceptable types
 * @returns {*}
 * @constructor
 */
export function FileUploader({
  children,
  url,
  allowedTypes = "",
  onUpload = () => {},
  onSuccess = () => {},
  onError = () => {},
  onComplete = () => {}
}) {
  const inputRef = useRef();

  /**
   * Submit the files to the server. Need to first get the files from the event.
   *
   * @param {object} e - React synthetic event
   */
  const submitFiles = e => {
    const files = Array.from(e.target.files);
    const allowed = allowedTypes.split(",");
    const bad = checkForBadFileTypes(files, allowed);

    if (bad.length) {
      showBadFilesNotification(bad, allowed);
      return;
    }

    onUpload();
    uploadFiles({
      url,
      files,
      onSuccess,
      onError
    }).finally(onComplete);
  };

  return (
    <button
      style={{ border: "none", backgroundColor: "transparent" }}
      className="t-file-upload-btn"
      onClick={() => inputRef.current.click()}
    >
      <input
        className="t-file-upload-input"
        ref={inputRef}
        type="file"
        multiple
        hidden
        onChange={submitFiles}
        accept={allowedTypes}
      />
      {children}
    </button>
  );
}
