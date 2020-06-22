import React, { useState } from "react";
import { Button, notification, Popconfirm, Tooltip } from "antd";
import { IconRemove } from "../icons/Icons";

/**
 * React component to remove a member from a project
 * @param {object} item - details about the current user
 * @returns {*}
 * @constructor
 */
export function RemoveTableItemButton({
  onRemove,
  onRemoveSuccess = () => {},
  tooltipText = "",
  confirmText = "",
}) {
  const [loading, setLoading] = useState(false);

  /**
   * Handle the successful removal of the current item
   * @param message
   */
  const removeSuccess = (message) => {
    onRemoveSuccess();
    notification.success({message, className: "t-remove-success"})
  };

  /**
   * Make the request to remove the item from the project.
   */
  const removeItem = () => {
    setLoading(true);
    onRemove()
      .then(removeSuccess)
      .catch((error) =>
        notification.error({
          message: error.response.data,
          className: "t-remove-error",
        })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Popconfirm
      className="t-remove-popover"
      okButtonProps={{ className: "t-remove-confirm" }}
      onConfirm={removeItem}
      placement="topLeft"
      title={confirmText}
    >
      <Tooltip title={tooltipText} placement="left">
        <Button
          className="t-remove-btn"
          icon={<IconRemove />}
          shape="circle-outline"
          loading={loading}
        />
      </Tooltip>
    </Popconfirm>
  );
}
