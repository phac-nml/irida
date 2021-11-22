import { Form, Input, Modal, Typography } from "antd";
import React from "react";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";
import { RoleSelect } from "../../roles/RoleSelect";

const { Title } = Typography;

/**
 * Function to render Add New Metadata Field modal
 * @param sampleId - identifier for a sample
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function AddNewMetadata({ sampleId, children }) {
  const [visible, setVisible] = React.useState(false);
  const { roles: metadataRoles } = useMetadataRoles();

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
              <RoleSelect roles={metadataRoles} />
            </Form.Item>
          </Form>
        </Modal>
      ) : null}
    </>
  );
}
