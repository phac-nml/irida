import React, { useEffect, useState } from "react";
import { FormattedMessage, IntlProvider } from "react-intl";
import PropTypes from "prop-types";
import { Icon, Modal } from "antd";
import { getTranslations } from "../../../../apis/translations/translations";

/**
 * Modal to indicate to the user what will happen when they delete
 * a column of entries from the linelist table.
 * @param {object} field - the MetadataTemplateField
 * @param {boolean} visible - whether the modal is visible
 * @param {function} hideModal - function to hide the modal
 * @param {function} removeEntries - function to complete the action of deleting entries
 * @returns {*}
 * @constructor
 */
export default function RemoveMetadataEntriesModal({
  field,
  visible,
  hideModal,
  removeEntries
}) {
  const [translations, setTranslations] = useState(null);

  useEffect(() => {
    getTranslations({
      page: "linelist",
      component: "RemoveMetadataEntriesModal"
    }).then(data => {
      setTranslations(data);
    });
  }, []);

  function removeColumnEntries() {
    removeEntries(field.headerName);
    hideModal();
  }

  return (
    <IntlProvider messages={translations}>
      <Modal
        title={
          <FormattedMessage
            id="RemoveMetadataEntriesModal_title"
            values={{
              name: field.headerName
            }}
          />
        }
        visible={visible}
        okType="danger"
        okText={<FormattedMessage id="RemoveMetadataEntriesModal_confirm" />}
        onOk={removeColumnEntries}
        onCancel={hideModal}
      >
        <p>
          <FormattedMessage
            id="RemoveMetadataEntriesModal_intro"
            values={{
              name: (
                <span style={{ textDecoration: "underline" }}>
                  {field.headerName}
                </span>
              )
            }}
          />
        </p>
        <p>
          <FormattedMessage
            id="RemoveMetadataEntriesModal_warning"
            values={{
              icon: <Icon type="lock" theme="twoTone" />
            }}
          />
        </p>
      </Modal>
    </IntlProvider>
  );
}

RemoveMetadataEntriesModal.propTypes = {
  field: PropTypes.object.isRequired,
  visible: PropTypes.bool.isRequired,
  hideModal: PropTypes.func.isRequired,
  removeEntries: PropTypes.func.isRequired
};
