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
import { OntologySelect } from "../../components/ontology";
import { SPACE_LG } from "../../styles/spacing";
import "../../vendor/plugins/jquery/select2";
import store from "./create/store";

const { Content } = Layout;

function NewProjectDetails({ onNext }) {
  const [organism, setOrganism] = React.useState("");
  const nameRef = React.createRef();

  React.useEffect(() => {
    // Autofocus on the name input after loading
    nameRef.current.focus();
  }, [nameRef]);

  const validateMessages = {
    required: "THIS MUST BE THERE",
    string: {
      min: "THIS IS TOO SHORT",
    },
    url: "This does not look like a fucking url does it?",
  };

  return (
    <Form
      layout={"vertical"}
      validateMessages={validateMessages}
      onFinish={onNext}
    >
      <Form.Item
        name={["name"]}
        label={i18n("projects.create.form.name")}
        rules={[{ type: "string", min: 5, required: true }]}
      >
        <Input type={"text"} ref={nameRef} />
      </Form.Item>
      <Form.Item
        name={["organism"]}
        label={i18n("projects.create.form.organism")}
      >
        <OntologySelect
          term={organism}
          onTermSelected={setOrganism}
          ontology={TAXONOMY}
          autofocus={false}
        />
      </Form.Item>
      <Form.Item
        label={i18n("projects.create.form.description")}
        name="projectDescription"
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
      {/*{hasCart && (*/}
      {/*  <>*/}
      {/*    <Divider />*/}
      {/*    <Form.Item name={"useCartSamples"} valuePropName="checked">*/}
      {/*      <Checkbox onChange={onAddSamples}>*/}
      {/*        {i18n("projects.create.settings.cart")}*/}
      {/*      </Checkbox>*/}
      {/*    </Form.Item>*/}

      {/*  </>*/}
      {/*)}*/}
      <Button htmlType="submit">Next</Button>
    </Form>
  );
}

function NewProjectSamples({ samples }) {
  const [selected, setSelected] = React.useState([]);
  const [lock, setLock] = React.useState(false);

  return (
    <div>
      <Form>
        <Table
          rowSelection={{
            type: "checkbox",
            selectedRowKeys: selected,
            onChange: (selectedRowKeys) => setSelected(selectedRowKeys),
          }}
          scroll={{ y: 200 }}
          pagination={false}
          dataSource={samples.unlocked}
          rowKey={(sample) => `sample-${sample.identifier}`}
          columns={[
            { title: "Name", dataIndex: "label" },
            { title: "Organism", dataIndex: "organism" },
          ]}
        />
        <Form.Item>
          <Checkbox
            disabled={selected.length === 0}
            checked={lock}
            onChange={(e) => setLock(e.target.checked)}
          >
            {i18n("projects.create.settings.sample.modification")}
          </Checkbox>
        </Form.Item>
        <Form.Item>
          <Button>NEXT</Button>
        </Form.Item>
      </Form>
    </div>
  );
}

function NewProjectLayout() {
  const [step, setStep] = React.useState(0);
  const [details, setDetails] = React.useState({});
  const { data: samples = {} } = useGetCartSamplesQuery();

  const updateDetails = (updates) => {
    console.log({ updates, details });
    setDetails({ ...details, ...updates });
    setStep(step + 1);
  };

  const steps = [
    {
      title: "DETAILS",
      content: <NewProjectDetails onNext={updateDetails} />,
    },
  ];

  if (samples.locked?.length) {
    steps.push({
      title: "SAMPLES",
      content: <NewProjectSamples onNext={updateDetails} samples={samples} />,
    });
  }

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
                  {steps[step].content}
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
