import React, { useRef } from "react";
import { Checkbox, Form, Input, Modal } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";

export function CreateAnnouncementModal({
  visible,
  closeModal,
  createAnnouncement,
}) {
  const markdownRef = useRef();
  const [form] = Form.useForm();

  function saveMarkdown() {
    form.validateFields().then((values) => {
      const markdown = markdownRef.current.getMarkdown();
      const title = values.title;
      const priority = values.priority;

      createAnnouncement(title, markdown, priority);
      closeModal();
    });
  }
  return (
    <Modal
      visible={visible}
      title={
        <>
          <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
          {i18n("CreateNewAnnouncement.title")}
        </>
      }
      width="80%"
      okText={i18n("CreateNewAnnouncement.okBtn")}
      okButtonProps={{
        className: "t-submit-announcement",
      }}
      onOk={saveMarkdown}
      onCancel={closeModal}
    >
      <Form layout="vertical" form={form} preserve={false}>
        <Form.Item
          name="title"
          label="Title"
          rules={[
            {
              required: true,
              message: "Please input a title",
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="message" label="Message">
          <MarkdownEditor ref={markdownRef} />
        </Form.Item>
        <Form.Item name="priority" label="Priority" valuePropName="checked">
          <Checkbox />
        </Form.Item>
      </Form>
    </Modal>
  );
}
