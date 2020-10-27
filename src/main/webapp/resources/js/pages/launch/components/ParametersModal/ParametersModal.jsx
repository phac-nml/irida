import React from "react";
import {
  Alert,
  Button,
  Dropdown,
  Form,
  Input,
  Menu,
  Modal,
  Space,
  Tooltip,
} from "antd";
import { useLaunchDispatch, useLaunchState } from "../../launch-context";
import { IconExclamationCircle } from "../../../../components/icons/Icons";
import { SPACE_SM, SPACE_XS } from "../../../../styles/spacing";
import { grey9, yellow6 } from "../../../../styles/colors";

export function ParametersModal({ visible, closeModal }) {
  const { parameterSet } = useLaunchState();
  const {
    dispatchUseModifiedParameters,
    dispatchOverwriteParameterSave,
  } = useLaunchDispatch();

  const [fields, setFields] = React.useState([]);
  const [showSave, setShowSave] = React.useState(false);
  const [showSaveAs, setShowSaveAs] = React.useState(false);

  const [form] = Form.useForm();

  React.useEffect(() => {
    setFields(
      parameterSet.parameters.map((parameter) => ({
        name: [parameter.name],
        value: parameter.value,
      }))
    );
  }, [parameterSet]);

  const saveParameters = (name) => {
    form.validateFields().then((values) => console.log(values));
  };

  const onOk = () => {
    form.validateFields().then((values) => {
      console.log(values);
      closeModal();
    });
  };

  const useModifiedParameters = () => {
    form.validateFields().then((values) => {
      if (form.isFieldsTouched()) {
        dispatchUseModifiedParameters(values);
      }
      closeModal();
    });
  };

  const saveModifiedParameters = () => {
    form.validateFields().then((values) => {
      // dispatchUseModifiedParameters(values);
    });
  };

  /**
   * Overwrite an existing saved parameter set with new values.
   */
  const overwriteParameterSet = () => {
    form.validateFields().then(dispatchOverwriteParameterSave);
  };

  const menu = (
    <Menu>
      {parameterSet.id !== 0 ? (
        <Menu.Item key="copy" onClick={() => setShowSave(true)}>
          Save
        </Menu.Item>
      ) : null}
      <Menu.Item key="save" onClick={() => setShowSaveAs(true)}>
        Save as ...
      </Menu.Item>
    </Menu>
  );

  return (
    <Modal
      bodyStyle={{
        padding: 0,
      }}
      visible={visible}
      keyboard={false}
      maskClosable={false}
      onCancel={closeModal}
      onOk={onOk}
      title={parameterSet.label}
      width={600}
      footer={
        <Space style={{ width: `100%` }} direction="vertical">
          {showSaveAs || showSave ? null : (
            <div style={{ display: "flex", flexDirection: "row-reverse" }}>
              <Space>
                <Button onClick={closeModal}>Cancel</Button>
                <Dropdown.Button
                  type="primary"
                  overlay={menu}
                  buttonsRender={([leftButton, rightButton]) => [
                    <Tooltip
                      color={yellow6}
                      title={
                        <span style={{ color: grey9 }}>
                          <IconExclamationCircle
                            style={{ marginRight: SPACE_XS }}
                          />
                          Parameters will not be saved
                        </span>
                      }
                      key="leftButton"
                    >
                      {React.cloneElement(leftButton, {
                        onClick: useModifiedParameters,
                      })}
                    </Tooltip>,
                    rightButton,
                  ]}
                >
                  <div>Use these parameters</div>
                </Dropdown.Button>
              </Space>
            </div>
          )}
          {showSaveAs ? (
            <Form layout="vertical">
              <Form.Item label={"NAME"}>
                <Input defaultValue={`${parameterSet.label} (copy)`} />
              </Form.Item>
              <Button onClick={() => setShowSaveAs(false)}>Cancel</Button>
              <Button onClick={() => {}}>Save</Button>
            </Form>
          ) : null}
          {showSave ? (
            <Form layout="vertical">
              <Alert
                style={{ textAlign: "left", marginBottom: SPACE_SM }}
                type="warning"
                showIcon
                message="This will overwrite the current parameter set"
                description={
                  "EVerytihng else will magically be the same. Have fun and do evil things."
                }
              />
              <Button onClick={() => setShowSave(false)}>Cancel</Button>
              <Button onClick={() => overwriteParameterSet()}>Save</Button>
            </Form>
          ) : null}
        </Space>
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
