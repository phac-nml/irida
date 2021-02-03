import React from "react";
import {
  Button,
  Card,
  Col,
  Form,
  InputNumber,
  Modal,
  notification,
  Row,
  Statistic,
  Typography,
} from "antd";
import isNumeric from "antd/es/_util/isNumeric";
import {
  fetchProcessingCoverage,
  updateProcessingCoverage,
} from "../../../apis/projects/settings";

export function ProcessingCoverage({ projectId }) {
  const [visible, setVisible] = React.useState(false);
  const [coverage, setCoverage] = React.useState({});
  const [form] = Form.useForm();

  const getCoverage = React.useCallback(async () => {
    const data = await fetchProcessingCoverage(projectId);
    setCoverage(data);
  }, [projectId]);

  React.useEffect(() => {
    getCoverage();
  }, [getCoverage]);

  const numericValidator = () => ({
    validator(rule, value) {
      if (!value || isNumeric(value)) {
        return Promise.resolve();
      }
      return Promise.reject("Must be a numeric value");
    },
  });

  const update = () =>
    form.validateFields().then((values) => {
      updateProcessingCoverage(projectId, values)
        .then((message) => {
          setVisible(false);
          setCoverage(values);
          notification.success({ message });
        })
        .catch((message) => {
          setVisible(false);
          notification.info({ message });
        });
    });

  return (
    <section>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography.Title level={3}>
          {i18n("ProcessingCoverage.title")}
        </Typography.Title>
        <Button onClick={() => setVisible(true)}>
          {i18n("form.btn.edit")}
        </Button>
        <Modal
          title={i18n("ProcessingCoverage.modal.title")}
          visible={visible}
          onCancel={() => setVisible(false)}
          onOk={update}
        >
          <Form layout="vertical" initialValues={coverage} form={form}>
            <Form.Item
              label={i18n("ProcessingCoverage.minimum")}
              name="minimum"
              precision={0}
              rules={[numericValidator]}
            >
              <InputNumber style={{ width: `100%` }} step={100} min={0} />
            </Form.Item>
            <Form.Item
              label={i18n("ProcessingCoverage.maximum")}
              name="maximum"
              rules={[numericValidator]}
            >
              <InputNumber style={{ width: `100%` }} step={100} min={0} />
            </Form.Item>
            <Form.Item
              label={i18n("ProcessingCoverage.genomeSize")}
              name="genomeSize"
              rules={[numericValidator]}
            >
              <InputNumber style={{ width: `100%` }} step={100} min={0} />
            </Form.Item>
          </Form>
        </Modal>
      </div>
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.minimum")}
              value={coverage?.minimum}
              suffix="X"
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.maximum")}
              value={coverage?.maximum}
              suffix="X"
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.genomeSize")}
              value={coverage?.genomeSize}
              suffix="BP"
            />
          </Card>
        </Col>
      </Row>
    </section>
  );
}
