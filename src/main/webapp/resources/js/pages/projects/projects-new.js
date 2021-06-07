import {
  Button,
  Card,
  Checkbox,
  Col,
  Form,
  Input,
  Layout,
  Row,
  Steps,
  Table,
} from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { useGetCartSamplesQuery } from "../../apis/cart/cart";
import { TAXONOMY } from "../../apis/ontology/taxonomy";
import { createProject } from "../../apis/projects/create";
import { OntologySelect } from "../../components/ontology";
import { SampleDetailViewer } from "../../components/samples/SampleDetailViewer";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import "../../vendor/plugins/jquery/select2";
import store from "./create/store";

const { Content } = Layout;

function NewProjectDetails({ form }) {
  const nameRef = React.createRef();
  const [organism, setOrganism] = React.useState();

  React.useEffect(() => {
    // Autofocus on the name input after loading
    nameRef.current.focus();
  }, [nameRef]);

  const setFormOrganism = (value) => {
    form.setFieldsValue({ organism: value });
  };

  return (
    <>
      <Form.Item
        name="name"
        label={i18n("projects.create.form.name")}
        rules={[{ type: "string", min: 5, required: true }]}
      >
        <Input type={"text"} ref={nameRef} />
      </Form.Item>
      <Form.Item
        name={"organism"}
        label={i18n("projects.create.form.organism")}
      >
        <OntologySelect
          term={organism}
          onTermSelected={setFormOrganism}
          ontology={TAXONOMY}
          autofocus={false}
        />
      </Form.Item>
      <Form.Item
        label={i18n("projects.create.form.description")}
        name="description"
      >
        <Input.TextArea />
      </Form.Item>
      <Form.Item
        name={"remoteURL"}
        label={i18n("projects.create.form.wiki")}
        rules={[{ type: "url", required: false }]}
      >
        <Input type="url" />
      </Form.Item>
    </>
  );
}

function NewProjectSamples({ form, samples }) {
  const [organismFilter] = React.useState(() => {
    const exists = {};
    samples.unlocked.forEach((sample) => {
      if (!exists[sample.organism]) {
        exists[sample.organism] = {
          text: sample.organism,
          value: sample.organism,
        };
      }
    });
    return Object.values(exists);
  });
  const [selected, setSelected] = React.useState([]);
  const [lock, setLock] = React.useState(false);

  return (
    <>
      <Table
        rowSelection={{
          type: "checkbox",
          selectedRowKeys: selected,
          onChange: (selectedRowKeys, selectedRows) => {
            setSelected(selectedRowKeys);
            form.setFieldsValue({
              samples: selectedRows.map((s) => Number(s.identifier)),
            });
          },
        }}
        scroll={{ y: 600 }}
        pagination={false}
        dataSource={samples.unlocked}
        rowKey={(sample) => `sample-${sample.identifier}`}
        columns={[
          {
            title: "Name",
            dataIndex: "label",
            render: (text, sample) => (
              <SampleDetailViewer sampleId={sample.identifier}>
                <Button size="small">{sample.label}</Button>
              </SampleDetailViewer>
            ),
            onFilter: (value, record) =>
              record.label.toLowerCase().indexOf(value.toLowerCase()) >= 0,
          },
          {
            title: "Organism",
            dataIndex: "organism",
            filters: organismFilter,
            onFilter: (value, record) => record.organism === value,
          },
        ]}
      />
      <Form.Item name="samples" hidden>
        <Input />
      </Form.Item>
      <Form.Item name="lock" valuePropName="checked">
        <Checkbox
          disabled={selected.length === 0}
          checked={lock}
          onChange={(e) => setLock(e.target.checked)}
        >
          {i18n("projects.create.settings.sample.modification")}
        </Checkbox>
      </Form.Item>
    </>
  );
}

function NewProjectLayout() {
  const [form] = Form.useForm();
  const [step, setStep] = React.useState(0);
  const [loading, setLoading] = React.useState(false);
  const { data: samples = {} } = useGetCartSamplesQuery();

  const steps = [
    {
      title: "DETAILS",
      content: <NewProjectDetails form={form} />,
    },
  ];

  if (samples.unlocked?.length) {
    steps.push({
      title: "SAMPLES",
      content: <NewProjectSamples form={form} samples={samples} />,
    });
  }

  const validateMessages = {
    required: "THIS MUST BE THERE",
    string: {
      min: "THIS IS TOO SHORT",
    },
    url: "This does not look like a fucking url does it?",
  };

  const submit = (values) => {
    setLoading(true);
    createProject(values).then(
      ({ id }) => (window.location.href = setBaseUrl(`projects/${id}`))
    );
  };

  return (
    <Layout>
      <Row justify="center">
        <Col xs={23} md={20} xl={16} xxl={8}>
          <Content style={{ marginTop: SPACE_LG }}>
            <Card>
              <Row>
                <Col sm={10} md={6}>
                  <Steps
                    progressDot
                    current={step}
                    style={{ marginBottom: SPACE_LG }}
                    direction={"vertical"}
                  >
                    {steps.map((item) => (
                      <Steps.Step key={item.title} title={item.title} />
                    ))}
                  </Steps>
                </Col>
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
                    {steps.map((s, index) => (
                      <div
                        key={`step-${index}`}
                        style={{ display: step === index ? "block" : "none" }}
                      >
                        {s.content}
                      </div>
                    ))}
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        flexDirection: "row-reverse",
                      }}
                    >
                      {step !== steps.length - 1 && (
                        <Button
                          onClick={() => {
                            form.validateFields().then(() => {
                              setStep(step + 1);
                            });
                          }}
                        >
                          NEXT
                        </Button>
                      )}
                      {step === steps.length - 1 && (
                        <Button
                          htmlType="submit"
                          loading={loading}
                          type="primary"
                        >
                          CREATE
                        </Button>
                      )}
                      {step !== 0 && (
                        <Button
                          onClick={() => {
                            form.validateFields().then(() => setStep(step - 1));
                          }}
                        >
                          PREVIOUS
                        </Button>
                      )}
                    </div>
                  </Form>
                </Col>
              </Row>
            </Card>
          </Content>
        </Col>
      </Row>
    </Layout>
  );
}

render(
  <Provider store={store}>
    <NewProjectLayout />
  </Provider>,
  document.querySelector("#root")
);
