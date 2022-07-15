import React from "react";
import { AutoComplete, Form, Input, Modal, Select, Typography } from "antd";
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

  const { data: projectsData = {}, isProjectsSuccess } =
    useGetProjectNamesForUserQuery();
  const { data: samplesData = {}, isSamplesSuccess } =
    useGetSampleNamesForProjectQuery(projectId, {
      skip,
    });

  React.useEffect(() => {
    setSamples(
      samplesData.samples?.map(({ name }) => ({ label: name, value: name }))
    );
  }, [isSamplesSuccess]);

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
      return Promise.reject("Sample name must not be empty.");
    } else if (value.length < 3) {
      return Promise.reject("Sample name must have at least 3 characters.");
    } else if (!new RegExp("^.[A-Za-z\\d-_!@#$%~`]+$").test(value)) {
      return Promise.reject(
        "Sample name cannot contain any spaces or special characters."
      );
    } else {
      const sampleId = samplesData.samples?.find(
        (sample) => sample.name === value
      )?.id;
      if (sampleId) {
        setSampleNameValidateStatus("success");
        setSampleNameHelp("Files will be added to this existing sample");
        form.setFieldsValue({ sampleId });
        return Promise.resolve();
      } else {
        setSampleNameValidateStatus("success");
        setSampleNameHelp("A new sample will be created within project");
        form.setFieldsValue({ sampleId: null });
        return Promise.resolve();
      }
    }
  };

  const onProjectChange = (value) => {
    setProjectId(value);
    setSkip(false);
  };

  const onCancel = () => {
    setVisible(false);
    setSkip(true);
    setProjectId(null);
    setSamples([]);
    setSampleNameValidateStatus(null);
    setSampleNameHelp(null);
    form.resetFields();
  };

  const onOk = () => {
    form.validateFields().then((values) => {
      console.log(values);
      setVisible(false);
      setSkip(true);
      setProjectId(null);
      setSamples([]);
      setSampleNameValidateStatus(null);
      setSampleNameHelp(null);
      dispatch(
        addSample({
          sampleName: values.sampleName,
          sampleId: values.sampleId,
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
            projectId: null,
            sampleName: "",
            sampleId: null,
          }}
          layout="vertical"
        >
          <Form.Item
            name="project"
            label="Project"
            rules={[
              {
                required: true,
                message: "Project is required",
              },
            ]}
          >
            <Select showSearch onChange={onProjectChange}>
              {projectOptions}
            </Select>
          </Form.Item>
          <StyledFormItem
            name="sampleName"
            label="Sample Name"
            validateStatus={sampleNameValidateStatus}
            help={sampleNameHelp}
            rules={[
              () => ({
                validator(_, value) {
                  return validateSampleName(value);
                },
              }),
            ]}
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
          <Form.Item name="sampleId" hidden={true}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
