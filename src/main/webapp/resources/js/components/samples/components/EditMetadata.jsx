import { Form, Input, Modal, notification, Select, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useUpdateSampleMetadataMutation } from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
const { Title } = Typography;
import { editSampleMetadata, setEditSampleMetadata } from "../sampleSlice";

/**
 * Function to render Update Metadata Field modal
 * @param refetch - Function which refetches sample metadata for sample
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function EditMetadata({ refetch: refetchSampleMetadata, children }) {
  const dispatch = useDispatch();
  const [updateSampleMetadata] = useUpdateSampleMetadataMutation();
  const { roles: metadataRoles } = useMetadataRoles();
  const {
    editModalVisible,
    field,
    fieldId,
    entry,
    entryId,
    restriction,
    sample,
    projectId,
  } = useSelector((state) => state.sampleReducer);

  const [form] = Form.useForm();

  useResetFormOnCloseModal({
    form,
    visible: editModalVisible,
  });

  const updateMetadata = () => {
    form.validateFields().then((values) => {
      updateSampleMetadata({
        sampleId: sample.identifier,
        projectId,
        metadataFieldId: fieldId,
        metadataField: values.metadata_field_name,
        metadataEntryId: entryId,
        metadataEntry: values.metadata_field_value,
        metadataRestriction: values.metadata_field_permission,
      })
        .then((response) => {
          if (response.error) {
            notification.error({ message: response.error.data.error });
          } else {
            notification.success({ message: response.data.message });
            refetchSampleMetadata();
          }
          dispatch(
            editSampleMetadata({
              fieldId,
              entryId,
              field: values.metadata_field_name,
              entry: values.metadata_field_value,
              restriction: values.metadata_field_permission,
            })
          );
          form.resetFields();
          dispatch(setEditSampleMetadata({ editModalVisible: false }));
        })
        .catch((error) => {
          notification.error({ message: error });
        });
    });
  };

  return (
    <>
      {editModalVisible ? (
        <Modal
          className="t-edit-sample-metadata"
          onCancel={() =>
            dispatch(setEditSampleMetadata({ editModalVisible: false }))
          }
          visible={editModalVisible}
          onOk={updateMetadata}
          okText={i18n("SampleMetadata.modal.btn.update")}
          cancelText={i18n("SampleMetadata.modal.btn.cancel")}
        >
          <Title level={4}>{i18n("SampleMetadata.edit.modal.title")}</Title>
          <Form layout="vertical" form={form}>
            <Form.Item
              name="metadata_field_name"
              label={i18n("SampleMetadata.modal.fieldName")}
              initialValue={field}
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
              initialValue={entry}
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
              initialValue={restriction ? restriction : "LEVEL_1"}
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
