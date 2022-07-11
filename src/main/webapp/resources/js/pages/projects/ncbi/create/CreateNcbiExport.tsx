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
import {
  fetchSampleFiles,
  SamplesFiles,
} from "../../../../apis/samples/samples";
import type { Option } from "../../../../types/ant-design";
import type {
  NcbiBiosample,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
} from "../../../../types/irida";
import { getStoredSamples } from "../../../../utilities/session-utilities";
import CreateNcbiDefaultOptions from "./CreateNcbiDefaultOptions";
import CreateNcbiExportSamples from "./CreateNcbiExportSamples";
import { formatPlatformsAsCascaderOptions } from "./ncbi-utilities";

export interface SampleRecord extends NcbiBiosample {
  files: SamplesFiles;
}

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
  (field: string, value: string): void;
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
          library_name: sample.name,
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

function CreateNcbiExport(): JSX.Element {
  const { samples }: LoaderValues = useLoaderData();
  const [form] = Form.useForm();

  /**
   * Update the default value for each sample in the form
   * @param field the field to update
   * @param value new value
   */
  const updateDefaultValue = (field: string, value: string): void => {
    console.log(value);
    // Update all the samples in the Ant Design form with the new value.
    const values: SampleRecords = form.getFieldValue("samples");
    Object.values(values).forEach((sample) => {
      form.setFieldsValue({ samples: { [sample.name]: { [field]: value } } });
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
              <Space direction="vertical">
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
                <Card title={"Samples"}>
                  <CreateNcbiDefaultOptions onChange={updateDefaultValue} />
                  <CreateNcbiExportSamples form={form} />
                </Card>

                <Button type="primary" htmlType="submit">
                  __SUBMIT
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
