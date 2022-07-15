import React from "react";
import { AutoComplete, Form, Modal, Select, Typography } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { addSample } from "../services/runReducer";
import { useDispatch } from "react-redux";
import { useGetSampleNamesForProjectQuery } from "../../../apis/projects/samples";
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
  const [sampleNameValidateStatus, setSampleNameValidateStatus] =
    React.useState(null);
  const [sampleNameHelp, setSampleNameHelp] = React.useState(null);
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

  const projectOptions = projectsData.projects?.map((project) => (
    <Select.Option value={project.id} key={`${project.id} ${project.name}`}>
      <Text style={{ marginRight: SPACE_XS }}>{project.id}</Text>
      <Text type="secondary">{project.name}</Text>
    </Select.Option>
  ));

  const validateSampleName = (value) => {
    if (!value) {
      setSampleNameValidateStatus("error");
      setSampleNameHelp("Sample name must not be empty.");
    } else if (value.length < 3) {
      setSampleNameValidateStatus("error");
      setSampleNameHelp("Sample name must have at least 3 characters.");
    } else if (!new RegExp("^.[A-Za-z\\d-_!@#$%~`]+$").test(value)) {
      setSampleNameValidateStatus("error");
      setSampleNameHelp(
        "Sample name cannot contain any spaces or special characters."
      );
    } else if (samplesData.samples?.find((sample) => sample.name === value)) {
      setSampleNameValidateStatus("success");
      setSampleNameHelp("Files will be added to this existing sample");
    } else {
      setSampleNameValidateStatus("success");
      setSampleNameHelp("A new sample will be created within project");
    }
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
            validateStatus={sampleNameValidateStatus}
            help={sampleNameHelp}
          >
            <AutoComplete
              options={samples}
              filterOption={(inputValue, option) =>
                option.value.toLowerCase().indexOf(inputValue.toLowerCase()) !==
                -1
              }
              disabled={projectId === null}
              onChange={(value) => validateSampleName(value)}
            />
          </StyledFormItem>
        </Form>
      </Modal>
    </>
  );
}
