import React, { useRef } from "react";
import { notification } from "antd";
import { uploadFiles } from "../../apis/files/files";

/**
 * Generic file uploader.  Handles single and multiple files.
 *
 * @param {string} url - Url to upload files to
 * @param {string} label - Text to display on the button
 * @param {string} allowedTypes - Input accepts attribute
 * @param {function} onSuccess - what to do after successful upload
 * @param {function} onError - what to do if there is an error uploading
 * @param {function} onBadFiles - what to do if there are files not match acceptable types
 * @returns {*}
 * @constructor
 */
export function FileUploader({
  children,
  url,
  label,
  allowedTypes = "",
  onSuccess = () => {},
  onError = () => {},
  onBadFiles = () => {}
}) {
  const inputRef = useRef();

  const showBadFilesNotification = (files, types) => {
    const description = (
      <>
        {i18n("FileUploader.badFiles")}
        <ul>
          {files.map(f => (
            <li className="t-bad-file-name">{f.name}</li>
          ))}
        </ul>
        <p>{i18n("FileUploader.continued", types.join(", "))}</p>
      </>
    );

    /*
     This notification is different the the standard global notifications.
     It will persist to the screen so the user can see and copy the file
     names that are not uploaded.
     */
    notification.error({
      className: "t-file-upload-error",
      message: i18n("FileUploader.message"),
      description,
      placement: "bottomRight",
      duration: 0
    });
  };

  /**
   * Submit the files to the server. Need to first get the files from the event.
   *
   * @param {object} e - React synthetic event
   */
  const submitFiles = e => {
    const files = e.target.files;
    // Check to see if it matches the allowed types
    const allowed = allowedTypes.split(",");
    const good = [];
    const bad = [];
    files.forEach(f => {
      let found = false;
      for (let type of allowed) {
        if (f.name.endsWith(type)) {
          good.push(f);
          found = true;
          break;
        }
      }
      if (!found) {
        bad.push(f);
      }
    });

    if (bad.length) {
      showBadFilesNotification(bad, allowed);
    } else if (good.length) {
      uploadFiles({ url, files })
        .then(onSuccess)
        .catch(onError);
    }
  };

  return (
    <div className="t-file-upload-btn" onClick={() => inputRef.current.click()}>
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
    </div>
  );
}
