import { Form, Input, Modal, notification, Select, Typography } from "antd";
import React from "react";
import {
  useUpdateSampleMetadataMutation,
  useGetMetadataFieldRestrictionQuery,
} from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
const { Title } = Typography;

/**
 * Function to render Update Metadata Field modal
 * @param projectId - The project identifier
 * @param metadataField - The metadata field label
 * @param metadataFieldId - The metadata field identifier
 * @param metadataEntryId - The metadata entry identifier
 * @param metadataEntry - The metadata entry value
 * @param refetch - Function which refetches sample metadata for sample
 * @param visible - Whether or not modal should be displayed
 * @param refetch - Function to refetch sample metadata after changes are made
 * @param onCancel - Function to run if user clicks the cancel button
 * @param onOk - Function to run if user clicks the update button
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function EditMetadata({
  sampleId,
  projectId,
  metadataField,
  metadataFieldId,
  metadataEntryId,
  metadataEntry,
  refetch: refetchSampleMetadata,
  visible,
  onCancel,
  onOk,
  children,
}) {
  const {
    data: metadataFieldRestriction = {},
    isLoading,
    refetch: refetchMetadataRestriction,
  } = useGetMetadataFieldRestrictionQuery(
    {
      projectId,
      metadataFieldId: metadataFieldId,
    },
    { skip: !metadataFieldId }
  );

  const [updateSampleMetadata] = useUpdateSampleMetadataMutation();
  const { roles: metadataRoles } = useMetadataRoles();

  React.useEffect(() => {}, [visible]);

  const [form] = Form.useForm();

  useResetFormOnCloseModal({
    form,
    visible,
  });

  const updateMetadata = () => {
    form.validateFields().then((values) => {
      updateSampleMetadata({
        sampleId,
        projectId,
        metadataFieldId: metadataFieldId,
        metadataField: values.metadata_field_name,
        metadataEntryId: metadataEntryId,
        metadataEntry: values.metadata_field_value,
        metadataRestriction: values.metadata_field_permission,
      })
        .then((response) => {
          if (response.error) {
            notification.error({ message: response.error.data.error });
          } else {
            notification.success({ message: response.data.message });
            refetchSampleMetadata();
            refetchMetadataRestriction();
          }
          form.resetFields();
          onOk();
        })
        .catch((error) => {
          notification.error({ message: error });
        });
    });
  };

  return (
    <>
      {!isLoading && visible ? (
        <Modal
          className="t-edit-sample-metadata"
          onCancel={onCancel}
          visible={visible}
          onOk={updateMetadata}
          okText={i18n("SampleMetadata.modal.btn.update")}
          cancelText={i18n("SampleMetadata.modal.btn.cancel")}
        >
          <Title level={4}>{i18n("SampleMetadata.edit.modal.title")}</Title>
          <Form layout="vertical" form={form}>
            <Form.Item
              name="metadata_field_name"
              label={i18n("SampleMetadata.modal.fieldName")}
              initialValue={metadataField}
              rules={[
                {
                  required: true,
                  message: (
                    <div className="t-metadata-field-name-required">
                      {i18n("SampleMetadata.fieldName.required")}
                    </div>
                  ),
                },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="metadata_field_value"
              label={i18n("SampleMetadata.modal.fieldValue")}
              initialValue={metadataEntry}
              rules={[
                {
                  required: true,
                  message: (
                    <div className="t-metadata-field-value-required">
                      {i18n("SampleMetadata.fieldValue.required")}
                    </div>
                  ),
                },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="metadata_field_permission"
              label={i18n("SampleMetadata.modal.restriction")}
              initialValue={
                !isLoading &&
                (metadataFieldRestriction &&
                Object.keys(metadataFieldRestriction).length === 0
                  ? "LEVEL_1"
                  : metadataFieldRestriction.level)
              }
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
