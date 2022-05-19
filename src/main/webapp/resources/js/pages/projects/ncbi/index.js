import React from "react";
import { render } from "react-dom";
import {
  Button,
  Cascader,
  Col,
  Form,
  Input,
  Layout,
  PageHeader,
  Row,
  Select,
  Steps,
  Table,
} from "antd";
import { MinusCircleOutlined } from "@ant-design/icons";
import { grey1 } from "../../../styles/colors";
import {
  LIBRARY_SELECTION_OPTIONS,
  LIBRARY_STRATEGY_OPTIONS,
} from "./contstants";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { configureStore } from "@reduxjs/toolkit";
import ncbiReducer, { fetchPlatforms, fetchSources } from "./ncbiSlice";
import { Provider, useDispatch, useSelector } from "react-redux";

function NCBIPage() {
  const dispatch = useDispatch();
  const [form] = Form.useForm();

  const { platforms, sources } = useSelector((state) => state.ncbi);

  React.useEffect(() => {
    dispatch(fetchPlatforms());
    dispatch(fetchSources());
  }, [dispatch]);

  const samples = (() => {
    const stored = window.sessionStorage.getItem("share");
    const storedJson = stored && stored.length ? JSON.parse(stored) : [];

    if (stored.length) {
      const storedJson = JSON.parse(stored);
      return storedJson.samples.map(({ id, name }) => ({
        key: name,
        id,
        name,
        bioSample: "",
        instrument_model: "",
        library_name: name,
        library_construction_protocol: "",
        library_strategy: "",
        library_selection: "",
        library_source: "",
      }));
    }
    return [];
  })();

  React.useEffect(() => {
    fetch(setBaseUrl(`/ajax/export/ncbi/sources`))
      .then((response) => response.json())
      .then((data) => {
        setSources(data);
      });
  }, []);

  const columns = [
    {
      title: "_SAMPLE",
      dataIndex: "name",
      key: "sample",
      fixed: "left",
    },
    {
      title: i18n("project.export.biosample.title"),
      dataIndex: "biosample",
      key: "biosample",
    },
    {
      title: i18n("project.export.library_name.title"),
      dataIndex: "library_strategy",
      key: "library_strategy",
      render: (_, item) => {
        return (
          <Form.Item name={[item.name, "library_name"]} style={{ margin: 0 }}>
            <Input type="text" defaultValue={item.library_name} />
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.library_strategy.title"),
      dataIndex: "library_strategy",
      key: "library_strategy",
      render: (_, item) => {
        return (
          <Form.Item
            name={[item.name, "library_strategy"]}
            style={{
              margin: 0,
            }}
          >
            <Select
              style={{ width: `100%` }}
              defaultValue={LIBRARY_STRATEGY_OPTIONS[0]}
            >
              {LIBRARY_STRATEGY_OPTIONS.map((option) => (
                <Select.Option key={option}>{option}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.library_source.title"),
      dataIndex: "library_source",
      key: "library_source",
      width: 220,
      render: (_, item) => {
        return (
          <Form.Item
            name={[item.name, "library_source"]}
            style={{
              margin: 0,
            }}
          >
            <Select
              style={{ width: `100%` }}
              defaultValue={item.library_source}
            >
              {sources?.map((option) => (
                <Select.Option key={option}>{option}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.library_construction_protocol.title"),
      dataIndex: "library_construction_protocol",
      key: "library_construction_protocol",
    },
    {
      title: i18n("project.export.instrument_model.title"),
      dataIndex: "instrument_model",
      key: "instrument_model",
      width: 220,
      render: (_, item) => {
        return (
          <Form.Item
            name={[item.name, "instrument_model"]}
            style={{
              margin: 0,
            }}
          >
            <Cascader options={platforms} />
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.library_selection.title"),
      dataIndex: "library_selection",
      key: "library_selection",
      render: (_, item) => {
        return (
          <Form.Item
            name={[item.name, "library_selection"]}
            style={{
              margin: 0,
            }}
          >
            <Select
              style={{ width: `100%` }}
              defaultValue={item.library_selection}
            >
              {LIBRARY_SELECTION_OPTIONS.map((option) => (
                <Select.Option key={option}>{option}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        );
      },
    },
    {
      title: "",
      dataIndex: "actions",
      key: "actions",
      render: () => {
        return (
          <Button shape="circle" type="text" icon={<MinusCircleOutlined />} />
        );
      },
    },
  ];

  const validateAndSubmit = () => {
    form.validateFields().then(console.log);
  };

  return (
    <>
      <PageHeader
        title={i18n("project.export.title")}
        subTitle={i18n("project.export.files.description")}
      />
      <Layout>
        <Layout.Sider theme="light">
          <Steps direction="vertical" current={1}>
            <Steps.Step title={"BioProject Details"} />
            <Steps.Step title={"Sample Details"} />
            <Steps.Step title={"Files"} />
          </Steps>
        </Layout.Sider>
        <Layout.Content style={{ backgroundColor: grey1 }}>
          <Row gutter={[16, 16]}>
            <Col>
              <Form
                onFinish={validateAndSubmit}
                layout="vertical"
                form={form}
                initialValues={{ samples }}
              >
                <Form.Item
                  label={i18n("project.export.bioproject.title")}
                  help={i18n("project.export.bioproject.description")}
                  rules={[{ required: true }]}
                  name="bioProject"
                >
                  <Input type="text" />
                </Form.Item>
                <Form.Item
                  label={i18n("project.export.organization.title")}
                  help={i18n("project.export.organization.description")}
                  rules={[{ required: true }]}
                  name="organization"
                >
                  <Input type="text" />
                </Form.Item>
                <Form.Item
                  label={i18n("project.export.namespace.title")}
                  help={i18n("project.export.namespace.description")}
                  rules={[{ required: true }]}
                  name="namespace"
                >
                  <Input type="text" />
                </Form.Item>
                {/* TODO: Datepicker */}
                <Form.Item
                  label={i18n("project.export.release_date.title")}
                  help={i18n("project.export.release_date.description")}
                  rules={[{ required: true }]}
                  name="release_date"
                >
                  <Input type="text" />
                </Form.Item>
                <Table
                  columns={columns}
                  dataSource={samples}
                  pagination={getPaginationOptions(samples.length)}
                />
                <Button htmlType="submit" type="primary">
                  Send
                </Button>
              </Form>
            </Col>
          </Row>
        </Layout.Content>
      </Layout>
    </>
  );
}

const store = configureStore({
  reducer: {
    ncbi: ncbiReducer,
  },
  devTools: process.env.NODE_ENV !== "production",
});

render(
  <Provider store={store}>
    <NCBIPage />
  </Provider>,
  document.querySelector("#root")
);
