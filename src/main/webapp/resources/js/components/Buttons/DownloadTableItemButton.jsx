import React, { useState } from "react";
import { Button, notification, Tooltip } from "antd";
import { IconDownloadFile } from "../icons/Icons";

/**
 * React component to download an item from the table
 * @param {object} item - details about item
 * @returns {*}
 * @constructor
 */
export function DownloadTableItemButton({
  onDownload,
  onDownloadSuccess = () => {},
  tooltipText = "",
  disableDownloadButton,
}) {
  const [loading, setLoading] = useState(false);

  /**
   * Handle the successful download of the current item
   * @param message
   */
  const downloadSuccess = () => {
    onDownloadSuccess();
  };

  /**
   * Make the request to download the item from the project.
   */
  const downloadItem = () => {
    setLoading(true);
    onDownload()
      .then(downloadSuccess)
      .catch((error) =>
        notification.error({
          message: error.response.data,
          className: "t-download-error",
        })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Tooltip title={tooltipText} placement="left">
      <Button
        className="t-download-btn"
        icon={<IconDownloadFile />}
        shape="circle"
        loading={loading}
        onClick={downloadItem}
        disabled={disableDownloadButton}
      />
    </Tooltip>
  );
}
