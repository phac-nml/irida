import React from "react";
import {
  Button,
  Dropdown,
  Form,
  Input,
  Menu,
  Modal,
  Space,
  Tooltip,
} from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import { IconExclamationCircle } from "../../components/icons/Icons";
import { SPACE_XS } from "../../styles/spacing";
import { yellow6 } from "../../styles/colors";

export function ParametersModal({ visible, closeModal }) {
  const { parameterSet } = useLaunchState();
  const { dispatchUseModifiedParameters } = useLaunchDispatch();

  const [fields, setFields] = React.useState([]);
  const [form] = Form.useForm();

  React.useEffect(() => {
    setFields(
      parameterSet.parameters.map((parameter) => ({
        name: [parameter.name],
        value: parameter.value,
      }))
    );
  }, [parameterSet]);

  const onOk = () => {
    form.validateFields().then((values) => {
      console.log(values);
      closeModal();
    });
  };

  const useModifiedParameters = () => {
    form.validateFields().then((values) => {
      dispatchUseModifiedParameters(values);
      closeModal();
    });
  };

  const saveModifiedParameters = () => {
    form.validateFields().then((values) => {
      // dispatchUseModifiedParameters(values);
    });
  };

  const menu = (
    <Menu>
      {parameterSet.id !== 0 ? <Menu.Item key="copy">Save</Menu.Item> : null}
      <Menu.Item key="save">Save as ...</Menu.Item>
    </Menu>
  );

  return (
    <Modal
      visible={visible}
      keyboard={false}
      maskClosable={false}
      onCancel={closeModal}
      onOk={onOk}
      title={parameterSet.label}
      width={600}
      footer={
        <div style={{ display: "flex", flexDirection: "row-reverse" }}>
          <Space>
            <Button onClick={closeModal}>Cancel</Button>
            <Dropdown.Button
              type="primary"
              onClick={useModifiedParameters}
              overlay={menu}
              buttonsRender={([leftButton, rightButton]) => [
                <Tooltip
                  title={
                    <div>
                      <IconExclamationCircle
                        style={{ marginRight: SPACE_XS, color: yellow6 }}
                      />
                      PARAMETERS WILL NOT BE SAVED
                    </div>
                  }
                  key="leftButton"
                >
                  {leftButton}
                </Tooltip>,
                rightButton,
              ]}
            >
              <div>Use these parameters</div>
            </Dropdown.Button>
          </Space>
        </div>
      }
    >
      <section style={{ maxHeight: 600, overflow: "auto" }}>
        <Form form={form} layout="vertical" fields={fields}>
          {parameterSet.parameters.map((parameter) => (
            <Form.Item
              key={parameter.name}
              label={parameter.label}
              name={parameter.name}
            >
              <Input />
            </Form.Item>
          ))}
        </Form>
      </section>
    </Modal>
  );
}
