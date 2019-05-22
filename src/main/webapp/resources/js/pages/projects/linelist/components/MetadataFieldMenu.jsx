import React, { lazy, Suspense, useEffect, useState } from "react";
import { FormattedMessage, IntlProvider } from "react-intl";
import { Dropdown, Icon, Menu } from "antd";
import { getTranslations } from "../../../../apis/translations/translations";

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
  const [deleteColVisible, setDeleteColVisibility] = useState(false);
  const [translations, setTranslations] = useState(null);

  useEffect(() => {
    /*
    Since this component is rendered separately from the main app (through ag-grid)
    It needs to get its own translations.
     */
    getTranslations({ page: "linelist", component: "MetadataFieldMenu" }).then(
      data => setTranslations(data)
    );
  }, []);

  const hideDeleteColumnModal = () => setDeleteColVisibility(false);

  return (
    <IntlProvider messages={translations}>
      <div>
        <Dropdown
          trigger={["click"]}
          overlay={
            <Menu>
              <Menu.Item onClick={() => setDeleteColVisibility(true)}>
                <FormattedMessage id="MetadataFieldMenu_remove_entries" />
              </Menu.Item>
            </Menu>
          }
        >
          <Icon type="more" style={{ display: "inline-block" }} />
        </Dropdown>
        {deleteColVisible ? (
          <Suspense fallback={<span />}>
            <DeleteMetadataEntriesModal
              field={field}
              visible={deleteColVisible}
              hideModal={hideDeleteColumnModal}
              removeEntries={removeColumnData}
            />
          </Suspense>
        ) : null}
      </div>
    </IntlProvider>
  );
}
