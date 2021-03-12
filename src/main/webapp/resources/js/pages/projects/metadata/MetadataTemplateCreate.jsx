import React from "react";
import { Form, Input, Modal, Typography } from "antd";
import { navigate } from "@reach/router";
import DnDTable from "../../../components/ant.design/DnDTable";
import { HelpPopover } from "../../../components/popovers";
import { useDispatch } from "react-redux";
import { createNewMetadataTemplate } from "./templates/templatesSlice";
import { unwrapResult } from "@reduxjs/toolkit";

const { Text } = Typography;

export function MetadataTemplateCreate({ children, projectId, fields = [] }) {
  const dispatch = useDispatch();
  const [visible, setVisible] = React.useState(false);
  const [fieldsState, setFieldsState] = React.useState([]);
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (fields.length) {
      setFieldsState(fields.map((field) => ({ ...field, key: field.id })));
    }
  }, [fields]);

  const onOk = async () => {
    const values = await form.validateFields();
    try {
      values.fields = fieldsState;
      dispatch(createNewMetadataTemplate({ projectId, template: values }))
        .then(unwrapResult)
        .then((template) => {
          form.resetFields(Object.keys(values));
          setVisible(false);
          navigate(`templates/${template.identifier}`);
        });
      setVisible(false);
    } catch (e) {
      console.log(e);
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
          <Form.Item
            label={"NAME"}
            name="name"
            rules={[
              {
                required: true,
                message: "Please suplly a name for this template",
              },
            ]}
          >
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
                title: (
                  <>
                    <Text strong>Metadata Fields</Text>
                    <HelpPopover
                      content={
                        <div>
                          You can drag and drop to re-arrange the order of the
                          fields
                        </div>
                      }
                    />
                  </>
                ),
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
