import React, { lazy, Suspense, useState } from "react";
import { Dropdown, Icon, Menu } from "antd";

/*
Lazy load the modal since this does not need to be used all the time.
 */
const DeleteMetadataEntriesModal = lazy(() =>
  import("./RemoveMetadataEntriesModal")
);

/**
 * This component renders a "more" icon into the right side of the Ag-Grid
 * Header component to allow for column options
 * @param  {object} field - Metadata Template Field
 * @param {function} removeColumnData - function to remove the data.
 * @returns {*}
 * @constructor
 */
export function MetadataFieldMenu({ field, removeColumnData }) {
  const [
    isRemoveEntriesModalVisible,
    setRemoveEntriesModalVisibility
  ] = useState(false);

  /**
   * Hide the delete
   */
  const hideRemoveMetadataEntriesModal = () =>
    setRemoveEntriesModalVisibility(false);

  return (
    <span>
      <Dropdown
        trigger={["click"]}
        overlay={
          <Menu>
            <Menu.Item
              className="t-delete-entries"
              onClick={() => setRemoveEntriesModalVisibility(true)}
            >
              {window.PAGE.i18n.MetadataFieldMenu_remove_entries}
            </Menu.Item>
          </Menu>
        }
      >
        <Icon type="menu" style={{ display: "inline-block" }} />
      </Dropdown>
      {isRemoveEntriesModalVisible ? (
        <Suspense fallback={<span />}>
          <DeleteMetadataEntriesModal
            field={field}
            visible={isRemoveEntriesModalVisible}
            hideModal={hideRemoveMetadataEntriesModal}
            removeEntries={removeColumnData}
          />
        </Suspense>
      ) : null}
    </span>
  );
}
