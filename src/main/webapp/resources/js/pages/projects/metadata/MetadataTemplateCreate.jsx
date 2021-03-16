import React from "react";
import { Form, Input, Modal, notification, Typography } from "antd";
import { navigate } from "@reach/router";
import DnDTable from "../../../components/ant.design/DnDTable";
import { HelpPopover } from "../../../components/popovers";
import { useDispatch, useSelector } from "react-redux";
import { createNewMetadataTemplate } from "./redux/templates/templatesSlice";
import { unwrapResult } from "@reduxjs/toolkit";

const { Text } = Typography;

/**
 * Component to create a new metadata template with a list of metadata fields
 *
 * @param {JSX.Element} children
 * @param {number} projectId - identifier for the current project
 * @param {Object[]} fields - list of metadata fields for the template
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataTemplateCreate({ children, projectId, fields = [] }) {
  const dispatch = useDispatch();
  const { templates } = useSelector((state) => state.templates);
  const [names, setNames] = React.useState(undefined);
  const [visible, setVisible] = React.useState(false);
  const [fieldsState, setFieldsState] = React.useState([]);
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (fields.length) {
      setFieldsState(fields.map((field) => ({ ...field, key: field.id })));
    }
  }, [fields]);

  React.useEffect(() => {
    if (templates) {
      const templateNames = new Set(
        templates.map((template) => template.label)
      );
      setNames(templateNames);
    }
  }, [templates]);

  const onOk = async () => {
    const values = await form.validateFields();
    values.fields = fieldsState;
    dispatch(createNewMetadataTemplate({ projectId, template: values }))
      .then(unwrapResult)
      .then((template) => {
        form.resetFields(Object.keys(values));
        setVisible(false);
        navigate(`templates/${template.identifier}`);
        setVisible(false);
      })
      .catch((message) => notification.error({ message }));
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
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (value.length && names.has(value)) {
                    return Promise.reject(
                      new Error("A template with this name already exists")
                    );
                  }
                  return Promise.resolve();
                },
              }),
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
