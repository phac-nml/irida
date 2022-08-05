import {
  Form,
  Input,
  Modal,
  notification,
  Popover,
  Select,
  Typography,
} from "antd";
import React from "react";
import { RootStateOrAny, useDispatch, useSelector } from "react-redux";
import { useUpdateSampleMetadataMutation } from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
import { editSampleMetadata, setEditSampleMetadata } from "../sampleSlice";
import { IconWarningOutlined } from "../../icons/Icons";
import { grey6 } from "../../../styles/colors";
import { WarningAlert } from "../../alerts";

const { Title } = Typography;

export interface MetadataRoles {
  value: string;
  label: string;
}

/**
 * Function to render Update Metadata Field modal
 * @returns {JSX.Element}
 * @constructor
 */
export function EditMetadata() {
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
  } = useSelector((state: RootStateOrAny) => state.sampleReducer);

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
        .unwrap()
        .then(({ message }) => {
          notification.success({ message });
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
          notification.error({ message: error.data.error });
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
              label={
                <span>
                  {i18n("SampleMetadata.modal.fieldName")}
                  <Popover
                    content={
                      <WarningAlert
                        message={i18n(
                          "SampleMetadata.edit.modal.warning.message"
                        )}
                      />
                    }
                  >
                    <IconWarningOutlined
                      style={{
                        margin: "0 4px",
                        cursor: "help",
                        color: grey6,
                      }}
                    />
                  </Popover>
                </span>
              }
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
                {metadataRoles?.map((role: MetadataRoles) => (
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
