import { SwapOutlined, SwapRightOutlined } from "@ant-design/icons";
import {
  Avatar,
  Cascader,
  Checkbox,
  Col,
  Form,
  Input,
  List,
  Row,
  Select,
  Space,
} from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { LoaderValues, SampleRecord } from "./CreateNcbiExport";

const rules = [
  {
    required: true,
    message: i18n("CreateNcbiExport.required"),
  },
];

export default function CreateNcbiSampleDetails({
  sample,
  onChange,
}: {
  sample: SampleRecord;
  onChange: () => void;
}): JSX.Element {
  const { strategies, sources, platforms, selections }: LoaderValues =
    useLoaderData();

  const singles = sample.files.singles.map((file) => ({
    label: (
      <Space>
        <Avatar
          size="small"
          style={{ backgroundColor: `var(--primary-grey)` }}
          icon={<SwapRightOutlined />}
        />
        <span className="t-single-name">{file.name}</span> -{" "}
        {file.file.fileSize}
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
            {file.files[0].name} - {file.files[0].fileSize}
          </List.Item>
          <List.Item>
            {file.files[1].name} - {file.files[1].fileSize}
          </List.Item>
        </List>
      </Space>
    ),
    value: file.id,
  }));

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
          name={["samples", sample.name, "libraryStrategy"]}
          label={i18n("NcbiBioSample.libraryStrategy")}
        >
          <Select
            style={{ display: "block" }}
            onChange={onChange}
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
          name={["samples", sample.name, "librarySource"]}
          label={i18n("NcbiBioSample.librarySource")}
        >
          <Select
            style={{ display: "block" }}
            onChange={onChange}
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
          name={["samples", sample.name, "libraryConstructionProtocol"]}
          label={i18n("NcbiBioSample.libraryConstructionProtocol")}
        >
          <Input
            type="text"
            onChange={onChange}
            className="t-sample-protocol"
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "instrumentModel"]}
          label={i18n("NcbiBioSample.instrumentModel")}
        >
          <Cascader
            options={platforms}
            style={{ display: "block" }}
            onChange={onChange}
            className="t-sample-model"
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "librarySelection"]}
          label={i18n("NcbiBioSample.librarySelection")}
        >
          <Select
            style={{ display: "block" }}
            onChange={onChange}
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
          >
            <Checkbox.Group
              style={{ width: `100%` }}
              options={singles}
              onChange={onChange}
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
          >
            <Checkbox.Group
              style={{ width: `100%` }}
              options={pairs}
              onChange={onChange}
            />
          </Form.Item>
        </Col>
      )}
    </Row>
  );
}
