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
            <Form.Item label={i18n("project.export.library_strategy.title")}>
              <Select onChange={(value) => onChange("library_strategy", value)}>
                {strategies?.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.library_source.title")}>
              <Select
                onSelect={(value: string) => onChange("library_source", value)}
              >
                {sources.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item
              label={i18n("project.export.library_construction_protocol.title")}
            >
              <Input
                type="text"
                onChange={(e) =>
                  onChange("library_construction_protocol", e.target.value)
                }
              />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.instrument_model.title")}>
              <Cascader
                options={platforms}
                onChange={(value) =>
                  onChange("instrument_model", value as string[])
                }
              />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.library_selection.title")}>
              <Select
                onSelect={(value: string) =>
                  onChange("library_selection", value)
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
