import React from "react";
import Form from "antd/es/form";
import { Button, Select, Space, Typography } from "antd";
import { IconEdit } from "../../components/icons/Icons";
import ParametersModal from "./components/ParametersModal";
import { useLaunchDispatch, useLaunchState } from "./launch-context";

export function SavedParameters({ form }) {
  const { savedPipelineParameters, parameterSet } = useLaunchState();
  const { dispatchUseSavedParameterSet } = useLaunchDispatch();
  const [visible, setVisible] = React.useState(false);

  React.useEffect(() => {
    form.setFieldsValue({ parameterSet: parameterSet.id });
  }, [form, parameterSet]);

  return (
    <>
      <Typography.Text>Parameters</Typography.Text>
      <Space
        style={{
          display: "flex",
          alignContent: "center",
          marginTop: 8,
        }}
      >
        <Form.Item name="parameterSet" style={{ marginBottom: 0 }}>
          <Select
            value={parameterSet.id}
            onChange={dispatchUseSavedParameterSet}
            style={{ width: 300 }}
          >
            {savedPipelineParameters.map((set) => (
              <Select.Option key={set.label} value={set.id}>
                {set.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Button icon={<IconEdit />} onClick={() => setVisible(true)} />
        <ParametersModal
          visible={visible}
          closeModal={() => setVisible(false)}
        />
      </Space>
    </>
  );
}
