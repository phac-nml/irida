import React, { useRef } from "react";
import { Checkbox, Form, Input, Modal } from "antd";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { IconEdit } from "../../../../components/icons/Icons";

/**
 * Component to add a button which will open a modal to create an announcement.
 * @param {function} createAnnouncement
 * @returns {*}
 * @constructor
 */
export function CreateNewAnnouncement({ createAnnouncement }) {
  const [visible, setVisible] = React.useState(false);
  const markdownRef = useRef();
  const [form] = Form.useForm();

  function saveMarkdown() {
    form.validateFields().then((values) => {
      const markdown = markdownRef.current.getMarkdown();
      const title = values.title;
      const priority = values.priority;

      createAnnouncement(title, markdown, priority);
    });
  }

  function displayModal() {
    Modal.confirm({
      title: i18n("CreateNewAnnouncement.title"),
      icon: <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />,
      width: "80%",
      content: (
        <Form layout="vertical" form={form}>
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
      ),
      okText: i18n("CreateNewAnnouncement.okBtn"),
      okButtonProps: {
        className: "t-submit-announcement",
      },
      onOk() {
        saveMarkdown();
      },
    });
  }

  return (
    <AddNewButton
      className="t-create-announcement"
      onClick={() => setVisible(true)}
      text={i18n("CreateNewAnnouncement.title")}
    />
  );
}
