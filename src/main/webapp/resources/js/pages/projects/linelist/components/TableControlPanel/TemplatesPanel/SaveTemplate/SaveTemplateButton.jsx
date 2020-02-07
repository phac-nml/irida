import React from "react";

import PropTypes from "prop-types";
import { Button, Form, Modal } from "antd";
import { SPACE_XS } from "../../../../../../../styles/spacing";
import { SaveOutlined } from "@ant-design/icons";
import { SaveTemplateForm } from "./SaveTemplateForm";

/**
 * This components is part of the TemplateSelect.  Displays a save button
 * if the "All Field" or no template is selected and columns modified.
 */
export function SaveTemplateButton({ disabled, template, templates, saveTemplate }) {
  const [form] = Form.useForm();

  function saveTemplateData() {
    const name = form.getFieldValue("name");
    const overwrite = form.getFieldValue("overwrite");
    const id = template.id > 0 && overwrite ? template.id : null;
    saveTemplate(name, template.modified, id);
  }

  function showSaveModal() {
    form.resetFields();
    /*
    Update the confirm button on the modal when the state of the name
    changes.
     */
    function updateValidity(validity) {
      modal.update({
        okButtonProps: {
          disabled: !validity
        }
      })
    }

    const modal = Modal.confirm({
      title: i18n("SaveTemplateModal.title"),
      icon: <SaveOutlined />,
      content: (
        <SaveTemplateForm
          form={form}
          templates={templates}
          template={template}
          setValidity={updateValidity}
        />
      ),
      okButtonProps: {
        disabled: false
      },
      okText: i18n("SaveTemplateModal.okText"),
      onOk() {
        saveTemplateData();
      },
      cancelText: i18n("SaveTemplateModal.cancelText")
    });
  }

  return (
    <Button
      className="t-template-save-btn"
      type="primary"
      ghost
      disabled={disabled}
      onClick={showSaveModal}
      style={{ marginLeft: SPACE_XS }}
    >
      {i18n("SaveTemplateButton.label")}
    </Button>
  );
}

SaveTemplateButton.propTypes = {
  template: PropTypes.object
};
