import React from "react";
import {
  Button,
  Empty,
  Form,
  Input,
  List,
  Modal,
  Select,
  Typography,
} from "antd";

const { Option } = Select;

const { Title } = Typography;
/**
 * React component to display metadata associated with a sample
 *
 * @param {array} metadata
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata({ metadata }) {
  const [visible, setVisible] = React.useState(false);
  return (
    <>
      <Button onClick={() => setVisible(true)}>
        {i18n("SampleMetadata.addNewMetadata")}
      </Button>
      <Modal
        className="t-add-metadata-field"
        onCancel={() => setVisible(false)}
        visible={visible}
        onOk={() => setVisible(false)}
      >
        <Title level={4}>{i18n("SampleMetadata.modal.title")}</Title>
        <Form layout="vertical">
          <Form.Item
            name="metadata_field_name"
            label={i18n("SampleMetadata.modal.fieldName")}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="metadata_field_value"
            label={i18n("SampleMetadata.modal.fieldValue")}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="metadata_field_permission"
            label={i18n("SampleMetadata.modal.permission")}
          >
            <Select
              defaultValue="1"
              style={{ width: "100%" }}
              onChange={(e) =>
                console.log("Changed to LEVEL " + e + " metadata permission.")
              }
            >
              <Option value="1">{i18n("SampleMetadata.modal.level1")}</Option>
              <Option value="2">{i18n("SampleMetadata.modal.level2")}</Option>
              <Option value="3">{i18n("SampleMetadata.modal.level3")}</Option>
              <Option value="4">{i18n("SampleMetadata.modal.level4")}</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
      <div>
        {Object.keys(metadata).length ? (
          <List
            itemLayout="horizontal"
            dataSource={Object.keys(metadata).sort((a, b) =>
              a.localeCompare(b)
            )}
            renderItem={(item) => (
              <List.Item className="t-sample-details-metadata-item">
                <List.Item.Meta
                  title={
                    <span className="t-sample-details-metadata__field">
                      {item}
                    </span>
                  }
                  description={
                    <span className="t-sample-details-metadata__entry">
                      {metadata[item].value}
                    </span>
                  }
                />
              </List.Item>
            )}
          />
        ) : (
          <Empty description={i18n("SampleDetails.no-metadata")} />
        )}
      </div>
    </>
  );
}
