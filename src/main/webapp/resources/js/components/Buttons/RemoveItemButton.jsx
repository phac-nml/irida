import React, { useContext, useState } from "react";
import { Button, notification, Popconfirm, Tooltip } from "antd";
import { IconRemove } from "../icons/Icons";
import { PagedTableContext } from "../ant.design/PagedTable";

/**
 * React component to remove an item from a table / list.
 * @param {function} removeFn ajax request to remove the item
 * @param {function} onRemoveSuccess handler for after the item has been successfully removed
 * @param {function} onRemoveError handler for if there is an error removing item
 * @param {string} btnTooltip tooltip title
 * @param {string} popoverLabel popover label
 * @returns {*}
 * @constructor
 */
export function RemoveItemButton({
  removeFn = () => {},
  onRemoveSuccess = () => {},
  onRemoveError = () => {},
  btnTooltip,
  popoverLabel,
}) {
  const { updateTable } = useContext(PagedTableContext);
  const [loading, setLoading] = useState(false);

  /**
   * Handle the successful removal of the current item
   * @param message
   */
  const removeSuccess = (message) => {
    notification.success({ message, className: "t-remove-success" });
    updateTable();
    onRemoveSuccess();
  };

  const removeError = ({ response }) => {
    notification.error({
      message: response.data,
      className: "t-remove-error",
    });
    onRemoveError();
  };

  /**
   * Make the request to remove the item from the project.
   */
  const removeItem = () => {
    setLoading(true);
    removeFn()
      .then(removeSuccess)
      .catch(removeError)
      .finally(() => setLoading(false));
  };

  return (
    <Popconfirm
      className="t-remove-popover"
      okButtonProps={{ className: "t-remove-confirm" }}
      onConfirm={removeItem}
      placement="topLeft"
      title={popoverLabel}
    >
      <Tooltip title={btnTooltip} placement="left">
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
