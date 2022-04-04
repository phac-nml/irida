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
  Typography,
} from "antd";
import React from "react";
import reactVirtualizedAutoSizer from "react-virtualized-auto-sizer";
import { validateSampleName } from "../../../../apis/projects/samples";

export default function MergeModal({ samples, visible, onOk }) {
  const [renameSample, setRenameSample] = React.useState(false);
  const [form] = Form.useForm();

  // Determine valid and invalid samples
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

  const initialValues = {
    primary: valid[0]?.id,
    newName: "",
    ids: valid.map((sample) => sample.id),
  };

  React.useEffect(() => {
    if (!renameSample) {
      form.setFieldsValue({
        newName: "",
      });
    }
  }, [renameSample]);

  // Validate new name
  const validateName = async (name) => {
    if (renameSample) {
      const data = await validateSampleName(name);
      if (data.status === "success") {
        return Promise.resolve();
      } else {
        return Promise.reject(new Error(data.help));
      }
    } else {
      return Promise.resolve();
    }
  };

  return (
    <Modal
      title="Merge Samples"
      visible={visible}
      onOk={() => {
        form.validateFields().then((values) => console.log(values));
      }}
      onCancel={onOk}
      width={600}
    >
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
            <Col span={24}>
              <Form form={form} layout="vertical" initialValues={initialValues}>
                <Form.Item hidden name="ids">
                  <Input />
                </Form.Item>
                <Form.Item
                  name="primary"
                  label={"Select sample to retain metadata from"}
                  required
                >
                  <Radio.Group>
                    <Space direction="vertical">
                      {valid.map((sample) => {
                        return (
                          <Radio value={sample.id} key={`sample-${sample.id}`}>
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
    </Modal>
  );
}
