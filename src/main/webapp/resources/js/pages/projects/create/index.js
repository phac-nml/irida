import React, { useState } from "react";
import { render } from "react-dom";
import axios from "axios";
import debounce from "lodash/debounce";
import { Button, Form, Input, Select } from "antd";
import { PageWrapper } from "../../../components/page/PageWrapper";

function hasErrors(fieldsError) {
  const result = Object.keys(fieldsError).some(field => fieldsError[field]);
  console.log(fieldsError);
  return result;
}

function OrganismInput({}) {
  const [organism, setOrganism] = useState();
  const [taxonomy, setTaxonomy] = useState(undefined);
  const [loading, setLoading] = useState(false);
  let lastFetchId = 0;

  const onSearch = value => {
    setLoading(true);
    lastFetchId += 1;
    const fetchId = lastFetchId;
    axios
      .get(`${window.TL.BASE_URL}ajax/taxonomy?term=${value}`)
      .then(({ data }) => {
        if (fetchId === lastFetchId) {
          const taxonomy = [];
          // This ensure that only the latest requests gets set into the data
          function createOption(taxon, depth) {
            taxonomy.push(
              <Select.Option key={taxon.value} value={taxon.value}>
                <span style={{ marginLeft: 5 * depth }}>{taxon.text}</span>
              </Select.Option>
            );
            taxon.children.forEach(child => createOption(child, depth + 1));
          }

          data.forEach(child => createOption(child, 0));
          setTaxonomy(taxonomy);
        }
      })
      .finally(() => setLoading(false));
  };

  const debouncedSearch = debounce(onSearch, 300);

  const onChange = value => {
    setOrganism(value);
  };

  return (
    <Select
      showSearch
      value={organism}
      onSearch={debouncedSearch}
      loading={loading}
      onChange={onChange}
      optionLabelProp="value"
    >
      {taxonomy}
    </Select>
  );
}

function CreateProjectForm({ form }) {
  console.log(form);
  const {
    getFieldDecorator,
    getFieldError,
    getFieldsError,
    isFieldTouched
  } = form;

  // Only show error after a field is touched.
  const nameError = isFieldTouched("name") && getFieldError("name");

  return (
    <PageWrapper title="Create New Project">
      <Form>
        <Form.Item label="Project Name">
          {getFieldDecorator("name", {
            rules: [
              { required: true, message: "Project requires a name" },
              { min: 5, message: "At least 5 letters you fool!" }
            ]
          })(<Input />)}
        </Form.Item>
        <Form.Item label="Organism">
          <OrganismInput />
        </Form.Item>
        <Form.Item label="Description">
          <Input.TextArea autosize={{ minRows: 3, maxRows: 5 }} />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            disabled={!isFieldTouched("name") || hasErrors(getFieldsError())}
          >
            Create
          </Button>
        </Form.Item>
      </Form>
    </PageWrapper>
  );
}

const WrappedCreateProjectForm = Form.create({})(CreateProjectForm);

render(<WrappedCreateProjectForm />, document.querySelector("#root"));
