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

/**
 * Display and allow managers to be able to modify the minimum and maximum
 * coverage as well as the genome size.
 * @param {number} projectId - project identifier
 * @param {boolean} canManage - if the usr can manage this project
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingCoverage({ projectId, canManage }) {
  const NOT_SET = i18n("ProcessingCoverage.not-set");
  const [visible, setVisible] = React.useState(false);
  const [coverage, setCoverage] = React.useState({});
  const [form] = Form.useForm();

  const getCoverage = React.useCallback(async () => {
    const { minimum, maximum, genomeSize } = await fetchProcessingCoverage(
      projectId
    );
    setCoverage({
      minimum: minimum > -1 ? minimum : NOT_SET,
      maximum: maximum > -1 ? maximum : NOT_SET,
      genomeSize: genomeSize > -1 ? genomeSize : NOT_SET,
    });
  }, [projectId]);

  React.useEffect(() => {
    getCoverage();
  }, [getCoverage]);

  const numericValidator = () => ({
    validator(rule, value) {
      if (!value || isNumeric(value)) {
        return Promise.resolve();
      }
      return Promise.reject(i18n("ProcessingCoverage.numeric"));
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
        {canManage && (
          <span>
            <Button onClick={() => setVisible(true)}>
              {i18n("form.btn.edit")}
            </Button>
            <Modal
              title={i18n("ProcessingCoverage.modal.title")}
              visible={visible}
              onCancel={() => setVisible(false)}
              onOk={update}
            >
              <Form
                layout="vertical"
                initialValues={{
                  minimum: isNumeric(coverage.minimum)
                    ? coverage.minimum
                    : null,
                  maximum: isNumeric(coverage.maximum)
                    ? coverage.maximum
                    : null,
                  genomeSize: isNumeric(coverage.genomeSize)
                    ? coverage.genomeSize
                    : null,
                }}
                form={form}
              >
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
          </span>
        )}
      </div>
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.minimum")}
              value={isNumeric(coverage.minimum) ? coverage.minimum : NOT_SET}
              suffix={isNumeric(coverage.minimum) ? "X" : ""}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.maximum")}
              value={isNumeric(coverage.maximum) ? coverage.maximum : NOT_SET}
              suffix={isNumeric(coverage.maximum) ? "X" : ""}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={i18n("ProcessingCoverage.genomeSize")}
              value={
                isNumeric(coverage.genomeSize) ? coverage.genomeSize : NOT_SET
              }
              suffix={isNumeric(coverage.genomeSize) ? "BP" : ""}
            />
          </Card>
        </Col>
      </Row>
    </section>
  );
}
