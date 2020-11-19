import React, { useRef } from "react";
import { Checkbox, Form, Input, Modal, Space } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";

/**
 * React component to display an Ant Design Modal containing a form to create a new announcement
 * @param {boolean} visible - whether the modal is open or not
 * @param {function} closeModal - the function to close the modal
 * @param {function} createAnnouncement - the function to create a new announcement and refresh the table
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateAnnouncementModal({
  visible,
  closeModal,
  createAnnouncement,
}) {
  const markdownRef = useRef();
  const [form] = Form.useForm();

  const onCancel = () => {
    form.resetFields();
    closeModal();
  };

  function saveAnnouncement() {
    form.validateFields().then((values) => {
      const markdown = markdownRef.current.getMarkdown();
      const title = values.title;
      const priority = values.priority;

      createAnnouncement(title, markdown, priority);

      form.resetFields();
      closeModal();
    });
  }
  return (
    <Modal
      visible={visible}
      title={
        <Space>
          <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
          {i18n("CreateNewAnnouncement.title")}
        </Space>
      }
      width="80%"
      okText={i18n("CreateNewAnnouncement.okBtn")}
      okButtonProps={{
        className: "t-submit-announcement",
      }}
      onOk={saveAnnouncement}
      onCancel={onCancel}
    >
      <Form layout="vertical" form={form}>
        <Form.Item
          name="title"
          label={i18n("CreateNewAnnouncement.form.title")}
          rules={[
            {
              required: true,
              message: i18n("CreateNewAnnouncement.form.error.title"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="message"
          label={i18n("CreateNewAnnouncement.form.message")}
        >
          <MarkdownEditor ref={markdownRef} />
        </Form.Item>
        <Form.Item name="priority" valuePropName="checked">
          <Checkbox>{i18n("CreateNewAnnouncement.form.priority")}</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  );
}
