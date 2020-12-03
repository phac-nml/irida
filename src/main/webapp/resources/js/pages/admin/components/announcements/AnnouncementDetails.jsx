import React, { useRef } from "react";
import {
  Button,
  Checkbox,
  Drawer,
  Form,
  Input,
  notification,
  Tabs,
  Space,
} from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import AnnouncementUserTable from "./AnnouncementUserTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";

/**
 * Render React component to show the details of an announcement.
 * @param {object} announcement - the announcement that is to be displayed.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementDetails({
  announcement,
  updateAnnouncement,
  deleteAnnouncement,
}) {
  const [visible, setVisible] = React.useState(false);
  const markdownRef = useRef();
  const [form] = Form.useForm();
  const { TabPane } = Tabs;
  const id = announcement.id;

  function saveAnnouncement() {
    form.validateFields().then(({ title, priority }) => {
      const markdown = markdownRef.current.getMarkdown();

      updateAnnouncement({
        id: announcement.id,
        title,
        message: markdown,
        priority,
      }).catch((message) => notification.error({ message }));
    });
  }

  return (
    <>
      <a onClick={() => setVisible(true)}>{announcement.title}</a>
      <Drawer
        title={i18n("announcement.control.details.title")}
        placement="right"
        closable={false}
        onClose={() => setVisible(false)}
        visible={visible}
        width={640}
      >
        <Tabs defaultActiveKey="1">
          <TabPane tab="Edit" key="1">
            <Form layout="vertical" form={form} initialValues={announcement}>
              <Form.Item
                name="title"
                label={i18n("AnnouncementModal.form.title")}
                rules={[
                  {
                    required: true,
                    message: i18n("AnnouncementModal.form.error.title"),
                  },
                ]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="message"
                label={i18n("AnnouncementModal.form.message")}
              >
                <MarkdownEditor
                  ref={markdownRef}
                  markdown={announcement ? announcement.message : null}
                />
              </Form.Item>
              <Form.Item name="priority" valuePropName="checked">
                <Checkbox>{i18n("AnnouncementModal.form.priority")}</Checkbox>
              </Form.Item>
              <Form.Item>
                <Space>
                  <Button
                    type="primary"
                    htmlType="submit"
                    onClick={saveAnnouncement}
                  >
                    {i18n("announcement.control.details.save")}
                  </Button>
                  <Button
                    htmlType="button"
                    onClick={() => deleteAnnouncement({ id })}
                  >
                    {i18n("announcement.control.details.delete")}
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </TabPane>
          <TabPane tab="Views" key="2">
            <PagedTableProvider
              url={setBaseUrl(
                `ajax/announcements/${announcement.id}/details/list`
              )}
            >
              <AnnouncementUserTable />
            </PagedTableProvider>
          </TabPane>
        </Tabs>
      </Drawer>
    </>
  );
}
