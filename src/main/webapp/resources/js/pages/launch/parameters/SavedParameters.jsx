import React from "react";
import {
  Alert,
  Button,
  Form,
  Input,
  Popover,
  Select,
  Space,
  Typography,
} from "antd";
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
    try {
      const id = await saveModifiedParametersAs(
        dispatch,
        saveParamsForm.getFieldValue("name"),
        fieldsValue
      );
      setCurrentSetId(id);
      setModified(false);
    } catch (e) {}
  }

  return (
    <>
      <div>
        <Form.Item
          label={i18n("SavedParameters.title")}
          tooltip={i18n("SavedParameters.tooltip")}
          help={
            Object.keys(modified).length ? (
              <Alert
                className="t-modified-alert"
                showIcon
                style={{ marginBottom: SPACE_XS, marginTop: SPACE_XS }}
                type={"warning"}
                message={i18n("SavedParameters.modified")}
                description={i18n("SavedParameters.modified.description")}
                action={
                  <Space>
                    <Popover
                      placement="bottomRight"
                      trigger="click"
                      content={
                        <>
                          <Typography.Text>
                            {i18n("SavedParameters.modified.name")}
                          </Typography.Text>
                          <Form
                            form={saveParamsForm}
                            layout="inline"
                            style={{
                              width: 305,
                            }}
                          >
                            <Form.Item name="name" required>
                              <Input className="t-modified-name" />
                            </Form.Item>
                            <Form.Item>
                              <Button
                                onClick={saveParameters}
                                className="t-saveas-submit"
                              >
                                {i18n("SavedParameters.modified.save")}
                              </Button>
                            </Form.Item>
                          </Form>
                        </>
                      }
                    >
                      <Button
                        size="small"
                        type="ghost"
                        className="t-modified-saveas"
                      >
                        {i18n("SavedParameters.modified.saveAs")}
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
            className="t-saved-select"
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
            help={modified[name] ? i18n("ParametersModal.modified") : null}
          >
            <Input
              className="t-saved-input"
              onChange={(e) => onValueUpdated(name, value, e.target.value)}
            />
          </Form.Item>
        </div>
      ))}
    </>
  );
}
