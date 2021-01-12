import React, { useRef } from "react";
import { Button, Checkbox, Form, Input, notification } from "antd";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";
import { useVisibility } from "../../../../contexts/visibility-context";

/**
 * Render React component to show the form to create or update an announcement.
 * @param {object} announcement - the announcement that is to be edited.
 * @param {function} createAnnouncement - the function that creates an announcement.
 * @param {function} updateAnnouncement - the function that updates an announcement.
 * @param {function} deleteAnnouncement - the function that deletes an announcement.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementForm({
  announcement,
  createAnnouncement,
  updateAnnouncement,
}) {
  const [, setVisibility] = useVisibility();
  const markdownRef = useRef();
  const [form] = Form.useForm();

  function saveAnnouncement() {
    form.validateFields().then(({ title, priority }) => {
      const markdown = markdownRef.current.getMarkdown();

      if (announcement) {
        updateAnnouncement({
          id: announcement.id,
          title,
          message: markdown,
          priority,
        })
          .then(() => {
            setVisibility(false);
          })
          .catch((message) => notification.error({ message }));
      } else {
        createAnnouncement(title, markdown, priority)
          .then(() => {
            form.resetFields();
            setVisibility(false);
          })
          .catch((message) => notification.error({ message }));
      }
    });
  }

  return (
    <Form layout="vertical" form={form} initialValues={announcement}>
      <Form.Item
        name="title"
        label={i18n("AnnouncementForm.form.title")}
        rules={[
          {
            required: true,
            message: i18n("AnnouncementForm.form.error.title.required"),
          },
          {
            max: 255,
            message: i18n("AnnouncementForm.form.error.title.maximum"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item name="message" label={i18n("AnnouncementForm.form.message")}>
        <MarkdownEditor
          ref={markdownRef}
          markdown={announcement ? announcement.message : null}
        />
      </Form.Item>
      <Form.Item name="priority" valuePropName="checked">
        <Checkbox>{i18n("AnnouncementForm.form.priority")}</Checkbox>
      </Form.Item>
      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          onClick={saveAnnouncement}
          className="t-submit-announcement"
        >
          {announcement
            ? i18n("AnnouncementForm.edit.button")
            : i18n("AnnouncementForm.create.button")}
        </Button>
      </Form.Item>
    </Form>
  );
}
