import React, { useState } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FileUploader } from "../files/FileUploader";
import { Button, Dropdown, Menu, notification } from "antd";
import { IconCloudUpload, IconDropDown, IconLoading } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";

/**
 * React component to upload sequence files for a sample.
 *
 * @returns {*}
 * @constructor
 */
export function SampleFileUploader() {
  const [uploading, setUploading] = useState(false);

  /**
   * Update the loading state once the upload has begun.
   * @returns {*}
   */
  const onUpload = () => setUploading(true);

  /**
   * Display successful upload then show notification that page will refresh
   * Need to reload the table after successful upload since the table
   * is static.
   * @param text
   * @returns {*}
   */
  const onSuccess = (text) => {
    notification.success({ message: text });
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  };

  const onError = () =>
    notification.error({ message: i18n("SampleFileUploader.error") });

  const onComplete = () => setUploading(false);

  const fileMenu = (
    <Menu className="t-upload-menu">
      <Menu.Item>
        <FileUploader
          allowedTypes=".fastq,.gz"
          url={setBaseUrl(
            `ajax/samples/${window.PAGE.id}/sequenceFiles/upload`
          )}
          onSuccess={onSuccess}
          onUpload={onUpload}
          onError={onError}
          onComplete={onComplete}
        >
          {i18n("SampleFileUploader.sequenceFiles")}
        </FileUploader>
      </Menu.Item>
      <Menu.Item>
        <FileUploader
          allowedTypes=".fasta,.fna"
          url={setBaseUrl(`ajax/samples/${window.PAGE.id}/assemblies/upload`)}
          onSuccess={onSuccess}
          onUpload={onUpload}
          onError={onError}
          onComplete={onComplete}
        >
          {i18n("SampleFileUploader.assembly")}
        </FileUploader>
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={fileMenu} disabled={uploading}>
      <Button className="t-download-dropdown" disabled={uploading}>
        {uploading ? (
          <>
            <IconLoading style={{ marginRight: SPACE_XS }} />
            {i18n("SampleFileUploader.button-uploading")}
            <IconDropDown />
          </>
        ) : (
          <>
            <IconCloudUpload style={{ marginRight: SPACE_XS }} />
            {i18n("SampleFileUploader.button-default")}
            <IconDropDown />
          </>
        )}
      </Button>
    </Dropdown>
  );
}
