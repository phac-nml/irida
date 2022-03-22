import { Button, Col, Form, Modal, notification, Row, Steps } from "antd";
import React from "react";
import { createProject } from "../../../apis/projects/create";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { CreateProjectDetails } from "./CreateProjectDetails";
import { CreateProjectSamples } from "./CreateProjectSamples";
import { CreateProjectMetadataRestrictions } from "./CreateProjectMetadataRestrictions";
import { useSelector } from "react-redux";

/**
 * React component to handle the layout of the Create New Project and
 * display a modal to contain it.
 *
 * @param  {JSX.Element} children - A clickable element used to open the modal
 * @returns {JSX.Element}
 * @constructor
 */
export function CreateProjectLayout({ children }) {
  const [form] = Form.useForm();
  const [visible, setVisible] = React.useState(false);
  const [current, setCurrent] = React.useState(0);
  const [loading, setLoading] = React.useState(false);

  const { metadataRestrictions } = useSelector(
    (state) => state.newProjectReducer
  );

  const steps = [
    {
      title: i18n("CreateProjectLayout.details"),
      content: <CreateProjectDetails form={form} />,
    },
    {
      title: i18n("CreateProjectLayout.samples"),
      content: <CreateProjectSamples form={form} />,
    },
    {
      title: i18n("CreateProjectLayout.metadataRestrictions"),
      content: <CreateProjectMetadataRestrictions form={form} />,
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

  /**
   * Once the form is filled out, this is the submit to server call.
   * After a successful call, the user will be redirected to the new project.
   */
  const submit = () => {
    setLoading(true);
    form
      .validateFields()
      .then((values) => {
        let restrictions = metadataRestrictions.map(({ restriction, id }) => ({
          restriction,
          identifier: id,
        }));
        /*
         We add the metadataRestrictions to the values map as it was not a form item
         */
        values["metadataRestrictions"] = restrictions;
        createProject(values).then(
          ({ id }) => (window.location.href = setBaseUrl(`projects/${id}`))
        );
      })
      .catch(() => {
        notification.error({
          message: i18n("CreateProjectLayout.error"),
        });
        setLoading(false);
      });
  };

  /**
   * Handle the modal closing - reset the form!
   */
  const onCancel = () => {
    form.resetFields();
    setCurrent(0);
    setVisible(false);
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
                className="t-create-next-btn"
                onClick={() => {
                  form.validateFields().then(() => {
                    setCurrent(current + 1);
                  });
                }}
              >
                {i18n("CreateProjectLayout.next")}
              </Button>
            )}
            {current === steps.length - 1 && (
              <Button
                className="t-create-finish-btn"
                loading={loading}
                type="primary"
                onClick={submit}
              >
                {i18n("CreateProjectLayout.finish")}
              </Button>
            )}
            {current !== 0 && (
              <Button
                className="t-create-previous-btn"
                onClick={() => {
                  form.validateFields().then(() => setCurrent(current - 1));
                }}
              >
                {i18n("CreateProjectLayout.previous")}
              </Button>
            )}
          </div>
        }
        onCancel={onCancel}
        width={720}
        title={i18n("CreateProject.title")}
      >
        <Row>
          {
            <Col sm={10} md={6}>
              <Steps current={current} size="small" direction={"vertical"}>
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
              initialValues={{
                name: "",
                description: "",
                organism: "",
                remoteURL: "",
                lock: false,
                samples: [],
                metadataRestrictions: [],
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
