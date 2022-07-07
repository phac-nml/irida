import React from "react";
import { AutoComplete, Form, Input, Modal } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { addSample } from "../services/runReducer";
import { useDispatch } from "react-redux";
import {
  createNewSample,
  validateSampleName,
} from "../../../apis/projects/samples";
import searchOntology from "../../../apis/ontology/taxonomy/query";
import { useGetProjectsForUserQuery } from "../../../apis/projects/projects";

/**
 * React component to display the sequencing run create new sample modal.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SequencingRunCreateSampleButton() {
  const dispatch = useDispatch();
  const [visible, setVisible] = React.useState(false);
  const [organisms, setOrganisms] = React.useState([]);
  const [form] = Form.useForm();

  // const { data = [] } = useGetSequencingRunFilesQuery(1);
  const { data = [], isSuccess } = useGetProjectsForUserQuery();

  if (isSuccess) {
    console.log(data);
  }

  const addNewSample = () => {
    setVisible(true);
    dispatch(
      addSample({
        sampleName: "New Sample",
        pairs: [],
      })
    );
  };

  const validateName = async (value) => {
    await validateSampleName(value).then((response) => {
      if (response.status === "success") {
        return Promise.resolve();
      } else {
        return Promise.reject(response.help);
      }
    });
  };

  function optionsReducer(accumulator, current) {
    accumulator.push({ value: current.value });
    if (current.children) {
      accumulator.push(...current.children.reduce(optionsReducer, []));
    }
    return accumulator;
  }

  const searchOrganism = async (term) => {
    const data = await searchOntology({
      query: term,
      ontology: "taxonomy",
    });
    setOrganisms(data.reduce(optionsReducer, []));
  };

  const onCancel = () => {
    setVisible(false);
    form.resetFields();
  };

  const onChange = (e) => {
    console.log("radio checked", e.target.value);
  };

  const onOk = () => {
    form.validateFields().then((values) => {
      createNewSample(values).then(() => {
        form.resetFields();
      });
    });
  };

  return (
    <>
      <AddNewButton
        type="default"
        onClick={addNewSample}
        text={i18n("SequencingRunSamplesList.empty.button")}
      />
      <Modal
        title="Create New Sample"
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
      >
        <Form
          form={form}
          initialValues={{
            project_new: "false",
            project_name: "",
            sample_name: "",
            organism: "",
          }}
          layout="vertical"
        >
          <Form.Item name="project" label="Project">
            <Input />
          </Form.Item>
          <Form.Item
            name="sample_name"
            label="Sample Name"
            rules={[
              ({}) => ({
                validator(_, value) {
                  return validateName(value);
                },
              }),
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item name="organism" label="Organism">
            <AutoComplete
              allowClear={true}
              backfill={true}
              onSearch={searchOrganism}
              options={organisms}
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
