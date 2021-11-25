import { Form, Input, Modal, notification, Select, Typography } from "antd";
import React from "react";
import { useAddSampleMetadataMutation } from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
const { Title } = Typography;

/**
 * Function to render Add New Metadata Field modal
 * @param sampleId - identifier for a sample
 * @param refetch - Function which refetches sample metadata for sample
 * @param projectId - The project identifier
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function AddNewMetadata({ sampleId, refetch, projectId, children }) {
  const [visible, setVisible] = React.useState(false);
  const [addSampleMetadata] = useAddSampleMetadataMutation();
  const { roles: metadataRoles } = useMetadataRoles();

  const [form] = Form.useForm();
  useResetFormOnCloseModal({
    form,
    visible,
  });

  const addMetadata = () => {
    const values = form.getFieldsValue();

    // Add the new sample metadata and refetch the metadata
    addSampleMetadata({
      sampleId,
      projectId,
      metadataField: values.metadata_field_name,
      metadataEntry: values.metadata_field_value,
      metadataFieldPermission: values.metadata_field_permission,
    })
      .then((response) => {
        if (response.error) {
          notification.error({ message: response.error.data.error });
        } else {
          notification.success({ message: response.data.message });
          refetch();
        }
        form.resetFields();
        setVisible(false);
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible ? (
        <Modal
          className="t-add-metadata-field"
          onCancel={() => setVisible(false)}
          visible={visible}
          onOk={addMetadata}
        >
          <Title level={4}>{i18n("SampleMetadata.modal.title")}</Title>
          <Form layout="vertical" form={form}>
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
              <Select style={{ width: "100%" }}>
                {metadataRoles?.map((role) => (
                  <Select.Option
                    className={`t-${role.value}`}
                    value={role.value}
                    key={role.value}
                  >
                    {role.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      ) : null}
    </>
  );
}
