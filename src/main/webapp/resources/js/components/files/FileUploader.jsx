import React, { useRef } from "react";
import { Button } from "antd";
import { uploadFiles } from "../../apis/files/files";
import { IconCloudUpload } from "../icons/Icons";

export function FileUploader({
  url,
  label,
  allowedTypes = "",
  onSuccess = () => {}
}) {
  const inputRef = useRef();

  const submitFiles = e => {
    const files = e.target.files;
    if (files.length) {
      uploadFiles({ url, files }).then(onSuccess);
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
