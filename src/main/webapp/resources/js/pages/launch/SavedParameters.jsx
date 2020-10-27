import React from "react";
import Form from "antd/es/form";
import { Button, Select } from "antd";
import { IconEdit } from "../../components/icons/Icons";
import ParametersModal from "./components/ParametersModal";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import { SPACE_XS } from "../../styles/spacing";

/**
 * React component to render a select input and modifying button for
 * selecting saved pipeline parameters.
 *
 * @param {object} form - Ant Design form api.
 * @returns {JSX.Element}
 * @constructor
 */
export function SavedParameters({ form }) {
  const { savedPipelineParameters, parameterSet } = useLaunchState();
  const { dispatchUseSavedParameterSet } = useLaunchDispatch();
  const [visible, setVisible] = React.useState(false);

  /**
   * If the parameters are updated in the modal window, the selected template
   * will change.  This watches for this and updates the select value to
   * make sure the appropriate template is selected.
   */
  React.useEffect(() => {
    form.setFieldsValue({ parameterSet: parameterSet.id });
  }, [form, parameterSet]);

  return (
    <>
      <Form.Item label={i18n("SavedParameters.title")}>
        <div style={{ display: "flex" }}>
          <div style={{ flexGrow: 1, marginRight: SPACE_XS }}>
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
          </div>
          <div>
            <Button icon={<IconEdit />} onClick={() => setVisible(true)} />
          </div>
        </div>
      </Form.Item>
      <ParametersModal visible={visible} closeModal={() => setVisible(false)} />
    </>
  );
}
