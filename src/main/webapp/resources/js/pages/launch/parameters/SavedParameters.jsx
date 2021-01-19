import React from "react";
import { Button, Form, Select, Space, Tag } from "antd";
import { IconEdit } from "../../../components/icons/Icons";
import { ParametersModal } from "./ParametersModal";
import { useLaunch } from "../launch-context";
import { SPACE_XS } from "../../../styles/spacing";
import { setParameterSetById } from "../launch-dispatch";

/**
 * React component to render a select input and modifying button for
 * selecting saved pipeline parameters.
 *
 * @param {object} form - Ant Design form api.
 * @returns {JSX.Element}
 * @constructor
 */
export function SavedParameters({ form }) {
  const [{ parameterSets, parameterSet }, launchDispatch] = useLaunch();
  const [visible, setVisible] = React.useState(false);

  /**
   * If the parameters are updated in the modal window, the selected template
   * will change.  This watches for this and updates the select value to
   * make sure the appropriate template is selected.
   */
  React.useEffect(() => {
    form.setFieldsValue({ parameterSet: parameterSet.id });
  }, [form, parameterSet]);

  return parameterSets[0].parameters.length > 0 ? (
    <>
      <Form.Item label={i18n("SavedParameters.title")}>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: `1fr min-content`,
            columnGap: SPACE_XS,
          }}
        >
          <div>
            <Form.Item>
              <Select
                value={parameterSet.id}
                onChange={(id) => setParameterSetById(launchDispatch, id)}
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
            <Button
              className="t-edit-params-btn"
              icon={<IconEdit />}
              onClick={() => setVisible(true)}
            />
            <ParametersModal
              visible={visible}
              closeModal={() => setVisible(false)}
            />
          </div>
        </div>
      </Form.Item>
    </>
  ) : null;
}
