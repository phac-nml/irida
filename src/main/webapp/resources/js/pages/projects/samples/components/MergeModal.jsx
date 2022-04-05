import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  List,
  Modal,
  Radio,
  Row,
  Space,
  Spin,
  Typography,
} from "antd";
import React from "react";
import { mergeSamples } from "../../../../apis/projects/project-samples";
import { serverValidateSampleName } from "../../../../utilities/validation-utilities";

export default function MergeModal({ samples, visible, onComplete, onCancel }) {
  const [initialized, setInitialized] = React.useState(false);
  const [renameSample, setRenameSample] = React.useState(false);
  const [error, setError] = React.useState(undefined);
  const [loading, setLoading] = React.useState(false);
  const [form] = Form.useForm();

  /**
   * Determine valid and invalid samples
   * Invalid sample: sample that cannot be modified
   */
  const [valid, invalid] = React.useMemo(() => {
    const values = Object.values(samples),
      valid = [],
      invalid = [];
    values?.forEach((sample) => {
      if (sample.owner) {
        valid.push(sample);
      } else {
        invalid.push(sample);
      }
    });
    return [valid, invalid];
  }, [samples]);

  React.useEffect(() => {
    if (invalid !== undefined && valid !== undefined) {
      setInitialized(true);
    }
  }, [invalid, valid]);

  const initialValues = {
    primary: valid[0]?.id,
    newName: "",
  };

  React.useEffect(() => {
    if (!renameSample) {
      form.setFieldsValue({
        newName: "",
      });
    }
  }, [form, renameSample]);

  // Server validate new name
  const validateName = async (name) => {
    if (renameSample) {
      return serverValidateSampleName(name);
    } else {
      return Promise.resolve();
    }
  };

  const onSubmit = () => {
    setLoading(true);
    form
      .validateFields()
      .then((values) => {
        const ids = valid
          .map((sample) => sample.id)
          .filter((id) => id !== values.primary);

        mergeSamples(valid[0].projectId, {
          ...values,
          ids,
        })
          .then(onComplete)
          .catch((e) => setError(e.response.data.error));
      })
      .finally(() => setLoading(false));
  };

  // TODO: Handle rendering many samples?
  // TODO: Handle rendering many locked samples?k
  return (
    <Modal
      title="Merge Samples"
      visible={visible}
      onOk={onSubmit}
      okButtonProps={{
        loading,
      }}
      onCancel={onCancel}
      width={600}
    >
      {initialized ? (
        <Row gutter={[16, 16]}>
          {valid.length >= 2 ? (
            <>
              <Col span={24}>
                <Alert
                  type="warning"
                  showIcon
                  message={
                    "Only the metadata from the selected sample will be retained"
                  }
                />
              </Col>
              {error !== undefined ? (
                <Col span={24}>
                  <Alert
                    showIcon
                    type="error"
                    message={error}
                    closable
                    onClose={() => setError(undefined)}
                  />
                </Col>
              ) : null}
              <Col span={24}>
                <Form
                  form={form}
                  layout="vertical"
                  initialValues={initialValues}
                >
                  <Form.Item
                    name="primary"
                    label={"Select sample to retain metadata from"}
                    required
                  >
                    <Radio.Group>
                      <Space direction="vertical">
                        {valid.map((sample) => {
                          return (
                            <Radio
                              value={sample.id}
                              key={`sample-${sample.id}`}
                            >
                              {sample.sampleName}
                            </Radio>
                          );
                        })}
                      </Space>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item noStyle>
                    <Checkbox
                      checked={renameSample}
                      onChange={(e) => setRenameSample(e.target.checked)}
                    >
                      Rename Sample
                    </Checkbox>
                    <Form.Item
                      name="newName"
                      rules={[
                        ({ getFieldValue }) => ({
                          validator(_, value) {
                            return validateName(value);
                          },
                        }),
                      ]}
                    >
                      <Input disabled={!renameSample} />
                    </Form.Item>
                  </Form.Item>
                </Form>
              </Col>
            </>
          ) : (
            <Col span={24}>
              <Alert
                type="error"
                showIcon
                message="Must have at least 2 non-locked samples to merge"
              />
            </Col>
          )}
          {invalid.length ? (
            <Col span={24}>
              <List
                size="small"
                header={"Locked samples cannot be merged"}
                bordered
                dataSource={invalid}
                renderItem={(item) => (
                  <List.Item>
                    <Typography.Text>{item.sampleName}</Typography.Text>
                  </List.Item>
                )}
              />
            </Col>
          ) : null}
        </Row>
      ) : (
        <Space>
          <Spin /> <Typography.Text>Checking samples ...</Typography.Text>
        </Space>
      )}
    </Modal>
  );
}
