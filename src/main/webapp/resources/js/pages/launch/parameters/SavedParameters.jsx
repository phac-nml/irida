import React from "react";
import {
  Alert,
  Button,
  Collapse,
  Form,
  Input,
  notification,
  Popover,
  Select,
  Typography,
} from "antd";
import { SPACE_MD, SPACE_XS } from "../../../styles/spacing";
import { useLaunch } from "../launch-context";
import { saveModifiedParametersAs } from "../launch-dispatch";
import styled from "styled-components";

const ParameterCollapse = styled(Collapse)`
  .ant-collapse-header {
    padding: 0 !important;
  }
  .template-parameters {
    display: grid;
    grid-template-columns: auto 100px;
    gap: ${SPACE_XS};
  }
`;

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
  const [fields] = React.useState(() => sets[0]?.parameters);

  function updateSelectedSet(id) {
    const set = sets.find((set) => set.id === id);
    setCurrentSetId(id);
    setModified({});
    const parameters = set.parameters.reduce(
      (params, curr) => ({ ...params, [curr.name]: curr.value }),
      {}
    );
    form.setFieldsValue(parameters);
  }

  function onValueUpdated(field, newValue) {
    const original = sets
      .find((set) => set.id === currentSetId)
      .parameters.find((parameter) => field === parameter.name);
    if (original.value === newValue) {
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
    saveParamsForm.validateFields().then(({ name }) => {
      saveModifiedParametersAs(dispatch, name, fieldsValue)
        .then((id) => {
          setCurrentSetId(id);
          setModified({});
        })
        .catch((e) => notification.error({ message: e }));
    });
  }

  return (
    <section>
      {fields.map(({ name }) => (
        <Form.Item key={`hidden-${name}`} name={name} hidden>
          <Input />
        </Form.Item>
      ))}
      <ParameterCollapse
        style={{ marginBottom: SPACE_MD }}
        expandIcon={Function.prototype}
        ghost
      >
        <Collapse.Panel
          header={
            <div className="template-parameters">
              <Select
                style={{ width: `100%` }}
                value={currentSetId}
                onChange={(id) => updateSelectedSet(id)}
                onClick={(e) => e.stopPropagation()}
                disabled={sets.length < 2}
                className="t-saved-select"
              >
                {sets.map((set) => (
                  <Select.Option key={set.key} value={set.id}>
                    {set.label}
                  </Select.Option>
                ))}
              </Select>
              <Button className="t-show-parameters">
                {i18n("SavedParameters.modify")}
              </Button>
            </div>
          }
        >
          {Object.keys(modified).length ? (
            <Alert
              className="t-modified-alert"
              showIcon
              style={{ marginBottom: SPACE_XS, marginTop: SPACE_XS }}
              type={"warning"}
              action={
                <Popover
                  placement="bottomRight"
                  trigger="click"
                  overlayClassName="t-save-params-form"
                  content={
                    <>
                      <Typography.Text>
                        {i18n("SavedParameters.modified.name")}
                      </Typography.Text>
                      <Form form={saveParamsForm} layout="inline">
                        <Form.Item
                          name="name"
                          rules={[
                            {
                              required: true,
                              message: i18n(
                                "SavedParameters.modified-name.required"
                              ),
                            },
                            {
                              min: 3,
                              message: i18n(
                                "SavedParameters.modified-name.length"
                              ),
                            },
                          ]}
                        >
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
              }
              message={i18n("SavedParameters.modified")}
              description={i18n("SavedParameters.modified.description")}
            />
          ) : null}
          {fields.map(({ name, label }) => (
            <Form.Item
              key={`field-${name}`}
              label={label}
              name={name}
              help={modified[name] ? i18n("ParametersModal.modified") : null}
            >
              <Input
                className="t-saved-input"
                onChange={(e) => onValueUpdated(name, e.target.value)}
              />
            </Form.Item>
          ))}
        </Collapse.Panel>
      </ParameterCollapse>
    </section>
  );
}
