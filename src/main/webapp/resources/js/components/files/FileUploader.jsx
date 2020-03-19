import React, { useRef } from "react";
import { Button } from "antd";
import { uploadFiles } from "../../apis/files/files";
import { IconCloudUpload } from "../icons/Icons";

/**
 * Generic file uploader.  Handles single and multiple files.
 * @param {string} url - Url to upload files to
 * @param {string} label - Text to display on the button
 * @param {string} allowedTypes - Input accepts attribute
 * @param {function} onSuccess - what to do after successful upload
 * @param {function} onError - what to do if there is an error uploading
 * @returns {*}
 * @constructor
 */
export function FileUploader({
  url,
  label,
  allowedTypes = "",
  onSuccess = () => {},
  onError = () => {}
}) {
  const inputRef = useRef();

  /**
   * Submit the files to the server. Need to first get the files from the event.
   * @param e
   */
  const submitFiles = e => {
    const files = e.target.files;
    if (files.length) {
      uploadFiles({ url, files })
        .then(onSuccess)
        .catch(onError);
    }
  };

  return (
    <>
      <Button onClick={() => inputRef.current.click()}>
        <IconCloudUpload />
        {label}
      </Button>
      <input
        ref={inputRef}
        type="file"
        multiple
        hidden
        onChange={submitFiles}
        accept={allowedTypes}
      />
    </>
  );
}
