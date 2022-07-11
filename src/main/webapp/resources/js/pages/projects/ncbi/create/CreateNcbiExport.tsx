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
  const results = Promise.all([
    samples,
    platforms,
    strategies,
    sources,
    selections,
  ]);
  return results.then(
    ([samples, platforms, strategies, sources, selections]) => ({
      samples,
      platforms,
      strategies,
      sources,
      selections,
    })
  );
}

function CreateNcbiExport(): JSX.Element {
  const { samples }: LoaderValues = useLoaderData();
  const [form] = Form.useForm();

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
          <Row gutter={[16, 16]} justify="center">
            <Col xxl={16} xl={20} sm={24}>
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
            </Col>
            <Col xxl={16} xl={20} sm={24}>
              <CreateNcbiDefaultOptions />
            </Col>
            <Col xxl={16} xl={20} sm={24}>
              <CreateNcbiExportSamples form={form} />
            </Col>

            <Col
              xxl={{ span: 16, offset: 4 }}
              xl={{ span: 20, offset: 2 }}
              sm={24}
            >
              <Button type="primary" htmlType="submit">
                __SUBMIT
              </Button>
            </Col>
          </Row>
        </Form>
      </PageHeader>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
