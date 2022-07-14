import React from "react";
import { AutoComplete, Form, Modal, Select, Typography } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { addSample } from "../services/runReducer";
import { useDispatch } from "react-redux";
import {
  useGetSampleNamesForProjectQuery,
  validateSampleName,
} from "../../../apis/projects/samples";
import { useGetProjectNamesForUserQuery } from "../../../apis/projects/projects";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";

const { Text } = Typography;

const StyledFormItem = styled(Form.Item)`
  .ant-select-status-success > .ant-select-selector {
    border-color: green;
  }
  .ant-form-item-explain-success {
    color: green;
  }
`;

/**
 * React component to display the sequencing run create new sample modal.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SequencingRunCreateSampleButton() {
  const dispatch = useDispatch();
  const [visible, setVisible] = React.useState(false);
  const [projectId, setProjectId] = React.useState(null);
  const [skip, setSkip] = React.useState(true);
  const [samples, setSamples] = React.useState([]);
  const [form] = Form.useForm();

  const { data: projectsData = {} } = useGetProjectNamesForUserQuery();
  const { data: samplesData = {}, isSuccess } =
    useGetSampleNamesForProjectQuery(projectId, {
      skip,
    });

  React.useEffect(() => {
    setSamples(
      samplesData.samples?.map(({ name }) => ({ label: name, value: name }))
    );
  }, [isSuccess]);

  const addNewSample = () => {
    setVisible(true);
  };

  const sampleOptions = samplesData.samples?.map((sample) => (
    <Select.Option value={sample.id} key={`sample-list-item-${sample.id}`}>
      {sample.id + " - " + sample.name}
    </Select.Option>
  ));

  const projectOptions = projectsData.projects?.map((project) => (
    <Select.Option value={project.id} key={`${project.id} ${project.name}`}>
      <Text style={{ marginRight: SPACE_XS }}>{project.id}</Text>
      <Text type="secondary">{project.name}</Text>
    </Select.Option>
  ));

  const validateName = async (value) => {
    await validateSampleName(value, projectId).then((response) => {
      if (response.status === "success") {
        return Promise.resolve();
      } else {
        return Promise.reject(response.help);
      }
    });
  };

  const onSamplesSearch = (term) => {
    const lowerTerm = term.toLowerCase();
    const newSamples = samples
      .filter((sample) => sample.name.toLowerCase().includes(lowerTerm))
      .map(({ name }) => ({ label: name, value: name }));
    setSamples(newSamples);
  };

  const onProjectChange = (value) => {
    setProjectId(value);
    setSkip(false);
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
        title="Add Sample"
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
      >
        <Form
          form={form}
          initialValues={{
            projectId: "",
            sampleId: "",
            sampleName: "",
          }}
          layout="vertical"
        >
          <Form.Item name="project" label="Project">
            <Select showSearch onChange={onProjectChange}>
              {projectOptions}
            </Select>
          </Form.Item>
          <StyledFormItem
            name="sampleName"
            label="Sample Name"
            validateStatus={"success"}
            help={"Name cannot be empty"}
            rules={[
              ({}) => ({
                validator(_, value) {
                  return validateName(value);
                },
              }),
            ]}
          >
            <AutoComplete
              // allowClear={true}
              // backfill={true}
              // onSearch={onSamplesSearch}
              options={samples}
              filterOption={(inputValue, option) =>
                option.value.toLowerCase().indexOf(inputValue.toLowerCase()) !==
                -1
              }
              disabled={projectId === null}
            />
          </StyledFormItem>
        </Form>
      </Modal>
    </>
  );
}
