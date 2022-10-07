import { Form, Input, Modal, notification, Select, Typography } from "antd";
import React from "react";
import { useAddSampleMetadataMutation } from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
import { addSampleMetadataField } from "../sampleSlice";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";

const { Title } = Typography;
const { TextArea } = Input;

export interface AddNewMetadataProps {
  children: React.ReactElement;
}

export interface AddMetadataRequest {
  fieldId: number;
  metadataTemplateField: string;
  metadataEntry: string;
  entryId: number;
  metadataRestriction: string;
  responseMessage: string;
}

export interface MetadataRoles {
  value: string;
  label: string;
}

/**
 * Function to render Add New Metadata Field modal
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function AddNewMetadata({ children }: AddNewMetadataProps): JSX.Element {
  const { sample, projectId } = useAppSelector((state) => state.sampleReducer);
  const [visible, setVisible] = React.useState<boolean>(false);
  const [addSampleMetadata] = useAddSampleMetadataMutation();
  const { roles: metadataRoles } = useMetadataRoles();
  const dispatch = useAppDispatch();

  const [form] = Form.useForm();
  useResetFormOnCloseModal({
    form,
    visible,
  });

  const addMetadata = () => {
    form.validateFields().then((values) => {
      // Add the new sample metadata and refetch the metadata
      addSampleMetadata({
        sampleId: sample.identifier,
        projectId,
        metadataField: values.metadata_field_name,
        metadataEntry: values.metadata_field_value,
        metadataRestriction: values.metadata_field_permission,
      })
        .unwrap()
        .then(
          ({
            fieldId,
            entryId,
            metadataEntry,
            metadataTemplateField,
            metadataRestriction,
            responseMessage,
          }: AddMetadataRequest) => {
            notification.success({ message: responseMessage });
            dispatch(
              addSampleMetadataField({
                fieldId,
                entryId,
                metadataTemplateField,
                metadataEntry,
                metadataRestriction,
              })
            );

            form.resetFields();
            setVisible(false);
          }
        )
        .catch(() => {
          notification.error({ message: i18n("SampleMetadata.add.error") });
        });
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
          okText={i18n("SampleMetadata.modal.btn.add")}
          cancelText={i18n("SampleMetadata.modal.btn.cancel")}
        >
          <Title level={4}>{i18n("SampleMetadata.add.modal.title")}</Title>
          <Form layout="vertical" form={form}>
            <Form.Item
              name="metadata_field_name"
              label={i18n("SampleMetadata.modal.fieldName")}
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
              <TextArea autoSize={true} />
            </Form.Item>
            <Form.Item
              name="metadata_field_permission"
              label={i18n("SampleMetadata.modal.restriction")}
              initialValue={metadataRoles ? metadataRoles[0].value : ""}
              rules={[
                {
                  required: true,
                  message: (
                    <div className="t-metadata-field-restriction-required">
                      {i18n("SampleMetadata.fieldRestriction.required")}
                    </div>
                  ),
                },
              ]}
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
