import React, { useRef } from "react";
import { Button, Checkbox, Form, Input, notification, Tabs, Space } from "antd";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";

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
  deleteAnnouncement,
}) {
  const markdownRef = useRef();
  const [form] = Form.useForm();
  const id = announcement ? announcement.id : null;

  function saveAnnouncement() {
    form.validateFields().then(({ title, priority }) => {
      const markdown = markdownRef.current.getMarkdown();

      if (announcement) {
        updateAnnouncement({
          id: announcement.id,
          title,
          message: markdown,
          priority,
        }).catch((message) => notification.error({ message }));
      } else {
        createAnnouncement(title, markdown, priority).catch((message) =>
          notification.error({ message })
        );
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
            message: i18n("AnnouncementForm.form.error.title"),
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
        {announcement ? (
          <Space>
            <Button type="primary" htmlType="submit" onClick={saveAnnouncement}>
              {i18n("AnnouncementForm.edit.button")}
            </Button>
            <Button
              htmlType="button"
              onClick={() => deleteAnnouncement({ id })}
            >
              {i18n("AnnouncementForm.delete.button")}
            </Button>
          </Space>
        ) : (
          <Space>
            <Button type="primary" htmlType="submit" onClick={saveAnnouncement}>
              {i18n("AnnouncementForm.create.button")}
            </Button>
          </Space>
        )}
      </Form.Item>
    </Form>
  );
}
