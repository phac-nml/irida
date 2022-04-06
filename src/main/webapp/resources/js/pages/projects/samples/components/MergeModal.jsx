import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  List,
  Modal,
  notification,
  Radio,
  Row,
  Space,
  Spin,
  Typography,
} from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { serverValidateSampleName } from "../../../../utilities/validation-utilities";
import { useMergeMutation } from "../services/samples";

export default function MergeModal({ samples, visible, onComplete, onCancel }) {
  const { projectId } = useSelector((state) => state.samples);
  const [merge, { isLoading }] = useMergeMutation();

  const [initialized, setInitialized] = React.useState(false);
  const [renameSample, setRenameSample] = React.useState(false);
  const [error, setError] = React.useState(undefined);
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

  const onSubmit = async () => {
    let values;

    try {
      values = await form.validateFields();
    } catch {
      return;
    }
    const ids = valid
      .map((sample) => sample.id)
      .filter((id) => id !== values.primary);

    const { message } = await merge({
      projectId,
      request: {
        ...values,
        ids,
      },
    }).unwrap();

    notification.success({
      message: i18n("MergeModal.success"),
      description: message,
    });
    onComplete();
  };

  // TODO: Handle rendering many samples?
  // TODO: Handle rendering many locked samples?k
  return (
    <Modal
      title={i18n("MergeModal.title")}
      visible={visible}
      onOk={onSubmit}
      okText={i18n("MergeModal.okText")}
      okButtonProps={{
        loading: isLoading,
        disabled: valid.length < 2,
      }}
      onCancel={onCancel}
      cancelText={i18n("MergeModal.cancelText")}
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
                  message={i18n("MergeModal.metadata-warning")}
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
                    label={i18n("MergeModal.input-primary")}
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
                message={i18n("MergeModal.error-valid")}
              />
            </Col>
          )}
          {invalid.length ? (
            <Col span={24}>
              <List
                size="small"
                header={i18n("MergeModal.locked-samples")}
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
          <Spin />
          <Typography.Text>{i18n("MergeModal.loading")}</Typography.Text>
        </Space>
      )}
    </Modal>
  );
}
