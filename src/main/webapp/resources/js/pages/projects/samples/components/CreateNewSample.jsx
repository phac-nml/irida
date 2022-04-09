import React from "react";
import { Form, Input, Modal } from "antd";
import { validateSampleName } from "../../../../apis/projects/samples";

/**
 * React component to create a new sample within a project.
 * @returns {JSX.Element}
 * @constructor
 */
export default function CreateNewSample({ visible, onCreate, onCancel }) {
  const [form] = Form.useForm();
  const nameRef = React.useRef();

  React.useEffect(() => {
    if (visible) {
      nameRef.current.focus();
    } else {
      form.resetFields();
    }
  }, [form, visible]);

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  /**
   * This is used by Ant Design's input  validation system to server side validate the
   * sample name.  This includes name length, special characters, and if the name is already used.
   * @param rule
   * @param {string} value - the current value of the input
   * @returns {Promise<void>}
   */
  const validateName = async (value) => {
    await validateSampleName(value).then((response) => {
      if (response.status === "success") {
        return Promise.resolve();
      } else {
        return Promise.reject(response.help);
      }
    });
  };

  return (
    <Modal
      visible={visible}
      onCancel={handleCancel}
      title={i18n("AddSample.title")}
    >
      <Form
        form={form}
        initialValues={{ name: "", organism: "" }}
        layout="vertical"
      >
        <Form.Item
          name="name"
          label={i18n("AddSample.name")}
          rules={[
            ({}) => ({
              validator(_, value) {
                return validateName(value);
              },
            }),
          ]}
        >
          <Input ref={nameRef} className={"t-sample-name"} value={name} />
        </Form.Item>
      </Form>
    </Modal>
  );
}
