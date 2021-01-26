import React from "react";
import { Alert, Button, Form, Input, Popover, Select, Space } from "antd";
import { SPACE_LG, SPACE_XS } from "../../../styles/spacing";
import { ArrowRightOutlined } from "@ant-design/icons";
import { grey5 } from "../../../styles/colors";
import { useLaunch } from "../launch-context";
import { saveModifiedParametersAs } from "../launch-dispatch";

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
  const [, dispatch] = useLaunch();
  const [saveParamsForm] = Form.useForm();

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

  async function saveParameters() {
    const fieldsValue = form.getFieldsValue(
      sets[0].parameters.map((parameter) => parameter.name)
    );
    const id = await saveModifiedParametersAs(
      dispatch,
      saveParamsForm.getFieldValue("name"),
      fieldsValue
    );
    setCurrentSetId(id);
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
                        <Form
                          form={saveParamsForm}
                          layout="inline"
                          style={{
                            width: 305,
                          }}
                        >
                          <Form.Item name="name">
                            <Input />
                          </Form.Item>
                          <Form.Item>
                            <Button onClick={saveParameters}>SAVE</Button>
                          </Form.Item>
                        </Form>
                      }
                    >
                      <Button size="small" type="ghost">
                        Save As
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
        <div
          key={name}
          style={{
            display: "grid",
            gridTemplateColumns: `8px auto`,
            columnGap: 0,
          }}
        >
          <ArrowRightOutlined
            style={{
              fontSize: 14,
              marginLeft: SPACE_XS,
              color: grey5,
              marginTop: 2,
            }}
          />
          <Form.Item
            style={{ marginLeft: SPACE_LG }}
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
        </div>
      ))}
    </>
  );
}
