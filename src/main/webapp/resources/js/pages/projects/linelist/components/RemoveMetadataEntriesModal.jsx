import React from "react";
import PropTypes from "prop-types";
import { Icon, Modal } from "antd";

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
  function removeColumnEntries() {
    removeEntries(field.headerName);
    hideModal();
  }

  const introStrings = window.PAGE.i18n.RemoveMetadataEntriesModal_intro.split(
    "{name}"
  );
  const intro = (
    <p>
      {introStrings[0]}
      <span style={{ textDecoration: "underline" }}>{field.headerName}</span>
      {introStrings[1]}
    </p>
  );

  const warningStrings = window.PAGE.i18n.RemoveMetadataEntriesModal_warning.split(
    "{icon}"
  );
  const warning = (
    <p>
      {warningStrings[0]}
      <Icon type="lock" theme="twoTone" />
      {warningStrings[1]}
    </p>
  );

  return (
    <Modal
      title={window.PAGE.i18n.RemoveMetadataEntriesModal_title.replace(
        "{name}",
        field.headerName
      )}
      visible={visible}
      okType="danger"
      okText={window.PAGE.i18n.RemoveMetadataEntriesModal_confirm}
      onOk={removeColumnEntries}
      onCancel={hideModal}
    >
      {intro}
      {warning}
    </Modal>
  );
}

RemoveMetadataEntriesModal.propTypes = {
  field: PropTypes.object.isRequired,
  visible: PropTypes.bool.isRequired,
  hideModal: PropTypes.func.isRequired,
  removeEntries: PropTypes.func.isRequired
};
