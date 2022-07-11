import { Cascader, Col, Collapse, Form, Input, Row, Select } from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";

function CreateNcbiDefaultOptions({
  onChange,
}: {
  onChange: () => void;
}): JSX.Element {
  const { strategies, sources, platforms, selections }: LoaderValues =
    useLoaderData();
  return (
    <Collapse ghost>
      <Collapse.Panel header="Default Sample Settings" key="1">
        <p>These will be applied to all samples:</p>
        <Row gutter={[16, 16]}>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.library_strategy.title")}>
              <Select
                style={{ display: "block" }}
                onChange={(value) => onChange("library_strategy", value)}
              >
                {strategies?.map((option: string) => (
                  <Select.Option key={option}>{option}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.library_source.title")}>
              <Select
                style={{ display: "block" }}
                onSelect={(value) => onChange("library_source", value)}
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
                onChange={(value) =>
                  onChange("library_construction_protocol", value)
                }
              />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.instrument_model.title")}>
              <Cascader options={platforms} style={{ display: "block" }} />
            </Form.Item>
          </Col>
          <Col md={12} xs={24}>
            <Form.Item label={i18n("project.export.library_selection.title")}>
              <Select style={{ display: "block" }}>
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
