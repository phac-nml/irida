import angular from "angular";
import "angular-ui-bootstrap";
import {
  Button,
  Card,
  Checkbox,
  Col,
  Divider,
  Form,
  Input,
  Layout,
  Row,
} from "antd";
import React from "react";
import { render } from "react-dom";
import { TAXONOMY } from "../../apis/ontology/taxonomy";
import { OntologySelect } from "../../components/ontology";
import { SPACE_LG } from "../../styles/spacing";
import { CART } from "../../utilities/events-utilities";
import "../../vendor/plugins/jquery/select2";

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

function CreateNewProject() {
  const [organism, setOrganism] = React.useState("");
  const [hasCart, setHasCart] = React.useState(false);
  const [form] = Form.useForm();

  React.useEffect(() => {
    function handleCart(event) {
      setHasCart(event.detail.count > 0);
    }

    document.addEventListener(CART.UPDATED, handleCart);
    return () => document.removeEventListener(handleCart);
  }, []);

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

  return (
    <Layout>
      <Row justify="center">
        <Col xs={24} md={18} xl={12}>
          <Content>
            <Card style={{ marginTop: SPACE_LG }}>
              <Form
                form={form}
                layout={"vertical"}
                validateMessages={validateMessages}
                onFinish={onFinish}
                onFinishFailed={onFinishFailed}
              >
                <Form.Item
                  name={["name"]}
                  label={i18n("projects.create.form.name")}
                  rules={[{ type: "string", min: 5, required: true }]}
                >
                  <Input type={"text"} />
                </Form.Item>
                <Form.Item
                  name={["organism"]}
                  label={i18n("projects.create.form.organism")}
                >
                  <OntologySelect
                    term={organism}
                    onTermSelected={setOrganism}
                    ontology={TAXONOMY}
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
                {hasCart && (
                  <>
                    <Divider />
                    <Form.Item name={"useCartSamples"} valuePropName="checked">
                      <Checkbox>
                        {i18n("projects.create.settings.cart")}
                      </Checkbox>
                    </Form.Item>
                    <Form.Item name={"lockSamples"} valuePropName="checked">
                      <Checkbox
                        rules={[
                          {
                            required: checkNick,
                            message: "Please input your nickname",
                          },
                        ]}
                      >
                        {i18n("projects.create.settings.sample.modification")}
                      </Checkbox>
                    </Form.Item>
                  </>
                )}
                <Form.Item noStyle>
                  <Button type="primary" htmlType="submit">
                    SUBMIT
                  </Button>
                </Form.Item>
              </Form>
            </Card>
          </Content>
        </Col>
      </Row>
    </Layout>
  );
}

render(<CreateNewProject />, document.querySelector("#root"));
