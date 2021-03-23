import { unwrapResult } from "@reduxjs/toolkit";
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
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchProjectCoverage,
  updateProcessingCoverage
} from "../../../redux/processingSlice";

/**
 * Display and allow managers to be able to modify the minimum and maximum
 * coverage as well as the genome size.
 * @param {number} projectId - project identifier
 * @param {boolean} canManage - if the usr can manage this project
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingCoverage({ projectId, canManage }) {
  const {
    minimum,
    maximum,
    genomeSize,
    loading
  } = useSelector(state => state.processing);
  console.log({ minimum, maximum, genomeSize });

  const dispatch = useDispatch();
  const NOT_SET = i18n("ProcessingCoverage.not-set");
  const [visible, setVisible] = React.useState(false);
  const [form] = Form.useForm();

  React.useEffect(() => {
    dispatch(fetchProjectCoverage(projectId));
  }, []);

  const numericValidator = () => ({
    validator(rule, value) {
      if (!value || isNumeric(value)) {
        return Promise.resolve();
      }
      return Promise.reject(i18n("ProcessingCoverage.numeric"));
    },
  });

  const update = () =>
    form.validateFields().then((coverage) => {
      dispatch(updateProcessingCoverage(
        {projectId, coverage}
      )).then(unwrapResult)
        .then(message => {
          notification.success({message});
          setVisible(false)
        })
        .catch(message => {
          notification.error({message})
          setVisible(false)
        })
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
                  minimum: isNumeric(minimum)
                    ? minimum
                    : null,
                  maximum: isNumeric(maximum)
                    ? maximum
                    : null,
                  genomeSize: isNumeric(genomeSize)
                    ? genomeSize
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
              loading={loading}
              title={i18n("ProcessingCoverage.minimum")}
              value={isNumeric(minimum) ? minimum : NOT_SET}
              suffix={isNumeric(minimum) ? "X" : ""}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              loading={loading}
              title={i18n("ProcessingCoverage.maximum")}
              value={isNumeric(maximum) ? maximum : NOT_SET}
              suffix={isNumeric(maximum) ? "X" : ""}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              loading={loading}
              title={i18n("ProcessingCoverage.genomeSize")}
              value={
                isNumeric(genomeSize) ? genomeSize : NOT_SET
              }
              suffix={isNumeric(genomeSize) ? "BP" : ""}
            />
          </Card>
        </Col>
      </Row>
    </section>
  );
}
