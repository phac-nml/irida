import { Button, notification, Popconfirm, Tooltip } from "antd";
import React, { useState } from "react";
import { IconRemove } from "../icons/Icons";

/**
 * React component to remove a member from a project
 * @param {object} item - details about the current user
 * @returns {*}
 * @constructor
 */
export function RemoveTableItemButton({
  onRemove,
  onRemoveSuccess = () => Function.prototype,
  tooltipText = "",
  confirmText = "",
  disabledLoggedInUser = false,
}) {
  const [loading, setLoading] = useState(false);

  /**
   * Make the request to remove the item from the project.
   */
  const removeItem = async () => {
    setLoading(true);
    try {
      const message = await onRemove();
      onRemoveSuccess();
      notification.success({ message, className: "t-remove-success" });
    } catch (message) {
      notification.error({
        message,
        className: "t-remove-error",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Popconfirm
      className="t-remove-popover"
      okButtonProps={{ className: "t-remove-confirm" }}
      onConfirm={removeItem}
      placement="topLeft"
      title={confirmText}
      disabled={disabledLoggedInUser}
    >
      <Tooltip title={tooltipText} placement="left">
        <Button
          className="t-remove-btn"
          icon={<IconRemove />}
          shape="circle"
          loading={loading}
          disabled={disabledLoggedInUser}
        />
      </Tooltip>
    </Popconfirm>
  );
}
