import React from "react";
import { Alert, Button, Form, Input, Popover, Select, Space } from "antd";
import { SPACE_LG } from "../../../styles/spacing";

/**
 * React component to render a select input and modifying button for
 * selecting saved pipeline parameters.
 *
 * @param {object} form - Ant Design form api.
 * @param {array} sets - list of different parameter sets available to this pipeline
 * @returns {JSX.Element}
 * @constructor
 */
export function SavedParameters({ form, sets }) {
  const [currentSetId, setCurrentSetId] = React.useState(sets[0].id);
  const [modified, setModified] = React.useState({});

  function updateSelectedSet(id) {
    const index = sets.findIndex((set) => set.id === id);
    setCurrentSetId(id);
    setModified({});
    const parameters = sets[index].parameters.reduce(
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
      <div>
        <Form.Item
          label={"Select a pre-defined parameter set"}
          tooltip={"Default Parameters are pipeline defaults"}
          help={
            Object.keys(modified).length ? (
              <Alert
                type={"warning"}
                message={
                  "This template had been modified. You can save it as a custom template"
                }
                action={
                  <Space>
                    <Popover
                      placement="bottomRight"
                      trigger="click"
                      content={
                        <div style={{ width: 300 }}>
                          <Form layout="inline">
                            <Form.Item>
                              <Input />
                            </Form.Item>
                            <Form.Item>
                              <Button>SAVE</Button>
                            </Form.Item>
                          </Form>
                        </div>
                      }
                    >
                      <Button size="small" type="ghost">
                        Save
                      </Button>
                    </Popover>
                  </Space>
                }
              />
            ) : null
          }
        >
          <Select
            style={{ width: `100%` }}
            value={currentSetId}
            onChange={(id) => updateSelectedSet(id)}
            disabled={sets.length < 2}
          >
            {sets.map((set) => (
              <Select.Option key={set.key} value={set.id}>
                {set.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </div>
      {sets[currentSetId].parameters.map(({ name, label, value }) => (
        <Form.Item
          style={{ marginLeft: SPACE_LG }}
          key={name}
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
            onChange={(e) => onValueUpdated(name, value, e.target.value)}
          />
        </Form.Item>
      ))}
    </>
  );
}
