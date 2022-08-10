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
  Collapse,
  Form,
  FormInstance,
  Input,
  Row,
  Select,
  Space,
} from "antd";
import { NamePath } from "antd/lib/form/interface";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { LoaderValues, SampleRecord, SampleRecords } from "./CreateNcbiExport";

const rules = [
  {
    required: true,
    message: i18n("CreateNcbiExport.required"),
  },
];

function SampleDetails({
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
                {sample.files.singles.map((pair) => (
                  <Col key={pair.key} span={24}>
                    <Checkbox value={pair.id}>
                      <Space>
                        <Avatar
                          size="small"
                          style={{ backgroundColor: `var(--primary-grey)` }}
                          icon={<SwapRightOutlined />}
                        />
                        {pair.name}
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
                        {pair.name}
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

function CreateNcbiExportSamples({
  form,
}: {
  form: FormInstance;
}): JSX.Element {
  const { samples }: { samples: SampleRecords } = useLoaderData();
  const values = Object.values(samples);
  const [validationStatus, setValidationStatus] = React.useState<boolean[]>(
    () =>
      values.map(
        (sample) =>
          sample.files.singles.length > 0 || sample.files.pairs.length > 0
      )
  );

  const checkStatus = (sample: SampleRecord, index: number): void => {
    const fields: Array<NamePath> = [
      ["samples", sample.name, "bioSample"],
      ["samples", sample.name, "libraryName"],
      ["samples", sample.name, "libraryStrategy"],
      ["samples", sample.name, "librarySource"],
      ["samples", sample.name, "libraryConstructionProtocol"],
      ["samples", sample.name, "instrumentModel"],
      ["samples", sample.name, "librarySelection"],
      ["samples", sample.name, "files", "singles"],
      ["samples", sample.name, "files", "pairs"],
    ];

    const touched: Array<NamePath> = [];
    let hasUntouched = false;

    fields.map((field) => {
      if (form.isFieldTouched(field)) {
        touched.push(field);
      } else {
        if (form.getFieldValue(field) === undefined) {
          hasUntouched = true;
        }
      }
    });

    if (hasUntouched) {
      // If it has untouched fields, then it is not valid yet!
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
    <Collapse accordion>
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
