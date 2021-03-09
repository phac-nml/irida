import React from "react";
import { Form, Input, Modal, Typography } from "antd";
import { createProjectMetadataTemplate } from "../../../apis/metadata/metadata-templates";
import { navigate } from "@reach/router";
import DnDTable from "../../../components/ant.design/DnDTable";

const { Text } = Typography;

export function MetadataTemplateCreate({ children, fields = [] }) {
  const [visible, setVisible] = React.useState(false);
  const [fieldsState, setFieldsState] = React.useState([]);
  const [form] = Form.useForm();

  React.useEffect(() => {
    setFieldsState([...fields]);
  }, [fields]);

  const onOk = async () => {
    const values = await form.validateFields();
    try {
      values.fields = fieldsState;
      await createTemplate(values);
      form.resetFields(Object.keys(values));
      setVisible(false);
    } catch (e) {
      console.log(e);
    }
  };

  const createTemplate = async (details) => {
    try {
      const template = await createProjectMetadataTemplate(
        window.project.id,
        details
      );
      navigate(`templates/${template.id}`);
    } catch (e) {
      return Promise.reject(e);
    }
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Modal
        title={"CREATE NEW METADATA TEMPLATE"}
        visible={visible}
        onCancel={() => setVisible(false)}
        okText={"CREATE TEMPLATE"}
        onOk={onOk}
      >
        <Form layout="vertical" form={form}>
          <Form.Item label={"NAME"} name="name">
            <Input />
          </Form.Item>
          <Form.Item label={"DESCRIPTION"} name="description">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
        {fieldsState.length > 0 && (
          <DnDTable
            size="small"
            data={fieldsState}
            onRowUpdate={setFieldsState}
            columns={[
              {
                title: <Text strong>Metadata Fields</Text>,
                dataIndex: "label",
                key: "label",
              },
            ]}
          />
        )}
      </Modal>
    </>
  );
}
