import React, { lazy, Suspense, useEffect, useState } from "react";
import { FormattedMessage, IntlProvider } from "react-intl";
import { Dropdown, Icon, Menu } from "antd";
import { getTranslations } from "../../../../apis/translations/translations";

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
  const [translations, setTranslations] = useState(null);

  /*
  On component mounting, get the translations. Since this component is rendered
  separately from the main app (through ag-grid) it needs to get its own translations.
   */
  useEffect(() => {
    getTranslations({ page: "linelist", component: "MetadataFieldMenu" }).then(
      data => setTranslations(data)
    );
  }, []);

  /**
   * Hide the delete
   */
  const hideRemoveMetadataEntriesModal = () =>
    setRemoveEntriesModalVisibility(false);

  return (
    <IntlProvider messages={translations}>
      <div>
        <Dropdown
          trigger={["click"]}
          overlay={
            <Menu>
              <Menu.Item onClick={() => setRemoveEntriesModalVisibility(true)}>
                <FormattedMessage id="MetadataFieldMenu_remove_entries" />
              </Menu.Item>
            </Menu>
          }
        >
          <Icon type="more" style={{ display: "inline-block" }} />
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
      </div>
    </IntlProvider>
  );
}
