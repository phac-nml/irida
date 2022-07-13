import {
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Layout,
  PageHeader,
  Row,
  Space,
} from "antd";
import type { RangePickerProps } from "antd/es/date-picker";
import moment from "moment";
import React from "react";
import { useLoaderData } from "react-router-dom";
import {
  getNCBIPlatforms,
  getNCBISelections,
  getNCBISources,
  getNCBIStrategies,
} from "../../../../apis/export/ncbi";
import { fetchSampleFiles } from "../../../../apis/samples/samples";
import type { Option } from "../../../../types/ant-design";
import type {
  NcbiBioSample,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
  StoredSample,
} from "../../../../types/irida";
import { getStoredSamples } from "../../../../utilities/session-utilities";
import CreateNcbiDefaultOptions from "./CreateNcbiDefaultOptions";
import CreateNcbiExportSamples from "./CreateNcbiExportSamples";
import { formatPlatformsAsCascaderOptions } from "./ncbi-utilities";

export type SampleRecord =
  | {
      key: string;
      files: {
        paired: PairedEndSequenceFile[];
        singles: SingleEndSequenceFile[];
      };
    }
  | StoredSample
  | Omit<NcbiBioSample, "paired" | "singles">;

export interface SampleRecords {
  [k: string]: SampleRecord;
}

export interface LoaderValues {
  samples: SampleRecords;
  platforms: Option[];
  strategies: NcbiStrategy[];
  sources: NcbiSource[];
  selections: NcbiSelection[];
}

export interface UpdateDefaultValues {
  (field: string, value: string | string[]): void;
}

/**
 * React router loader
 */
export async function loader(): Promise<LoaderValues> {
  const samples = await getStoredSamples().then(({ samples }) => {
    const formatted: SampleRecords = {};
    samples.forEach((sample) => {
      fetchSampleFiles({ sampleId: sample.id }).then((files) => {
        formatted[sample.name] = {
          key: sample.name,
          name: sample.name,
          id: sample.id,
          libraryName: sample.name,
          files,
        };
      });
    });
    return formatted;
  });
  const platforms = await getNCBIPlatforms().then((platforms) =>
    formatPlatformsAsCascaderOptions(platforms)
  );
  const strategies = await getNCBIStrategies();
  const sources = await getNCBISources();
  const selections = await getNCBISelections();
  return Promise.all([
    samples,
    platforms,
    strategies,
    sources,
    selections,
  ]).then(([samples, platforms, strategies, sources, selections]) => ({
    samples,
    platforms,
    strategies,
    sources,
    selections,
  }));
}

/**
 * React component to render a form for created a new NCBI SRA Export
 * @constructor
 */
function CreateNcbiExport(): JSX.Element {
  const { samples }: LoaderValues = useLoaderData();
  const [form] = Form.useForm();

  /**
   * Update the default value for each sample in the form
   * @param field the field to update
   * @param value new value
   */
  const updateDefaultValue: UpdateDefaultValues = (field, value) => {
    // Update all the samples in the Ant Design form with the new value.
    const values: SampleRecords = form.getFieldValue("samples");
    Object.values(values).forEach((sample) => {
      if ("name" in sample) {
        form.setFieldsValue({ samples: { [sample.name]: { [field]: value } } });
      }
    });
  };

  /*
  This prevents the release date to be in the past.
   */
  const disabledDate: RangePickerProps["disabledDate"] = (date): boolean => {
    // Can not select days before today for release
    return date && date < moment().startOf("day");
  };

  const validateAndSubmit = (): void => {
    // TODO: convert release date from momentjs
    form.validateFields().then(console.log);
  };

  return (
    <Layout.Content>
      <Row justify="center">
        <Col xxl={16} xl={20} sm={24}>
          <PageHeader title={i18n("CreateNcbiExport.title")}>
            <Form
              layout="vertical"
              initialValues={{
                release_date: moment(new Date()),
                samples,
              }}
              form={form}
              onFinish={validateAndSubmit}
            >
              <Space direction="vertical">
                <Card title={"Export Details"}>
                  <Row gutter={[16, 16]}>
                    <Col md={12} xs={24}>
                      <Form.Item
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "CreateNcbiExport.bioproject.description"
                            ),
                          },
                        ]}
                        name="bioproject"
                        label={i18n("CreateNcbiExport.bioproject.title")}
                        help={i18n("CreateNcbiExport.bioproject.description")}
                      >
                        <Input />
                      </Form.Item>
                    </Col>
                    <Col md={12} xs={24}>
                      <Form.Item
                        name="organization"
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "CreateNcbiExport.organization.description"
                            ),
                          },
                        ]}
                        label={i18n("CreateNcbiExport.organization.title")}
                        help={i18n("CreateNcbiExport.organization.description")}
                      >
                        <Input />
                      </Form.Item>
                    </Col>
                    <Col md={12} xs={24}>
                      <Form.Item
                        name="namespace"
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "CreateNcbiExport.namespace.description"
                            ),
                          },
                        ]}
                        label={i18n("CreateNcbiExport.namespace.title")}
                        help={i18n("CreateNcbiExport.namespace.description")}
                      >
                        <Input />
                      </Form.Item>
                    </Col>
                    <Col md={12} xs={24}>
                      <Form.Item
                        name="release_date"
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "CreateNcbiExport.release_date.description"
                            ),
                          },
                        ]}
                        label={i18n("CreateNcbiExport.release_date.title")}
                        help={i18n("CreateNcbiExport.release_date.description")}
                      >
                        <DatePicker
                          style={{ width: "100%" }}
                          disabledDate={disabledDate}
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                </Card>
                <Card title={i18n("CreateNcbiExport.samples")}>
                  <CreateNcbiDefaultOptions onChange={updateDefaultValue} />
                  <CreateNcbiExportSamples form={form} />
                </Card>

                <Button type="primary" htmlType="submit">
                  {i18n("CreateNcbiExport.submit")}
                </Button>
              </Space>
            </Form>
          </PageHeader>
        </Col>
      </Row>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
