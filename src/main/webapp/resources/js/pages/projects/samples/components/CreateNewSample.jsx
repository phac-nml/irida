import React from "react";
import { AutoComplete, Form, Input, Modal } from "antd";
import {
  createSamples,
  validateSampleName,
} from "../../../../apis/projects/samples";
import searchOntology from "../../../../apis/ontology/taxonomy/query";

/**
 * React component to create a new sample within a project.
 * @returns {JSX.Element}
 * @constructor
 */
export default function CreateNewSample({
  visible,
  projectId,
  onCreate,
  onCancel,
}) {
  const [form] = Form.useForm();
  const nameRef = React.useRef();
  const [organisms, setOrganisms] = React.useState([]);

  React.useEffect(() => {
    if (visible) {
      nameRef.current.focus();
    }
  }, [visible]);

  /**
   * Reducer: Create the dropdown contents from the taxonomy.
   *
   * @param {array} accumulator
   * @param {object} current
   * @returns {*}
   */
  function optionsReducer(accumulator, current) {
    accumulator.push({ value: current.value });
    /*
      Recursively check to see if there are any children to add
       */
    if (current.children) {
      accumulator.push(...current.children.reduce(optionsReducer, []));
    }
    return accumulator;
  }

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  /**
   * This is used by Ant Design's input  validation system to server side validate the
   * sample name.  This includes name length, special characters, and if the name is already used.
   * @param rule
   * @param {string} value - the current value of the input
   * @returns {Promise<void>}
   */
  const validateName = async (value) => {
    await validateSampleName(value).then((response) => {
      if (response.status === "success") {
        return Promise.resolve();
      } else {
        return Promise.reject(response.help);
      }
    });
  };

  const searchOrganism = async (term) => {
    const data = await searchOntology({
      query: term,
      ontology: "taxonomy",
    });
    setOrganisms(data.reduce(optionsReducer, []));
  };

  const createSample = () => {
    form.validateFields().then((values) => {
      createSamples({
        projectId,
        body: [values],
      }).then(() => {
        form.resetFields();
        onCreate();
      });
    });
  };

  return (
    <Modal
      open={visible}
      onOk={createSample}
      onCancel={handleCancel}
      title={i18n("AddSample.title")}
    >
      <Form
        form={form}
        initialValues={{ name: "", organism: "" }}
        layout="vertical"
      >
        <Form.Item
          className="t-sample-name-wrapper"
          name="name"
          label={i18n("AddSample.name")}
          rules={[
            () => ({
              validator(_, value) {
                return validateName(value);
              },
            }),
          ]}
        >
          <Input ref={nameRef} className={"t-sample-name"} />
        </Form.Item>
        <Form.Item name="organism" label={i18n("AddSample.organism")}>
          <AutoComplete
            className="t-organism-input"
            allowClear={true}
            backfill={true}
            onSearch={searchOrganism}
            options={organisms}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
}
