import {
  Cascader,
  Col,
  Collapse,
  Form,
  Input,
  Row,
  Select,
  Typography,
} from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { LoaderValues, UpdateDefaultValues } from "./CreateNcbiExport";

function CreateNcbiDefaultOptions({
  onChange,
}: {
  onChange: UpdateDefaultValues;
}): JSX.Element {
  const { strategies, sources, platforms, selections }: LoaderValues =
    useLoaderData();
  return (
    <Collapse ghost>
      <Collapse.Panel header={i18n("CreateNcbiExport.default.title")} key="1">
        <Typography.Text strong>
          {i18n("CreateNcbiExport.default.description")}
        </Typography.Text>
        <Row gutter={[16, 16]}>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("NcbiBioSample.libraryStrategy")}>
              <Select onChange={(value) => onChange("libraryStrategy", value)}>
                {strategies?.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("NcbiBioSample.librarySource")}>
              <Select
                onSelect={(value: string) => onChange("librarySource", value)}
              >
                {sources.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item
              label={i18n("NcbiBioSample.libraryConstructionProtocol")}
            >
              <Input
                type="text"
                onChange={(e) =>
                  onChange("libraryConstructionProtocol", e.target.value)
                }
              />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("NcbiBioSample.instrumentModel")}>
              <Cascader
                options={platforms}
                onChange={(value) =>
                  onChange("instrumentModel", value as string[])
                }
              />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("NcbiBioSample.librarySelection")}>
              <Select
                onSelect={(value: string) =>
                  onChange("librarySelection", value)
                }
              >
                {selections.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
        </Row>
      </Collapse.Panel>
    </Collapse>
  );
}

export default CreateNcbiDefaultOptions;
