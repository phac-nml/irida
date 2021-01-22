import React from "react";
import { Alert, Button, Form, Input, Popover, Select } from "antd";
import { useLaunch } from "../launch-context";
import { SPACE_XS } from "../../../styles/spacing";

/**
 * React component to render a select input and modifying button for
 * selecting saved pipeline parameters.
 *
 * @param {object} form - Ant Design form api.
 * @returns {JSX.Element}
 * @constructor
 */
export function SavedParameters({ form }) {
  const [{ parameterSets, parameterFields }] = useLaunch();
  const [current, setCurrent] = React.useState(parameterSets[0].id);
  const [modified, setModified] = React.useState({});

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

  return (
    <>
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
                content={<div>FOOBAR</div>}
              >
                <Button>Save</Button>
              </Popover>
            }
          />
        ) : null}
      </div>
      {parameterFields.map(({ name, label, key }) => (
        <Form.Item
          key={key}
          label={label}
          name={name}
          rules={[
            {
              required: true,
              message: "All values are required",
            },
          ]}
          help={modified[name] ? i18n("ParametersModal.modified") : null}
        >
          <Input
            onChange={(e) =>
              onValueUpdated(parameter.name, parameter.value, e.target.value)
            }
          />
        </Form.Item>
      ))}
    </>
  );
}
