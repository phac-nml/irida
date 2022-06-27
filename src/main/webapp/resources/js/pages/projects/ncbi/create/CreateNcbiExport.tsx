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
import { ColumnType } from "antd/lib/table";
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
import type { TableWithOptionsHandles } from "../../../../components/ant.design/table";
import { TableHeaderWithCascaderOptions } from "../../../../components/ant.design/table/TableHeaderWithCascaderOptions";
import { TableHeaderWithSelectOptions } from "../../../../components/ant.design/table/TableHeaderWithSelectOptions";
import TextWithHelpPopover from "../../../../components/ant.design/TextWithHelpPopover";
import type { Option } from "../../../../types/ant-design";
import type {
  NcbiBiosample,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
} from "../../../../types/irida";
import {
  getStoredSamples,
  SessionSample,
} from "../../../../utilities/session-utilities";
import {
  formatPlatformsAsCascaderOptions,
  formatStoredAsNcbiBiosample,
} from "./ncbi-utilities";

export type SampleRecord = {
  [k: string]: NcbiBiosample;
};

type LoaderValues = [
  SessionSample,
  FullNcbiPlatforms,
  NcbiStrategy[],
  NcbiSource[],
  NcbiSelection[]
];

/**
 * React router loader
 */
export async function loader(): Promise<LoaderValues> {
  const stored = getStoredSamples();
  const platforms = getNCBIPlatforms();
  const strategies = getNCBIStrategies();
  const sources = getNCBISources();
  const selections = getNCBISelections();
  return Promise.all([stored, platforms, strategies, sources, selections]);
}

function CreateNcbiExport(): JSX.Element {
  const [
    stored,
    fullNcbiPlatforms,
    strategies,
    sources,
    selections,
  ]: LoaderValues = useLoaderData();

  const [samples, setSamples] = React.useState<SampleRecord>(
    (): SampleRecord => stored.samples.reduce(formatStoredAsNcbiBiosample, {})
  );

  const [platforms] = React.useState<Option[]>(() =>
    formatPlatformsAsCascaderOptions(fullNcbiPlatforms)
  );

  const [form] = Form.useForm();
  const strategyRef = React.useRef<TableWithOptionsHandles>(null);
  const sourceRef = React.useRef<TableWithOptionsHandles>(null);
  const platformRef = React.useRef<TableWithOptionsHandles>(null);

  /*
  This prevents the release date to be in the past.
   */
  const disabledDate: RangePickerProps["disabledDate"] = (date): boolean => {
    // Can not select days before today for release
    return date && date < moment().startOf("day");
  };

  /**
   * Update the values for all samples based on the column name.
   * @param column Name of the column (corresponds to attribute on sample).
   * @return setter function that takes the value to set
   */
  function updateAllSamplesForField(column: string): (value: string) => void {
    /**
     *
     */
    return (value: string): void => {
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

  function clearSelectByRef(
    selectRef: React.RefObject<TableWithOptionsHandles>
  ) {
    if (selectRef !== null) {
      selectRef.current?.resetSelect();
    }
  }

  const DEFAULT_COLUMN_WIDTH = 200;
  const columns: ColumnType<NcbiBiosample>[] = [
    {
      title: "_SAMPLE",
      dataIndex: "name",
      width: DEFAULT_COLUMN_WIDTH,
      key: "sample",
      fixed: "left",
    },
    {
      title: (
        <TextWithHelpPopover
          text={i18n("CreateNcbiExport.biosample-id")}
          help={i18n("CreateNcbiExport.biosample-id.desc")}
        />
      ),
      dataIndex: "biosample",
      width: DEFAULT_COLUMN_WIDTH,
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
      width: DEFAULT_COLUMN_WIDTH,
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
      width: DEFAULT_COLUMN_WIDTH,
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
              onChange={() => clearSelectByRef(strategyRef)}
            >
              {strategies?.map((option: string) => (
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
      width: DEFAULT_COLUMN_WIDTH,
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
              onChange={() => clearSelectByRef(sourceRef)}
            >
              {sources.map((option: string) => (
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
      width: DEFAULT_COLUMN_WIDTH,
      key: "library_construction_protocol",
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
      width: DEFAULT_COLUMN_WIDTH,
      key: "instrument_model",
      render: (text: string, sample: NcbiBiosample) => {
        return (
          <Form.Item
            rules={[
              {
                required: true,
                message: "Interment Model is required",
              },
            ]}
            name={["samples", sample.name, "instrument_model"]}
            style={{
              margin: 0,
            }}
          >
            <Cascader
              options={platforms}
              style={{ display: "block" }}
              onChange={() => clearSelectByRef(platformRef)}
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
      width: DEFAULT_COLUMN_WIDTH,
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
              {selections.map((option) => (
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
