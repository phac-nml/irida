import { Button, Col, Form, Modal, Row, Steps } from "antd";
import React from "react";
import { createProject } from "../../../apis/projects/create";
import { SPACE_LG } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { CreateProjectDetails } from "./CreateProjectDetails";
import { CreateProjectSamples } from "./CreateProjectSamples";

export function CreateProjectLayout({ children }) {
  const [form] = Form.useForm();
  const [visible, setVisible] = React.useState(false);
  const [current, setCurrent] = React.useState(0);
  const [loading, setLoading] = React.useState(false);

  const steps = [
    {
      title: "DETAILS",
      content: <CreateProjectDetails form={form} />,
    },
    {
      title: "SAMPLES",
      content: <CreateProjectSamples form={form} />,
    },
  ];

  const validateMessages = {
    required: i18n("CreateProjectDetails.required"),
    string: {
      min: i18n("CreateProjectDetails.length"),
    },
    types: {
      url: i18n("CreateProjectDetails.url"),
    },
  };

  const submit = (values) => {
    setLoading(true);
    createProject(values).then(
      ({ id }) => (window.location.href = setBaseUrl(`projects/${id}`))
    );
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Modal
        visible={visible}
        footer={
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              flexDirection: "row-reverse",
            }}
          >
            {current !== steps.length - 1 && (
              <Button
                onClick={() => {
                  form.validateFields().then(() => {
                    setCurrent(current + 1);
                  });
                }}
              >
                NEXT
              </Button>
            )}
            {current === steps.length - 1 && (
              <Button htmlType="submit" loading={loading} type="primary">
                CREATE
              </Button>
            )}
            {current !== 0 && (
              <Button
                onClick={() => {
                  form.validateFields().then(() => setCurrent(current - 1));
                }}
              >
                PREVIOUS
              </Button>
            )}
          </div>
        }
        onCancel={() => setVisible(false)}
        width={720}
        title={i18n("CreateProject.title")}
      >
        <Row>
          {
            <Col sm={10} md={6}>
              <Steps
                progressDot
                current={current}
                style={{ marginBottom: SPACE_LG }}
                direction={"vertical"}
              >
                {steps.map((step) => (
                  <Steps.Step key={step.title} title={step.title} />
                ))}
              </Steps>
            </Col>
          }
          <Col sm={14} md={18}>
            <Form
              form={form}
              layout="vertical"
              validateMessages={validateMessages}
              onFinish={submit}
              initialValues={{
                name: "",
                description: "",
                organism: "",
                remoteURL: "",
                lock: false,
                samples: [],
              }}
            >
              {steps.map((step, index) => (
                <div
                  key={`step-${index}`}
                  style={{
                    display: current === index ? "block" : "none",
                    flex: `1 1 auto`,
                  }}
                >
                  {step.content}
                </div>
              ))}
            </Form>
          </Col>
        </Row>
      </Modal>
    </>
  );
}
