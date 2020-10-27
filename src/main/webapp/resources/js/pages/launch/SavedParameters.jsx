import React from "react";
import Form from "antd/es/form";
import { Button, Col, Row, Select } from "antd";
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
      <Form.Item label={i18n("SavedParameters.title")}>
        <Row gutter={8}>
          <Col span={20}>
            <Form.Item name="parameterSet">
              <Select
                value={parameterSet.id}
                onChange={dispatchUseSavedParameterSet}
              >
                {savedPipelineParameters.map((set) => (
                  <Select.Option key={set.label} value={set.id}>
                    {set.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col span={4}>
            <Button icon={<IconEdit />} onClick={() => setVisible(true)} />
          </Col>
        </Row>
      </Form.Item>
      <ParametersModal visible={visible} closeModal={() => setVisible(false)} />
    </>
  );
}
