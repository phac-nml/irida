import angular from "angular";
import "angular-ui-bootstrap";
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
import { TAXONOMY } from "../../apis/ontology/taxonomy";
import { OntologySelect } from "../../components/ontology";
import { SPACE_LG } from "../../styles/spacing";
import { CART } from "../../utilities/events-utilities";
import "../../vendor/plugins/jquery/select2";
import store from "./create/store";

const { Content } = Layout;

$("#new-organism-warning").hide();
$(".organism-select")
  .select2({
    minimumInputLength: 2,
    ajax: {
      // instead of writing the function to execute the request we use Select2's convenient helper
      url: window.PAGE.urls.taxonomy,
      dataType: "json",
      data: function (term, page) {
        return {
          searchTerm: term, // search term
        };
      },
      results: function (data, page) {
        // parse the results into the format expected by Select2.
        // since we are using custom formatting functions we do not need to alter remote JSON data
        return { results: data };
      },
    },
    initSelection: function (element, callback) {
      var organism = $(element).val();
      if (organism !== "") {
        $.ajax(window.PAGE.urls.taxonomy, {
          data: { searchTerm: organism },
          dataType: "json",
        }).done(function (data) {
          callback(data[0]);
        });
      }
    },
  })
  .on("change", function (data) {
    if (data.added.searchTerm) {
      $("#new-organism-warning").show();
    } else {
      $("#new-organism-warning").hide();
    }
  })
  .select2("val", window.PAGE.project.organism);

$("#useCartSamples").change(function () {
  showHideCart(100);
});

showHideCart(0);

function showHideCart(time) {
  if ($("#useCartSamples").prop("checked")) {
    $("#cartAlert").show(time);
  } else {
    $("#cartAlert").hide(time);
  }
}

/*
    Disables/enables the lockSamples checkbox
    depending on if using the samples from cart
*/
$("#useCartSamples").on("change", function (e) {
  var selected = $(e.target).prop("checked");
  if (!selected) {
    $("#lockSamples").attr("disabled", true);
    $("#lockSamples").prop("checked", false);
  } else {
    $("#lockSamples").removeAttr("disabled");
  }
});

angular
  .module("irida.project.new", ["ui.bootstrap"])
  .controller("NewProjectController", function () {
    var vm = this;
    vm.project = {
      name: window.PAGE.project.name,
      remoteURL: window.PAGE.project.remoteURL,
    };
  });

function NewProjectDetails() {
  const [organism, setOrganism] = React.useState("");
  const nameRef = React.createRef();

  React.useEffect(() => {
    // Autofocus on the name input after loading
    nameRef.current.focus();
  }, [nameRef]);

  return (
    <>
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
    </>
  );
}

function NewProjectSamples() {
  const [unlocked, setUnlocked] = React.useState();
  const [locked, setLocked] = React.useState();
  const [selected, setSelected] = React.useState([]);

  React.useEffect(() => {
    // Since we are here, there must be samples
    fetch(`/ajax/projects/new/samples`)
      .then((response) => response.json())
      .then((data) => {
        setUnlocked(data.unlocked);
        setLocked(data.locked);
        setSelected(
          data.unlocked.map((sample) => `sample-${sample.identifier}`)
        );
      });
  }, []);

  return (
    <div>
      <Table
        rowSelection={{
          type: "checkbox",
          selectedRowKeys: selected,
          onChange: (selectedRowKeys) => setSelected(selectedRowKeys),
        }}
        scroll={{ y: 200 }}
        pagination={false}
        dataSource={unlocked}
        rowKey={(sample) => `sample-${sample.identifier}`}
        columns={[
          { title: "Name", dataIndex: "label" },
          { title: "Organism", dataIndex: "organism" },
        ]}
      />
      <Form.Item name={"lockSamples"} valuePropName="checked">
        <Checkbox disabled={selected.length === 0}>
          {i18n("projects.create.settings.sample.modification")}
        </Checkbox>
      </Form.Item>
    </div>
  );
}

function NewProjectWithSteps() {
  const [step, setStep] = React.useState(0);

  return (
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
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <Button disabled={step === 0} onClick={() => setStep(step - 1)}>
            PREVIOUS
          </Button>
          <Button
            disabled={step === steps.length - 1}
            onClick={() => setStep(step + 1)}
          >
            Next
          </Button>
          {step === steps.length - 1 && (
            <Form.Item noStyle>
              <Button type="primary" htmlType="submit">
                {i18n("projects.create.form.create")}
              </Button>
            </Form.Item>
          )}
        </div>
      </Col>
    </Row>
  );
}

function NewProjectLayout() {
  const [hasCart, setHasCart] = React.useState(false);
  const [step, setStep] = React.useState(0);

  const [form] = Form.useForm();
  const steps = [
    {
      title: "DETAILS",
      content: <NewProjectDetails />,
    },
    {
      title: "SAMPLES",
      content: <NewProjectSamples />,
    },
  ];

  const validateMessages = {
    required: "THIS MUST BE THERE",
    string: {
      min: "THIS IS TOO SHORT",
    },
    url: "This does not look like a fucking url does it?",
  };

  const onFinish = (values) => {
    console.log("Success:", values);
    console.log(form);
  };

  const onFinishFailed = (errorInfo) => {
    console.log("Failed:", errorInfo);
  };

  const onAddSamples = (e) => {
    setDisableLockSamples(!e.target.checked);
  };

  React.useEffect(() => {
    function handleCart(event) {
      setHasCart(event.detail.count > 0);
      // Once this is set, we don't need to listen for updates since there
      // cannot be any.
      document.removeEventListener(CART.UPDATED, handleCart);
    }

    document.addEventListener(CART.UPDATED, handleCart);
  }, []);

  return (
    <Layout>
      <Row justify="center">
        <Col xs={23} md={18} xl={12} xxl={8}>
          <Content style={{ marginTop: SPACE_LG }}>
            <Card>
              <Form
                layout={"vertical"}
                validateMessages={validateMessages}
                onFinish={onFinish}
                onFinishFailed={onFinishFailed}
              >
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
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <Button
                      disabled={step === 0}
                      onClick={() => setStep(step - 1)}
                    >
                      PREVIOUS
                    </Button>
                    <Button
                      disabled={step === steps.length - 1}
                      onClick={() => setStep(step + 1)}
                    >
                      Next
                    </Button>
                    {step === steps.length - 1 && (
                      <Form.Item noStyle>
                        <Button type="primary" htmlType="submit">
                          {i18n("projects.create.form.create")}
                        </Button>
                      </Form.Item>
                    )}
                  </div>
                </Col>
              </Form>
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
