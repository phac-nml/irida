import {
  Button,
  Card,
  Checkbox,
  Col,
  Form,
  Input,
  Layout,
  List,
  Row,
  Space,
  Steps,
  Table,
} from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { useGetCartSamplesQuery } from "../../apis/cart/cart";
import { TAXONOMY } from "../../apis/ontology/taxonomy";
import { OntologySelect } from "../../components/ontology";
import { SampleDetailViewer } from "../../components/samples/SampleDetailViewer";
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
      <Button htmlType="submit">Next</Button>
    </Form>
  );
}

function NewProjectSamples({ samples, onNext }) {
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
  const [selectedSamples, setSelectedSamples] = React.useState([]);
  const [lock, setLock] = React.useState(false);

  return (
    <Space direction="vertical">
      <Table
        rowSelection={{
          type: "checkbox",
          selectedRowKeys: selected,
          onChange: (selectedRowKeys, selectedRows) => {
            setSelected(selectedRowKeys);
            setSelectedSamples(selectedRows);
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
      <Checkbox
        disabled={selected.length === 0}
        checked={lock}
        onChange={(e) => setLock(e.target.checked)}
      >
        {i18n("projects.create.settings.sample.modification")}
      </Checkbox>
      <Button onClick={() => onNext({ samples: selectedSamples })}>NEXT</Button>
    </Space>
  );
}

function NewProjectSummary({ details }) {
  return (
    <List itemLayout="horizontal">
      <List.Item>
        <List.Item.Meta title={"NAME"} description={details.name} />
      </List.Item>
      <List.Item>
        <List.Item.Meta
          title={"ORGANISM"}
          description={details.organism || "---"}
        />
      </List.Item>
      <List.Item>
        <List.Item.Meta
          title={"DESCRIPTION"}
          description={details.projectDescription}
        />
      </List.Item>
      <List.Item>
        <List.Item.Meta
          title={"Samples"}
          description={
            <List
              itemLayout="horizontal"
              dataSource={details.samples}
              renderItem={(sample) => <List.Item title={sample.label} />}
            />
          }
        />
      </List.Item>
    </List>
  );
}

function NewProjectLayout() {
  const [step, setStep] = React.useState(0);
  const [details, setDetails] = React.useState({});
  const { data: samples = {} } = useGetCartSamplesQuery();

  const updateDetails = (updates) => {
    setDetails({ ...details, ...updates });
    setStep(step + 1);
  };

  const steps = [
    {
      title: "DETAILS",
      content: <NewProjectDetails onNext={updateDetails} />,
    },
  ];

  if (samples.unlocked?.length) {
    steps.push({
      title: "SAMPLES",
      content: <NewProjectSamples onNext={updateDetails} samples={samples} />,
    });
  }

  steps.push({
    title: "SUMMARY",
    content: <NewProjectSummary details={details} />,
  });

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
