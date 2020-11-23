import React, { useRef } from "react";
import { Checkbox, Form, Input, Modal, Space } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";

/**
 * React component to display an Ant Design Modal containing a form to update an announcement
 * @param {boolean} visible - whether the modal is open or not
 * @param {function} closeModal - the function to close the modal
 * @param {object} announcement - the announcement to be updated
 * @param {function} updateAnnouncement - the function to update an announcement and refresh the table
 * @returns {JSX.Element}
 * @constructor
 */
export function EditAnnouncementModal({
  visible,
  closeModal,
  announcement,
  updateAnnouncement,
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

      updateAnnouncement({
        id: announcement.id,
        title: title,
        message: markdown,
        priority: priority,
      });

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
          {i18n("EditAnnouncement.title")}
        </Space>
      }
      width="80%"
      okText={i18n("EditAnnouncement.okBtn")}
      onOk={saveAnnouncement}
      onCancel={onCancel}
    >
      <Form layout="vertical" form={form} initialValues={announcement}>
        <Form.Item
          name="title"
          label={i18n("EditAnnouncement.form.title")}
          rules={[
            {
              required: true,
              message: i18n("EditAnnouncement.form.error.title"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="message" label={i18n("EditAnnouncement.form.message")}>
          <MarkdownEditor ref={markdownRef} markdown={announcement.message} />
        </Form.Item>
        <Form.Item name="priority" valuePropName="checked">
          <Checkbox>{i18n("EditAnnouncement.form.priority")}</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  );
}
