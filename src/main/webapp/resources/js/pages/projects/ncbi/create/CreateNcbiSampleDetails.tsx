import {
  CalendarOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import {
  Avatar,
  Cascader,
  Checkbox,
  Col,
  Form,
  FormInstance,
  Input,
  List,
  Row,
  Select,
  Space,
  Tooltip,
} from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import {
  DefaultModifiableField,
  LoaderValues,
  SampleRecord,
} from "./CreateNcbiExport";

const rules = [
  {
    required: true,
    message: i18n("CreateNcbiExport.required"),
  },
];

/**
 * React component to display a form details for each sample being submitted
 * to the NCBI SRA.
 * @param form Parent form being submitted
 * @param sample Current sample
 * @param onChange handles updated validation status of the sample
 * @constructor
 */
export default function CreateNcbiSampleDetails({
  form,
  sample,
  onChange,
}: {
  form: FormInstance;
  sample: SampleRecord;
  onChange: () => void;
}): JSX.Element {
  const { strategies, sources, platforms, selections }: LoaderValues =
    useLoaderData();

  const onDefaultModifiableFieldChange = (field: DefaultModifiableField) => {
    const value = form.getFieldValue(["samples", sample.name, field]);

    // Update that the field was modified directly on the sample
    form.setFieldsValue({
      samples: {
        [sample.name]: { [field]: { ...value, fromDefault: false } },
      },
    });
    onChange();
  };

  const singles = sample.files.singles.map((file) => ({
    label: (
      <Space>
        <Avatar
          size="small"
          style={{ backgroundColor: `var(--primary-grey)` }}
          icon={<SwapRightOutlined />}
        />
        <span className="t-single-name">{file.name}</span>-
        <Tooltip title={i18n("CreateNcbiExportSample.file")}>
          {file.file.fileSize}
        </Tooltip>
        -
        <Tooltip title={i18n("CreateNcbiExportSample.date")}>
          <Space>
            <CalendarOutlined style={{ color: `var(--primary-grey)` }} />
            {formatInternationalizedDateTime(file.createdDate)}
          </Space>
        </Tooltip>
      </Space>
    ),
    value: file.id,
  }));
  const pairs = sample.files.pairs.map((file) => ({
    label: (
      <Space>
        <Avatar
          size="small"
          style={{ backgroundColor: `var(--primary-grey)` }}
          icon={<SwapOutlined />}
        />
        <List size="small">
          <List.Item>
            <Space>
              <span>{file.files[0].name}</span>-
              <Tooltip title={i18n("CreateNcbiExportSample.file")}>
                {file.files[0].fileSize}
              </Tooltip>
              -
              <Tooltip title={i18n("CreateNcbiExportSample.date")}>
                <Space>
                  <CalendarOutlined style={{ color: `var(--primary-grey)` }} />
                  {formatInternationalizedDateTime(file.files[0].createdDate)}
                </Space>
              </Tooltip>
            </Space>
          </List.Item>
          <List.Item>
            <Space>
              <span>{file.files[1].name}</span>-
              <Tooltip title={i18n("CreateNcbiExportSample.file")}>
                {file.files[1].fileSize}
              </Tooltip>
              -
              <Tooltip title={i18n("CreateNcbiExportSample.date")}>
                <Space>
                  <CalendarOutlined style={{ color: `var(--primary-grey)` }} />
                  {formatInternationalizedDateTime(file.files[1].createdDate)}
                </Space>
              </Tooltip>
            </Space>
          </List.Item>
        </List>
      </Space>
    ),
    value: file.id,
  }));

  const onChangeFiles = async (type: string) => {
    form.validateFields([["samples", sample.name, type]]);
    onChange();
  };

  return (
    <Row gutter={[16, 16]}>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "bioSample"]}
          label={i18n("NcbiBioSample.id")}
        >
          <Input
            type="text"
            onChange={onChange}
            className="t-sample-biosample"
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "libraryName"]}
          label={i18n("NcbiBioSample.libraryName")}
        >
          <Input type="text" onChange={onChange} className="t-sample-library" />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "libraryStrategy", "value"]}
          label={i18n("NcbiBioSample.libraryStrategy")}
        >
          <Select
            style={{ display: "block" }}
            onChange={() => onDefaultModifiableFieldChange("libraryStrategy")}
            className="t-sample-strategy"
          >
            {strategies?.map((option: string) => (
              <Select.Option key={option}>{option}</Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "librarySource", "value"]}
          label={i18n("NcbiBioSample.librarySource")}
        >
          <Select
            style={{ display: "block" }}
            onChange={() => onDefaultModifiableFieldChange("librarySource")}
            className="t-sample-source"
          >
            {sources.map((option: string) => (
              <Select.Option key={option}>{option}</Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={[
            "samples",
            sample.name,
            "libraryConstructionProtocol",
            "value",
          ]}
          label={i18n("NcbiBioSample.libraryConstructionProtocol")}
        >
          <Input
            type="text"
            onChange={() =>
              onDefaultModifiableFieldChange("libraryConstructionProtocol")
            }
            className="t-sample-protocol"
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "instrumentModel", "value"]}
          label={i18n("NcbiBioSample.instrumentModel")}
        >
          <Cascader
            options={platforms}
            style={{ display: "block" }}
            onChange={() => onDefaultModifiableFieldChange("instrumentModel")}
            className="t-sample-model"
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "librarySelection", "value"]}
          label={i18n("NcbiBioSample.librarySelection")}
        >
          <Select
            style={{ display: "block" }}
            onChange={() => onDefaultModifiableFieldChange("librarySelection")}
            className="t-sample-selection"
          >
            {selections.map((option) => (
              <Select.Option key={option}>{option}</Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Col>
      {sample.files.singles.length > 0 && (
        <Col span={24}>
          <Form.Item
            name={["samples", sample.name, "singles"]}
            label={i18n("CreateNcbiExport.singles")}
            valuePropName="checked"
            className="t-samples-singles"
            rules={[
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (
                    getFieldValue(["samples", sample.name, "pairs"]).length >
                      0 ||
                    value.length > 0
                  ) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error("Must select at least one file of any type.")
                  );
                },
              }),
            ]}
          >
            <Checkbox.Group
              style={{ width: `100%` }}
              options={singles}
              onChange={() => onChangeFiles("pairs")}
            />
          </Form.Item>
        </Col>
      )}
      {sample.files.pairs.length > 0 && (
        <Col span={24}>
          <Form.Item
            name={["samples", sample.name, "pairs"]}
            label={i18n("CreateNcbiExport.pairs")}
            valuePropName="checked"
            rules={[
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (
                    getFieldValue(["samples", sample.name, "singles"]).length >
                      0 ||
                    value.length > 0
                  ) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error("Must select at least one file of any type.")
                  );
                },
              }),
            ]}
          >
            <Checkbox.Group
              style={{ width: `100%` }}
              options={pairs}
              onChange={() => onChangeFiles("singles")}
            />
          </Form.Item>
        </Col>
      )}
    </Row>
  );
}
