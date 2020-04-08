import React from "react";
import { notification } from "antd";

export const showBadFilesNotification = (names, types) => {
  const description = (
    <>
      {i18n("FileUploader.badFiles")}
      {
        <ul className="t-bad-files">
          {names.map(name => (
            <li key={name}>{name}</li>
          ))}
        </ul>
      }
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

export const checkForBadFileTypes = (files, allowed) => {
  function doesNotHaveCorrectFileExtension(file) {
    return allowed.filter(type => file.name.endsWith(type)).length === 0;
  }
  return files.filter(doesNotHaveCorrectFileExtension).map(f => f.name);
};
