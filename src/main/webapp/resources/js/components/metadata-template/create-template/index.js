import React, { useState } from "react";
import { Button, Form, Input, Modal } from "antd";
import { createNewMetadataTemplate } from "../../../apis/metadata/metadata-templates";
import { useLocation, useNavigate } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * React component to render the ability to create a metadata template.
 * Name and description only.
 * @returns {JSX.Element}
 * @constructor
 */
export default function CreateTemplate() {
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const location = useLocation();

  /**
   * Close the modal and reset the form if it is not needed.
   */
  const closeModal = () => {
    form.resetFields();
    setVisible(false);
  };

  /**
   * Submit the form to create the template.  Once the new template is created
   * redirect the user to the specific template page so they can add fields.
   */
  const submitForm = () => {
    form.validateFields().then((values) => {
      createNewMetadataTemplate(values).then((id) =>
        navigate(setBaseUrl(`${location.pathname}/${id}`))
      );
    });
  };

  return (
    <div>
      <Button onClick={() => setVisible(true)}>
        {i18n("ProjectMetadataTemplates.create")}
      </Button>
      <Modal
        title={i18n("CreateMetadataTemplate.title")}
        visible={visible}
        onCancel={closeModal}
        okText={i18n("CreateMetadataTemplate.okText")}
        onOk={submitForm}
      >
        <Form layout="vertical" form={form}>
          <Form.Item
            label={i18n("CreateMetadataTemplate.name")}
            name="name"
            rules={[{ required: true, message: "Hey!  You need a name!" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("CreateMetadataTemplate.description")}
            name="description"
          >
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
