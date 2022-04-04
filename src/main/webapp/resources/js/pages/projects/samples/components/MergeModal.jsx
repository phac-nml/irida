import React from "react";
import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  Modal,
  Radio,
  Row,
  Space,
  Typography,
} from "antd";
import { sampleNameRegex } from "../../../../utilities/validation-utilities";
import { validateSampleName } from "../../../../apis/projects/samples";

export default function MergeModal({ samples, visible, onOk }) {
  const copy = Object.entries(samples).map(([, sample]) => sample);
  const [renameSample, setRenameSample] = React.useState(false);
  const [form] = Form.useForm();

  const initialValues = {
    primary: copy[0].id,
    newName: "",
  };

  React.useEffect(() => {
    if (!renameSample) {
      form.setFieldsValue({
        newName: "",
      });
    }
  }, [renameSample]);

  // TODO: validate new name
  const validateName = async (name) => {
    const data = await validateSampleName(name);
    console.log(data);
    if (data.status === "success") {
      return Promise.resolve();
    } else {
      return Promise.reject(new Error(data.help));
    }
  };

  return (
    <Modal
      title="Merge Samples"
      visible={visible}
      onOk={onOk}
      onCancel={onOk}
      width={600}
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Alert
            type="warning"
            showIcon
            message={"Both samples will be merged into a single sample."}
            description={
              "Currently only the metadata from the sample selected will retain its metadata."
            }
          />
        </Col>
        <Col span={24}>
          <Typography.Text>
            The following 2 samples will be merged into the selected sample.
          </Typography.Text>
        </Col>
        <Col span={24}>
          <Form form={form} layout="vertical" initialValues={{ initialValues }}>
            <Form.Item
              label={"Select primary sample"}
              tooltip={
                "Other samples details will be overwritten by the primary sample"
              }
              required
            >
              <Radio.Group>
                <Space direction="vertical" name="primary">
                  {copy.map((sample) => {
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
      </Row>
    </Modal>
  );
}
