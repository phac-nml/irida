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
import { useLoaderData, useParams } from "react-router-dom";
import {
  getNCBIPlatforms,
  getNCBISelections,
  getNCBISources,
  getNCBIStrategies,
  NcbiSubmissionBioSample,
  NcbiSubmissionRequest,
  submitNcbiSubmissionRequest,
} from "../../../../apis/export/ncbi";
import { fetchSampleFiles } from "../../../../apis/samples/samples";
import type { Option } from "../../../../types/ant-design";
import type {
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../../types/irida";
import { getStoredSamples } from "../../../../utilities/session-utilities";
import CreateNcbiDefaultOptions from "./CreateNcbiDefaultOptions";
import CreateNcbiExportSamples from "./CreateNcbiExportSamples";
import { formatPlatformsAsCascaderOptions } from "./ncbi-utilities";

export interface SampleRecord {
  key: string;
  id: number;
  name: string;
  bioSample: string;
  libraryName: string;
  libraryStrategy: string;
  librarySource: string;
  libraryConstructionProtocol: string;
  instrumentModel: string;
  librarySelection: string;
  status?: string;
  files: {
    pairs: PairedEndSequenceFile[];
    singles: SingleEndSequenceFile[];
  };
}

/**
 * TypeGuard for SampleRecord interface.
 * @param attribute
 */
function isModifiableFieldOnSampleRecordProperty(
  attribute: string
): attribute is keyof SampleRecord {
  return [
    "bioSample",
    "libraryName",
    "libraryStrategy",
    "librarySource",
    "libraryConstructionProtocol",
    "instrumentModel",
    "librarySelection",
  ].includes(attribute);
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
          bioSample: "",
          instrumentModel: "",
          libraryConstructionProtocol: "",
          librarySelection: "",
          librarySource: "",
          libraryStrategy: "",
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
  const { projectId } = useParams();
  const [form] = Form.useForm();

  /**
   * Update the default value for each sample in the form
   * @param field the field to update
   * @param value new value
   */
  const updateDefaultValue: UpdateDefaultValues = (field, value): void => {
    if (isModifiableFieldOnSampleRecordProperty(field)) {
      // Update all the samples that do not currently have a value.
      const values: SampleRecords = form.getFieldValue("samples");
      Object.values(values).forEach((sample) => {
        if (sample[field] === undefined) {
          form.setFieldsValue({
            samples: { [sample.name]: { [field]: value } },
          });
        }
      });
    }
  };

  /*
  This prevents the release date to be in the past.
   */
  const disabledDate: RangePickerProps["disabledDate"] = (date): boolean => {
    // Can not select days before today for release
    return date && date < moment().startOf("day");
  };

  const validateAndSubmit = (): void => {
    form.validateFields().then(
      ({
        bioProject,
        namespace,
        organization,
        releaseDate,
        samples,
      }: {
        bioProject: string;
        namespace: string;
        organization: string;
        releaseDate: moment.Moment;
        samples: {
          [k: string]: {
            files: {
              pairs?: number[];
              singles?: number[];
            };
            bioSample: string;
            libraryName: string;
            libraryStrategy: string;
            librarySource: string;
            libraryConstructionProtocol: string;
            instrumentModel: [string, string];
            librarySelection: string;
          };
        };
      }) => {
        const request: NcbiSubmissionRequest = {
          projectId: Number(projectId),
          bioProject,
          namespace,
          organization,
          releaseDate: releaseDate.unix(),
          samples: Object.values(samples).map(
            ({
              files = { pairs: [], singles: [] },
              instrumentModel,
              ...rest
            }): NcbiSubmissionBioSample => {
              return {
                ...rest,
                instrumentModel: instrumentModel[1],
                singles: files.singles ? files.singles : [],
                pairs: files.pairs ? files.pairs : [],
              };
            }
          ),
        };

        submitNcbiSubmissionRequest(request)
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            console.log(error);
          });
      }
    );
  };

  return (
    <Layout.Content>
      <Row justify="center">
        <Col xxl={16} xl={20} sm={24}>
          <PageHeader title={i18n("CreateNcbiExport.title")}>
            <Form
              layout="vertical"
              initialValues={{
                releaseDate: moment(new Date()),
                samples,
              }}
              form={form}
              onFinish={validateAndSubmit}
            >
              <Space direction="vertical">
                <Card title={i18n("CreateNcbiExport.details")}>
                  <Row gutter={[16, 16]}>
                    <Col md={12} xs={24}>
                      <Form.Item
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "NcbiSubmissionRequest.projectId.description"
                            ),
                          },
                        ]}
                        name="bioProject"
                        label={i18n("NcbiSubmissionRequest.projectId")}
                        help={i18n(
                          "NcbiSubmissionRequest.projectId.description"
                        )}
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
                              "NcbiSubmissionRequest.organization.description"
                            ),
                          },
                        ]}
                        label={i18n("NcbiSubmissionRequest.organization")}
                        help={i18n(
                          "NcbiSubmissionRequest.organization.description"
                        )}
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
                              "NcbiSubmissionRequest.namespace.description"
                            ),
                          },
                        ]}
                        label={i18n("NcbiSubmissionRequest.namespace")}
                        help={i18n(
                          "NcbiSubmissionRequest.namespace.description"
                        )}
                      >
                        <Input />
                      </Form.Item>
                    </Col>
                    <Col md={12} xs={24}>
                      <Form.Item
                        name="releaseDate"
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "NcbiSubmissionRequest.releaseDate.description"
                            ),
                          },
                        ]}
                        label={i18n("NcbiSubmissionRequest.releaseDate")}
                        help={i18n(
                          "NcbiSubmissionRequest.releaseDate.description"
                        )}
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
