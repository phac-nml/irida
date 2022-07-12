import React from "react";
import { AutoComplete, Form, Input, Modal, Radio, Select } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { addSample } from "../services/runReducer";
import { useDispatch } from "react-redux";
import {
  useGetSampleNamesForProjectQuery,
  validateSampleName,
} from "../../../apis/projects/samples";
import searchOntology from "../../../apis/ontology/taxonomy/query";
import { useGetProjectNamesForUserQuery } from "../../../apis/projects/projects";

/**
 * React component to display the sequencing run create new sample modal.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SequencingRunCreateSampleButton() {
  const dispatch = useDispatch();
  const [projectId, setProjectId] = React.useState();
  const [isSampleNew, setIsSampleNew] = React.useState(false);
  const [visible, setVisible] = React.useState(false);
  const [organisms, setOrganisms] = React.useState([]);
  const [showSampleSection, setShowSampleSection] = React.useState(false);
  const [form] = Form.useForm();

  const { data: projects = {} } = useGetProjectNamesForUserQuery();
  const { data: samples = {} } = useGetSampleNamesForProjectQuery(projectId, {
    skip: !showSampleSection,
  });

  const sampleOptions = samples.samples?.map((sample) => (
    <Select.Option value={sample.id} key={`sample-list-item-${sample.id}`}>
      {sample.id + " - " + sample.name}
    </Select.Option>
  ));

  const projectOptions = projects.projects?.map((project) => (
    <Select.Option value={project.id} key={`project-list-item-${project.id}`}>
      {project.id + " - " + project.name}
    </Select.Option>
  ));

  const addNewSample = () => {
    setVisible(true);
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

  const onNewSampleChange = ({ target: { value } }) => {
    console.log("is new sample value ", value);
    setIsSampleNew(value);
  };

  const onProjectChange = (value) => {
    setProjectId(value);
    setShowSampleSection(true);
  };

  const onCancel = () => {
    setVisible(false);
    form.resetFields();
  };

  const onOk = () => {
    form.validateFields().then((values) => {
      dispatch(
        addSample({
          sampleName: "New Sample",
          pairs: [],
        })
      );
      form.resetFields();
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
            project: "",
            isSampleNew,
            sample_name: "",
            organism: "",
          }}
          layout="vertical"
        >
          <Form.Item name="project" label="Project">
            <Select showSearch onChange={onProjectChange}>
              {projectOptions}
            </Select>
          </Form.Item>
          {showSampleSection && (
            <Form.Item name="isSampleNew">
              <Radio.Group onChange={onNewSampleChange}>
                <Radio.Button value={true}>New</Radio.Button>
                <Radio.Button value={false}>Existing</Radio.Button>
              </Radio.Group>
            </Form.Item>
          )}
          {showSampleSection && !isSampleNew && (
            <Form.Item name="sample" label="Sample">
              <Select showSearch>{sampleOptions}</Select>
            </Form.Item>
          )}
          {showSampleSection && isSampleNew && (
            <>
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
            </>
          )}
        </Form>
      </Modal>
    </>
  );
}
