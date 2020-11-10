import React from "react";
import { Button, Form, Select, Space, Tag } from "antd";
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
  const { parameterSets, parameterSet } = useLaunchState();
  const { dispatchUseParameterSetById } = useLaunchDispatch();
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
                onChange={dispatchUseParameterSetById}
              >
                {parameterSets.map((set) => (
                  <Select.Option key={set.key} value={set.id}>
                    <Space>
                      {set.label}
                      {set.key.endsWith("MODIFIED") ? (
                        <Tag>{i18n("ParametersModal.modified")}</Tag>
                      ) : (
                        ""
                      )}
                    </Space>
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
