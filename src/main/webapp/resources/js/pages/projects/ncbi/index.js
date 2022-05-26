import React from "react";
import { render } from "react-dom";
import {
  Button,
  Cascader,
  Col,
  DatePicker,
  Form,
  Input,
  Layout,
  PageHeader,
  Row,
  Select,
  Table,
  Tooltip,
} from "antd";
import { MinusCircleOutlined } from "@ant-design/icons";
import * as moment from "moment";
import { grey1 } from "../../../styles/colors";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { configureStore } from "@reduxjs/toolkit";
import ncbiReducer, {
  fetchPlatforms,
  fetchSelections,
  fetchSources,
  fetchStrategies,
} from "./ncbiSlice";
import { Provider, useDispatch, useSelector } from "react-redux";
import { TableHeaderWithSelectOptions } from "../../../components/ant.design/TableHeaderWithSelectOptions";

function NCBIPage() {
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const strategyRef = React.useRef();
  const sourceRef = React.useRef();

  const [samples, setSamples] = React.useState(() => {
    const stored = window.sessionStorage.getItem("share");

    if (stored.length) {
      const storedJson = JSON.parse(stored);
      return storedJson.samples.reduce(
        (prev, { id, name }) => ({
          ...prev,
          [name]: {
            key: name,
            id,
            name,
            library_name: name,
          },
        }),
        {}
      );
    }
    return [];
  });

  const { platforms, selections, sources, strategies } = useSelector(
    (state) => state.ncbi
  );

  React.useEffect(() => {
    dispatch(fetchPlatforms());
    dispatch(fetchSources());
    dispatch(fetchStrategies());
    dispatch(fetchSelections());
  }, [dispatch]);

  function updateAllSamplesForField(column) {
    return (value) => {
      const formSamples = form.getFieldValue("samples");
      const updated = Object.values(formSamples).reduce(
        (prev, curr) => ({
          ...prev,
          [curr.name]: { ...curr, [column]: value },
        }),
        {}
      );
      form.setFieldsValue({ samples: updated });
    };
  }

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
      render: (_, item) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "BioSample ID is required" }]}
            name={["samples", item.name, "biosample"]}
            style={{ margin: 0 }}
          >
            <Input type="text" style={{ display: "block" }} />
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.library_name.title"),
      dataIndex: "library_strategy",
      key: "library_strategy",
      render: (_, item) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "Library Name is required" }]}
            name={["samples", item.name, "library_name"]}
            style={{ margin: 0 }}
          >
            <Input type="text" style={{ display: "block" }} />
          </Form.Item>
        );
      },
    },
    {
      title: () => {
        return (
          <TableHeaderWithSelectOptions
            options={strategies}
            title={i18n("project.export.library_strategy.title")}
            onChange={updateAllSamplesForField("library_strategy")}
            helpText={i18n("project.export.library_strategy.description")}
            ref={strategyRef}
          />
        );
      },
      dataIndex: "library_strategy",
      key: "library_strategy",
      render: (_, item) => {
        return (
          <Form.Item
            rules={[
              { required: true, message: "Library Strategy is required" },
            ]}
            name={["samples", item.name, "library_strategy"]}
            style={{
              margin: 0,
            }}
          >
            <Select
              style={{ display: "block" }}
              onChange={() => strategyRef.current.resetSelect()}
            >
              {strategies?.map((option) => (
                <Select.Option key={option}>{option}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        );
      },
    },
    {
      title: () => {
        return (
          <TableHeaderWithSelectOptions
            options={sources}
            title={i18n("project.export.library_source.title")}
            onChange={updateAllSamplesForField("library_source")}
            helpText={i18n("project.export.library_source.description")}
            ref={sourceRef}
          />
        );
      },
      dataIndex: "library_source",
      key: "library_source",
      render: (_, item) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "Library Source is required" }]}
            name={["samples", item.name, "library_source"]}
            style={{
              margin: 0,
            }}
          >
            <Select
              style={{ display: "block" }}
              onChange={() => sourceRef.current.resetSelect()}
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
      width: 200,
      render: (_, item) => {
        return (
          <Form.Item
            rules={[
              {
                required: true,
                message: "Library Construction Protocol is required",
              },
            ]}
            name={["samples", item.name, "library_construction_protocol"]}
            style={{ margin: 0 }}
          >
            <Input type="text" style={{ display: "block" }} />
          </Form.Item>
        );
      },
    },
    {
      title: i18n("project.export.instrument_model.title"),
      dataIndex: "instrument_model",
      key: "instrument_model",
      render: (_, item) => {
        return (
          <Form.Item
            rules={[
              {
                required: true,
                message: "Interment Model is required",
              },
            ]}
            name={["samples", item.name, "instrument_model"]}
            style={{
              margin: 0,
            }}
          >
            <Cascader options={platforms} style={{ display: "block" }} />
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
            rules={[
              {
                required: true,
                message: "Library Selection is required",
              },
            ]}
            name={["samples", item.name, "library_selection"]}
            style={{
              margin: 0,
            }}
          >
            <Select style={{ display: "block" }}>
              {selections?.map((option) => (
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
      fixed: "right",
      width: 60,
      render: (_, item) => {
        return (
          <Tooltip title={"Remove Sample"} placement="left">
            <Button
              shape="circle"
              type="text"
              icon={<MinusCircleOutlined />}
              onClick={() => {
                const copy = { ...samples };
                delete copy[item.name];
                setSamples(copy);
              }}
            />
          </Tooltip>
        );
      },
    },
  ];

  const validateAndSubmit = () => {
    // TODO: convert release date from momentjs
    form.validateFields().then(console.log);
  };

  return (
    <>
      <PageHeader
        title={i18n("project.export.title")}
        subTitle={i18n("project.export.files.description")}
      />
      <Layout>
        {/*<Layout.Sider theme="light">*/}
        {/*  <Steps direction="vertical" current={1}>*/}
        {/*    <Steps.Step title={"BioProject Details"} />*/}
        {/*    <Steps.Step title={"Sample Details"} />*/}
        {/*    <Steps.Step title={"Files"} />*/}
        {/*  </Steps>*/}
        {/*</Layout.Sider>*/}
        <Layout.Content style={{ backgroundColor: grey1 }}>
          <Form
            onFinish={validateAndSubmit}
            layout="vertical"
            form={form}
            initialValues={{ samples, release_date: moment(new Date()) }}
          >
            <Row gutter={[16, 16]}>
              <Col xs={24} sm={12}>
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
                <Form.Item
                  label={i18n("project.export.release_date.title")}
                  help={i18n("project.export.release_date.description")}
                  rules={[{ required: true }]}
                  name="release_date"
                >
                  <DatePicker />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Table
                  style={{ maxWidth: 1600 }}
                  scroll={{ x: "max-content" }}
                  columns={columns}
                  dataSource={Object.values(samples)}
                  pagination={getPaginationOptions(Object.keys(samples).length)}
                />
              </Col>
              <Col>
                <Button htmlType="submit" type="primary">
                  Send
                </Button>
              </Col>
            </Row>
          </Form>
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
