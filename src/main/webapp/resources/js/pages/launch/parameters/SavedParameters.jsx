import React from "react";
import {
  Alert,
  Button,
  Divider,
  Form,
  Input,
  Popover,
  Select,
  Space,
  Tag,
} from "antd";
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
  const [current, setCurrent] = React.useState(parameterSets[0].id);
  const [modified, setModified] = React.useState({});
  const [saveVisible, setSaveVisible] = React.useState(false);

  React.useEffect(() => {}, []);

  const [visible, setVisible] = React.useState(false);

  function updateSelectedSet(id) {
    const index = parameterSets.findIndex((set) => set.id === id);
    setCurrent(id);
    setModified({});
    const parameters = parameterSets[index].parameters.reduce(
      (acc, curr) => ({ ...acc, [curr.name]: curr.value }),
      {}
    );
    form.setFieldsValue(parameters);
  }

  function onValueUpdated(field, original, newValue) {
    if (original === newValue) {
      const newMod = { ...modified };
      delete newMod[field];
      setModified(newMod);
    } else {
      setModified({ ...modified, [field]: newValue });
    }
  }

  function getParameterValues() {
    const valueMap = form.getFieldsValue(
      parameterSets[0].parameters.map((parameter) => parameter.name)
    );
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
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: SPACE_XS,
        }}
      >
        <Form.Item label={"Select a pre-defined parameter set"}>
          <Select
            style={{ width: 300 }}
            value={current}
            onChange={(id) => updateSelectedSet(id)}
            disabled={parameterSets.length < 2}
          >
            {parameterSets.map((set) => (
              <Select.Option key={set.key} value={set.id}>
                {set.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        {Object.keys(modified).length ? (
          <Alert
            type={"warning"}
            message={
              "This template had been modified. You can save it as a custom template"
            }
            action={
              <Popover
                placement="bottomRight"
                trigger="click"
                title={"Save modified parameters as"}
                content={<div>FOOBAR</div>}
              >
                <Button size="small">Save</Button>
              </Popover>
            }
          />
        ) : null}
      </div>
      {parameterSet.parameters.map((parameter) => (
        <Form.Item
          key={parameter.name}
          label={parameter.label}
          name={parameter.name}
          rules={[
            {
              required: true,
              message: "All values are required",
            },
          ]}
          initialValue={parameter.value}
          help={
            modified[parameter.name] ? i18n("ParametersModal.modified") : null
          }
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
