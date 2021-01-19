import React from "react";
import { Button, Divider, Form, Input, Select, Space, Tag } from "antd";
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
  const [{ parameterSets, parameterSet }, launchDispatch] = useLaunch(); // TODO remove parameterSet
  const [current, setCurrent] = React.useState(parameterSets[0].parameters);
  const [modified, setModified] = React.useState({});

  React.useEffect(() => {}, []);

  const [visible, setVisible] = React.useState(false);

  function useParameterSet(id) {
    const newSet = parameterSets.find((set) => set.id === id);
    setCurrent(newSet);
    newSet.parameters.forEach((parameter) =>
      form.setFieldsValue({ [parameter.name]: parameter.value })
    );
  }

  React.useEffect(() => {
    console.log(modified);
  }, [modified]);

  function onValueUpdated(field, original, newValue) {
    if (original === newValue) {
      const newMod = { ...modified };
      delete newMod[field];
      setModified(newMod);
    } else {
      setModified({ ...modified, [field]: newValue });
    }
  }

  /**
   * If the parameters are updated in the modal window, the selected template
   * will change.  This watches for this and updates the select value to
   * make sure the appropriate template is selected.
   */
  React.useEffect(() => {
    form.setFieldsValue({ parameterSet: parameterSet.id });
  }, [form, parameterSet]); // TODO: This can be removed

  return parameterSets[0].parameters.length > 0 ? (
    <>
      <Divider orientation="left" plain>
        Saved Pipeline Parameters
      </Divider>
      <div>
        <Form.Item label={"Saved Parameter Sets"}>
          <Select
            value={parameterSet.id}
            onChange={(id) => useParameterSet(id)}
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
      {parameterSet.parameters.map((parameter) => (
        <Form.Item
          key={parameter.name}
          label={parameter.label}
          name={parameter.name}
          rules={[]}
          initialValue={parameter.value}
          hasFeedback={modified[parameter.name]}
          validateStatus={modified[parameter.name] ? "warning" : null}
          help={modified[parameter.name] ? "MODIFIED" : null}
        >
          <Input
            onChange={(e) =>
              onValueUpdated(parameter.name, parameter.value, e.target.value)
            }
          />
        </Form.Item>
      ))}
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
            <Button icon={<IconEdit />} onClick={() => setVisible(true)} />
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
