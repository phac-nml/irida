import React from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FileUploader } from "../files/FileUploader";
import {
  showErrorNotification,
  showNotification
} from "../../modules/notifications";
import { Button, Dropdown, Menu } from "antd";
import { IconCloudUpload, IconDropDown } from "../icons/Icons";

/**
 * React component to upload sequence files for a sample.
 *
 * @returns {*}
 * @constructor
 */
export function SampleFileUploader() {
  /**
   * Display successful upload then show notification that page will refresh
   * Need to reload the table after successful upload since the table
   * is static.
   * @param text
   * @returns {*}
   */
  const onSuccess = text => {
    showNotification({ text });
    setTimeout(() => {
      window.location.reload();
    }, 3000);
  };

  const fileMenu = (
    <Menu className="t-upload-menu">
      <Menu.Item>
        <FileUploader
          allowedTypes=".fastq,.fastq.gz"
          url={setBaseUrl(
            `ajax/samples/${window.PAGE.id}/sequenceFiles/upload`
          )}
          onSuccess={onSuccess}
          onError={text => showErrorNotification({ text })}
        >
          {i18n("SampleFileUploader.sequenceFiles")}
        </FileUploader>
      </Menu.Item>
      <Menu.Item>
        <FileUploader
          allowedTypes=".fasta,.fna"
          url={setBaseUrl(`ajax/samples/${window.PAGE.id}/assemblies/upload`)}
          onSuccess={onSuccess}
          onError={text => showErrorNotification({ text })}
        >
          {i18n("SampleFileUploader.assembly")}
        </FileUploader>
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={fileMenu}>
      <Button className="t-download-dropdown">
        <IconCloudUpload />
        Upload Files
        <IconDropDown />
      </Button>
    </Dropdown>
  );
}
