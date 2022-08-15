import {
  CheckOutlined,
  CloseOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
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

  return (
    <Row gutter={[16, 16]}>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "bioSample"]}
          label={i18n("NcbiBioSample.id")}
        >
          <Input type="text" onChange={onChange} />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "libraryName"]}
          label={i18n("NcbiBioSample.libraryName")}
        >
          <Input type="text" onChange={onChange} />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "libraryStrategy"]}
          label={i18n("NcbiBioSample.libraryStrategy")}
        >
          <Select style={{ display: "block" }} onChange={onChange}>
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
          <Select style={{ display: "block" }} onChange={onChange}>
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
          <Input type="text" onChange={onChange} />
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
          />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={rules}
          name={["samples", sample.name, "librarySelection"]}
          label={i18n("NcbiBioSample.librarySelection")}
        >
          <Select style={{ display: "block" }} onChange={onChange}>
            {selections.map((option) => (
              <Select.Option key={option}>{option}</Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Col>
      {sample.files.singles.length > 0 && (
        <Col span={24}>
          <Form.Item
            name={["samples", sample.name, "files", "singles"]}
            label={i18n("CreateNcbiExport.singles")}
            valuePropName="checked"
            rules={[
              {
                required: true,
                message: i18n("CreateNcbiExport.files.required"),
              },
            ]}
          >
            <Checkbox.Group style={{ width: `100%` }}>
              <Row>
                {sample.files.singles.map((single) => (
                  <Col key={single.key} span={24}>
                    <Checkbox value={single.id}>
                      <Space>
                        <Avatar
                          size="small"
                          style={{ backgroundColor: `var(--primary-grey)` }}
                          icon={<SwapRightOutlined />}
                        />
                        {single.name} - {single.file.fileSize}
                      </Space>
                    </Checkbox>
                  </Col>
                ))}
              </Row>
            </Checkbox.Group>
          </Form.Item>
        </Col>
      )}
      {sample.files.pairs.length > 0 && (
        <Col span={24}>
          <Form.Item
            name={["samples", sample.name, "files", "pairs"]}
            label={i18n("CreateNcbiExport.pairs")}
            valuePropName="checked"
            rules={[
              {
                required: true,
                message: i18n("CreateNcbiExport.files.required"),
              },
            ]}
          >
            <Checkbox.Group style={{ width: `100%` }}>
              <Row>
                {sample.files.pairs.map((pair) => (
                  <Col key={pair.key} span={24}>
                    <Checkbox value={pair.id}>
                      <Space>
                        <Avatar
                          size="small"
                          style={{ backgroundColor: `var(--primary-grey)` }}
                          icon={<SwapOutlined />}
                        />
                        <List size="small">
                          <List.Item>
                            {pair.files[0].name} - {pair.files[0].fileSize}
                          </List.Item>
                          <List.Item>
                            {pair.files[1].name} - {pair.files[1].fileSize}
                          </List.Item>
                        </List>
                      </Space>
                    </Checkbox>
                  </Col>
                ))}
              </Row>
            </Checkbox.Group>
          </Form.Item>
        </Col>
      )}
    </Row>
  );
}
