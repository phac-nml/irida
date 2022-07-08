import {
  CheckOutlined,
  CloseOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import {
  Avatar,
  Cascader,
  Col,
  Collapse,
  Form,
  Input,
  Radio,
  Row,
  Select,
  Space,
} from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { SampleRecord, SampleRecords } from "./CreateNcbiExport";

function SampleDetails({
  sample,
  onChange,
}: {
  sample: SampleRecord;
  onChange: () => void;
}): JSX.Element {
  const { strategies, sources, platforms, selections } = useLoaderData();

  return (
    <Row gutter={[16, 16]}>
      <Col md={12} xs={24}>
        <Form.Item
          rules={[{ required: true, message: "BioSample ID is required" }]}
          name={["samples", sample.name, "biosample"]}
          label={i18n("CreateNcbiExport.biosample-id")}
        >
          <Input type="text" onChange={onChange} />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={[{ required: true, message: "Library Name is required" }]}
          name={["samples", sample.name, "library_name"]}
          label={i18n("project.export.library_name.title")}
        >
          <Input type="text" onChange={onChange} />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={[{ required: true, message: "Library Strategy is required" }]}
          name={["samples", sample.name, "library_strategy"]}
          label={i18n("project.export.library_strategy.title")}
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
          rules={[{ required: true, message: "Library Source is required" }]}
          name={["samples", sample.name, "library_source"]}
          label={i18n("project.export.library_source.title")}
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
          rules={[
            {
              required: true,
              message: "Library Construction Protocol is required",
            },
          ]}
          name={["samples", sample.name, "library_construction_protocol"]}
          label={i18n("project.export.library_construction_protocol.title")}
        >
          <Input type="text" onChange={onChange} />
        </Form.Item>
      </Col>
      <Col md={12} xs={24}>
        <Form.Item
          rules={[
            {
              required: true,
              message: "Interment Model is required",
            },
          ]}
          name={["samples", sample.name, "instrument_model"]}
          label={i18n("project.export.instrument_model.title")}
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
          rules={[
            {
              required: true,
              message: "Library Selection is required",
            },
          ]}
          name={["samples", sample.name, "library_selection"]}
          label={i18n("project.export.library_selection.title")}
        >
          <Select style={{ display: "block" }} onChange={onChange}>
            {selections.map((option) => (
              <Select.Option key={option}>{option}</Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Col>
      <Col span={24}>
        <Form.Item
          name={["samples", sample.name, "file"]}
          label={"_Sequencing Files_"}
          rules={[
            {
              required: true,
              message: "Must select at least one sequencing file",
            },
          ]}
        >
          <Radio.Group onChange={onChange}>
            <Space direction="vertical">
              {sample.files.singles.map((file) => (
                <Radio key={`file-${file.identifier}`} value={file.identifier}>
                  <Space>
                    <Avatar
                      size="small"
                      style={{ backgroundColor: `var(--blue-6)` }}
                      icon={<SwapRightOutlined />}
                    />
                    {file.label}
                  </Space>
                </Radio>
              ))}
              {sample.files.paired.map((file) => (
                <Radio key={`file-${file.identifier}`} value={file.identifier}>
                  <Space>
                    <Avatar
                      size="small"
                      style={{ backgroundColor: `var(--blue-6)` }}
                      icon={<SwapOutlined />}
                    />
                    {file.label}
                  </Space>
                </Radio>
              ))}
            </Space>
          </Radio.Group>
        </Form.Item>
      </Col>
    </Row>
  );
}

function SampleValidIcon({ status }: { status: boolean }) {
  return status ? (
    <Avatar
      icon={<CheckOutlined />}
      size="small"
      style={{
        backgroundColor: `var(--primary-green)`,
        color: `var(--grey-1)`,
      }}
    />
  ) : (
    <Avatar
      icon={<CloseOutlined />}
      size="small"
      style={{
        backgroundColor: `var(--primary-red)`,
        color: `var(--grey-1)`,
      }}
    />
  );
}

function CreateNcbiExportSamples({ form }) {
  const { samples }: { samples: SampleRecords } = useLoaderData();
  const values = Object.values(samples);
  const [validationStatus, setValidationStatus] = React.useState<boolean[]>(
    () => values.map(() => false)
  );

  const checkStatus = (sample: SampleRecord, index: number): void => {
    const touched: string[][] = [];
    const untouched: string[][] = [];
    [
      ["samples", sample.name, "biosample"],
      ["samples", sample.name, "library_name"],
      ["samples", sample.name, "library_strategy"],
      ["samples", sample.name, "library_source"],
      ["samples", sample.name, "library_construction_protocol"],
      ["samples", sample.name, "instrument_model"],
      ["samples", sample.name, "library_selection"],
      ["samples", sample.name, "file"],
    ].map((field) => {
      if (form.isFieldTouched(field)) {
        touched.push(field);
      } else {
        if (form.getFieldValue(field) === undefined) {
          untouched.push(field);
        }
      }
    });

    if (untouched.length) {
      const updated = [...validationStatus];
      updated[index] = false;
      setValidationStatus(updated);
    } else {
      form
        .validateFields(touched)
        .then(() => {
          const updated = [...validationStatus];
          updated[index] = true;
          setValidationStatus(updated);
        })
        .catch(() => {
          const updated = [...validationStatus];
          updated[index] = false;
          setValidationStatus(updated);
        });
    }
  };

  return (
    <Collapse accordion defaultActiveKey={values[0].key}>
      {values.map((sample, index) => (
        <Collapse.Panel
          key={String(sample.key)}
          header={sample.name}
          extra={<SampleValidIcon status={validationStatus[index]} />}
        >
          <SampleDetails
            sample={sample}
            onChange={() => checkStatus(sample, index)}
          />
        </Collapse.Panel>
      ))}
    </Collapse>
  );
}

export default CreateNcbiExportSamples;
