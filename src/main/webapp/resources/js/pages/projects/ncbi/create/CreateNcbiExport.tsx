import { MinusCircleOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
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
import type { RangePickerProps } from "antd/es/date-picker";
import moment from "moment";
import React from "react";
import { useLoaderData } from "react-router-dom";
import {
  FullNcbiPlatforms,
  getNCBIPlatforms,
  getNCBISelections,
  getNCBISources,
  getNCBIStrategies,
} from "../../../../apis/export/ncbi";
import { TableHeaderWithCascaderOptions } from "../../../../components/ant.design/TableHeaderWithCascaderOptions";
import { TableHeaderWithSelectOptions } from "../../../../components/ant.design/TableHeaderWithSelectOptions";
import TextWithHelpPopover from "../../../../components/ant.design/TextWithHelpPopover";
import {
  NcbiBiosample,
  NcbiPlatform,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
} from "../../../../types/irida";
import {
  getSharedSamples,
  SharedStorage,
} from "../../../../utilities/share-utilities";

interface SampleRecord {
  [k: string]: NcbiBiosample;
}

export async function loader(): Promise<
  [
    SharedStorage,
    FullNcbiPlatforms,
    NcbiStrategy[],
    NcbiSource[],
    NcbiSelection[]
  ]
> {
  const stored = getSharedSamples();
  const platforms = getNCBIPlatforms();
  const strategies = getNCBIStrategies();
  const sources = getNCBISources();
  const selections = getNCBISelections();
  return Promise.all([stored, platforms, strategies, sources, selections]);
}

function CreateNcbiExport(): JSX.Element {
  const [stored, rawPlatforms, strategies, sources, selections] =
    useLoaderData();

  const [samples, setSamples] = React.useState<SampleRecord>(() =>
    stored.samples.reduce(
      (prev: SampleRecord, { id, name }: { id: string; name: string }) => ({
        ...prev,
        [name]: {
          key: name,
          id,
          name,
          library_name: name,
        },
      }),
      {}
    )
  );

  const [platforms] = React.useState<CascaderOption[]>(() =>
    Object.keys(rawPlatforms).map((platform) => ({
      value: platform,
      label: platform,
      children: rawPlatforms[platform].map((child: NcbiPlatform) => ({
        value: child,
        label: child,
      })),
    }))
  );

  const [form] = Form.useForm();
  const strategyRef = React.useRef();
  const sourceRef = React.useRef();
  const platformRef = React.useRef();

  const disabledDate: RangePickerProps["disabledDate"] = (date): boolean => {
    // Can not select days before today for release
    return date && date < moment().startOf("day");
  };

  function updateAllSamplesForField(column: string) {
    return (value: string) => {
      const formSamples: NcbiBiosample[] = form.getFieldValue("samples");
      const updated = Object.values(formSamples).reduce(
        (prev: SampleRecord, curr: NcbiBiosample): SampleRecord => {
          return {
            ...prev,
            [curr.name]: { ...curr, [column]: value },
          };
        },
        {}
      );
      form.setFieldsValue({ samples: updated });
    };
  }

  const width = 200;
  const columns = [
    {
      title: "_SAMPLE",
      dataIndex: "name",
      width,
      key: "sample",
      fixed: "left",
    },
    {
      title: (
        <TextWithHelpPopover
          text={i18n("project.export.biosample.title")}
          help={i18n("project.export.biosample.description")}
        />
      ),
      dataIndex: "biosample",
      width,
      key: "biosample",
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "BioSample ID is required" }]}
            name={["samples", sample.name, "biosample"]}
            style={{ margin: 0 }}
          >
            <Input type="text" style={{ display: "block" }} />
          </Form.Item>
        );
      },
    },
    {
      title: (
        <TextWithHelpPopover
          text={i18n("project.export.library_name.title")}
          help={i18n("project.export.library_name.description")}
        />
      ),
      dataIndex: "library_strategy",
      width,
      key: "library_strategy",
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "Library Name is required" }]}
            name={["samples", sample.name, "library_name"]}
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
      width,
      key: "library_strategy",
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[
              { required: true, message: "Library Strategy is required" },
            ]}
            name={["samples", sample.name, "library_strategy"]}
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
      width,
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[{ required: true, message: "Library Source is required" }]}
            name={["samples", sample.name, "library_source"]}
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
      width,
      key: "library_construction_protocol",
      width: 200,
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[
              {
                required: true,
                message: "Library Construction Protocol is required",
              },
            ]}
            name={["samples", sample.name, "library_construction_protocol"]}
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
          <TableHeaderWithCascaderOptions
            options={platforms}
            title={i18n("project.export.instrument_model.title")}
            onChange={updateAllSamplesForField("instrument_model")}
            helpText={i18n("project.export.instrument_model.description")}
            ref={platformRef}
          />
        );
      },
      dataIndex: "instrument_model",
      width,
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
            <Cascader
              options={platforms}
              style={{ display: "block" }}
              onChange={() => platformRef.current.resetSelect()}
            />
          </Form.Item>
        );
      },
    },
    {
      title: () => {
        return (
          <TableHeaderWithSelectOptions
            options={strategies}
            title={i18n("project.export.library_selection.title")}
            onChange={updateAllSamplesForField("library_selection")}
            helpText={i18n("project.export.library_selection.description")}
            ref={strategyRef}
          />
        );
      },
      dataIndex: "library_selection",
      width,
      key: "library_selection",
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[
              {
                required: true,
                message: "Library Selection is required",
              },
            ]}
            name={["samples", sample.name, "library_selection"]}
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
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Tooltip title={"Remove Sample"} placement="left">
            <Button
              shape="circle"
              type="text"
              icon={<MinusCircleOutlined />}
              onClick={() => {
                const copy = { ...samples };
                delete copy[sample.name];
                setSamples(copy);
              }}
            />
          </Tooltip>
        );
      },
    },
  ];

  const validateAndSubmit = (): void => {
    // TODO: convert release date from momentjs
    form.validateFields().then(console.log);
  };

  return (
    <Layout.Content>
      <PageHeader title={i18n("project.export.title")}>
        <Form
          layout="vertical"
          initialValues={{
            release_date: moment(new Date()),
            samples,
          }}
          form={form}
          onFinish={validateAndSubmit}
        >
          <Row gutter={[16, 16]}>
            <Col
              xxl={{ span: 16, offset: 4 }}
              xl={{ span: 20, offset: 2 }}
              sm={24}
            >
              <Card title={"Export Details"}>
                <Row gutter={[16, 16]}>
                  <Col md={12} xs={24}>
                    <Form.Item
                      required
                      label={i18n("project.export.bioproject.title")}
                      help={i18n("project.export.bioproject.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col md={12} xs={24}>
                    <Form.Item
                      required
                      label={i18n("project.export.organization.title")}
                      help={i18n("project.export.organization.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col md={12} xs={24}>
                    <Form.Item
                      required
                      label={i18n("project.export.namespace.title")}
                      help={i18n("project.export.namespace.description")}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col md={12} xs={24}>
                    <Form.Item
                      required
                      label={i18n("project.export.release_date.title")}
                      help={i18n("project.export.release_date.description")}
                      name="release_date"
                    >
                      <DatePicker
                        style={{ width: "100%" }}
                        disabledDate={disabledDate}
                      />
                    </Form.Item>
                  </Col>
                </Row>
              </Card>
            </Col>
            <Col span={24}>
              <Table
                columns={columns}
                dataSource={Object.values(samples)}
                style={{ width: `100%` }}
                scroll={{ x: "max-content", y: 600 }}
                pagination={false}
              />
            </Col>
            <Col
              xxl={{ span: 16, offset: 4 }}
              xl={{ span: 20, offset: 2 }}
              sm={24}
            >
              <Button type="primary" htmlType="submit">
                __SUBMIT
              </Button>
            </Col>
          </Row>
        </Form>
      </PageHeader>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
