import React, { useEffect, useReducer } from "react";
import { AutoComplete, Checkbox, Form } from "antd";

/**
 * @fileOverview Component to render a react ant design form for saving
 * a new / updated template.  The template name must either be unique (i.e
 * not existing in the system) or the checkbox to overwrite the existing template
 * must be selected.
 */

const initialState = {
  name: { value: "", validation: {} },
  options: [],
  existing: false,
  overwrite: false
};

const TYPES = {
  NAMES: 0,
  OVERWRITE: 1
};

const generateFilteredOptions = (array, term) =>
  array
    .filter(n => n.toLowerCase().includes(term.toLowerCase()))
    .map(n => (
      <AutoComplete.Option key={n} value={n}>
        {n}
      </AutoComplete.Option>
    ));

function reducer(state, action) {
  const generateNameInputValues = name => {
    const required = name.length === 0;
    const existing = state._names.includes(action.payload.name);
    const validation = {
      hasFeedback: required || existing,
      validateStatus: required ? "error" : existing ? "warning" : null,
      help: required
        ? i18n("SaveTemplateModal.required")
        : existing
        ? i18n("SaveTemplateModal.nameExists")
        : null
    };
    return {
      value: name,
      existing,
      validation
    };
  };

  switch (action.type) {
    case TYPES.NAME:
      return {
        ...state,
        overwrite: false,
        name: generateNameInputValues(action.payload.name),
        options: generateFilteredOptions(state._names, action.payload.name)
      };
    case TYPES.OVERWRITE:
      return { ...state, overwrite: action.payload.overwrite };
    default:
      return { ...state };
  }
}

export function SaveTemplateForm({ form, template, templates, setValidity }) {
  const names = templates.map(t => t.name);
  const [state, dispatch] = useReducer(reducer, {
    ...initialState,
    _template: template,
    _names: names,
    options: generateFilteredOptions(names, "")
  });

  useEffect(() => {
    const valid = state.overwrite || !state.name.validation.hasFeedback;
    setValidity(valid);
  }, [state.name.value, state.overwrite]);

  const updateName = name => dispatch({ type: TYPES.NAME, payload: { name } });
  const updateOverwrite = e =>
    dispatch({
      type: TYPES.OVERWRITE,
      payload: { overwrite: e.target.checked }
    });

  return (
    <Form form={form} layout="vertical" name="save_template">
      <Form.Item
        name="name"
        label={i18n("linelist.templates.saveModal.name")}
        {...state.name.validation}
      >
        <AutoComplete
          value={state.name.value}
          onSearch={updateName}
          onSelect={updateName}
        >
          {state.options}
        </AutoComplete>
      </Form.Item>
      {state.name.existing ? (
        <Form.Item name="overwrite">
          <Checkbox checked={state.overwrite} onChange={updateOverwrite}>
            {i18n("SaveTemplateModal.overwrite")}
          </Checkbox>
        </Form.Item>
      ) : null}
    </Form>
  );
}
