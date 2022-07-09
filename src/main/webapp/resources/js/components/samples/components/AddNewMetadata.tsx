import { Form, Input, Modal, notification, Select, Typography } from "antd";
import React from "react";
import { useAddSampleMetadataMutation } from "../../../apis/samples/samples";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
const { Title } = Typography;
import { addSampleMetadataField } from "../sampleSlice";
import { RootStateOrAny, useDispatch, useSelector } from "react-redux";

export interface AddNewMetadataProps {
  children: React.ReactElement;
}

/**
 * Function to render Add New Metadata Field modal
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function AddNewMetadata({ children }: AddNewMetadataProps): JSX.Element {
  const { sample, projectId } = useSelector(
    (state: RootStateOrAny) => state.sampleReducer
  );
  const [visible, setVisible] = React.useState(false);
  const [addSampleMetadata] = useAddSampleMetadataMutation();
  const { roles: metadataRoles } = useMetadataRoles();
  const dispatch = useDispatch();

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
        .then((response) => {
          const resData = response.data;
          if (response.error) {
            notification.error({ message: response.error.data.error });
          } else {
            notification.success({ message: response.data.responseMessage });
            dispatch(
              addSampleMetadataField({
                fieldId: resData.fieldId,
                entryId: resData.entryId,
                metadataTemplateField: resData.metadataTemplateField,
                metadataEntry: resData.metadataEntry,
                metadataRestriction: resData.metadataRestriction,
              })
            );
          }
          form.resetFields();
          setVisible(false);
        })
        .catch((error) => {
          notification.error({ message: error });
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
              <Input />
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
                {metadataRoles?.map(
                  (role: { value: string; label: string }) => (
                    <Select.Option
                      className={`t-${role.value}`}
                      value={role.value}
                      key={role.value}
                    >
                      {role.label}
                    </Select.Option>
                  )
                )}
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      ) : null}
    </>
  );
}
