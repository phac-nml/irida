import {
  Alert,
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Layout,
  notification,
  PageHeader,
  Row,
  Space,
} from "antd";
import type { RangePickerProps } from "antd/es/date-picker";
import { LabeledValue } from "antd/lib/select";
import moment from "moment";
import React from "react";
import { useLoaderData, useNavigate, useParams } from "react-router-dom";
import {
  getNCBISelections,
  getNCBISources,
  getNCBIStrategies,
  NcbiSubmissionBioSample,
  NcbiSubmissionRequest,
  submitNcbiSubmissionRequest,
} from "../../../../apis/export/ncbi";
import type {
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../../types/irida";
import CreateNcbiDefaultOptions from "./CreateNcbiDefaultOptions";
import CreateNcbiExportSamples from "./CreateNcbiExportSamples";
import {
  getNCBIPlatformsAsCascaderOptions,
  hydrateStoredSamples,
} from "./ncbi-utilities";

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

export type SampleRecords = Record<string, SampleRecord>;

export interface LoaderValues {
  samples: SampleRecords;
  platforms: LabeledValue[];
  strategies: NcbiStrategy[];
  sources: NcbiSource[];
  selections: NcbiSelection[];
}

export interface UpdateDefaultValues {
  (field: string, value: string | string[]): void;
}

enum CreateStatus {
  REJECTED,
  RESOLVED,
  PENDING,
  IDLE,
}

/**
 * React router loader
 */
export async function loader(): Promise<LoaderValues> {
  const samplesPromise = await hydrateStoredSamples();
  const platformsPromise = await getNCBIPlatformsAsCascaderOptions();
  const strategiesPromise = await getNCBIStrategies();
  const sourcesPromise = await getNCBISources();
  const selectionsPromise = await getNCBISelections();

  return Promise.all([
    samplesPromise,
    platformsPromise,
    strategiesPromise,
    sourcesPromise,
    selectionsPromise,
  ]).then(([samplesResponse, platforms, strategies, sources, selections]) => ({
    samples: samplesResponse,
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
  const navigate = useNavigate();
  const [createStatus, setCreateStatus] = React.useState<CreateStatus>(
    CreateStatus.IDLE
  );

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
        if (sample[field] !== undefined && String(sample[field]).length === 0) {
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

  /**
   * When submitting the form, validate that all fields are valid, format the data
   * and submitted to the server.
   * onSuccess: show a message and return the user to the calling page after 1 second.
   * onError: show an error message to the user.
   */
  const validateAndSubmit = (): void => {
    form.validateFields().then(
      ({
        bioProject,
        namespace,
        organization,
        releaseDate,
        samples: formSamples,
      }: {
        bioProject: string;
        namespace: string;
        organization: string;
        releaseDate: moment.Moment;
        samples: {
          string: {
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
        setCreateStatus(CreateStatus.PENDING);
        const request: NcbiSubmissionRequest = {
          projectId: Number(projectId),
          bioProject,
          namespace,
          organization,
          releaseDate: releaseDate.unix(),
          samples: Object.values(formSamples).map(
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
          .then(() => {
            setCreateStatus(CreateStatus.RESOLVED);
            // Redirect the user back to the referrer page
            setTimeout(() => navigate(-1), 2000);
          })
          .catch((message = i18n("CreateNcbiExport.error")) => {
            setCreateStatus(CreateStatus.REJECTED);
            notification.error({
              message,
            });
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
                  <CreateNcbiExportSamples
                    form={form}
                    samples={Object.values(samples)}
                  />
                </Card>

                {createStatus !== CreateStatus.RESOLVED ? (
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={createStatus === CreateStatus.PENDING}
                  >
                    {i18n("CreateNcbiExport.submit")}
                  </Button>
                ) : (
                  <Alert
                    type="success"
                    message={i18n("CreateNcbiExport.success")}
                  />
                )}
              </Space>
            </Form>
          </PageHeader>
        </Col>
      </Row>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
