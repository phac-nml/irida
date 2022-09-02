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
import { SequencingFiles } from "../../../../apis/projects/samples";
import type {
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
} from "../../../../types/irida";
import CreateNcbiDefaultOptions from "./CreateNcbiDefaultOptions";
import CreateNcbiExportSamples from "./CreateNcbiExportSamples";
import {
  getNCBIPlatformsAsCascaderOptions,
  hydrateStoredSamples,
  validateSample,
} from "./ncbi-utilities";

export interface SampleRecord {
  key: string;
  id: number;
  name: string;
  files: SequencingFiles;
}

type DefaultFieldValue = {
  value: string | string[];
  fromDefault: boolean;
};

export type FormSample = Omit<SampleRecord, "files" | "key"> & {
  bioSample: string;
  libraryName: string;
  libraryStrategy: DefaultFieldValue;
  librarySource: DefaultFieldValue;
  libraryConstructionProtocol: DefaultFieldValue;
  instrumentModel: DefaultFieldValue;
  librarySelection: DefaultFieldValue;
  status?: string;
  singles: number[];
  pairs: number[];
};

export type DefaultModifiableField =
  | "libraryStrategy"
  | "librarySource"
  | "libraryConstructionProtocol"
  | "instrumentModel"
  | "librarySelection";

export type FormSamples = Record<string, FormSample>;

export interface LoaderValues {
  samples: SampleRecord[];
  platforms: LabeledValue[];
  strategies: NcbiStrategy[];
  sources: NcbiSource[];
  selections: NcbiSelection[];
}

export interface UpdateDefaultValues {
  (field: DefaultModifiableField, value: string | string[]): void;
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
  const { projectId } = useParams();
  const { samples: originalSamples }: LoaderValues = useLoaderData();

  const [form] = Form.useForm();
  const navigate = useNavigate();

  const [samples, setSamples] = React.useState<Record<string, SampleRecord>>(
    {}
  );
  const [formSamples, setFormSamples] = React.useState<FormSamples>({});
  const [invalid, setInvalid] = React.useState<SampleRecord[]>([]);
  const [createStatus, setCreateStatus] = React.useState<CreateStatus>(
    CreateStatus.IDLE
  );

  React.useEffect(() => {
    const formValues: FormSamples = {};
    const invalidSamples: SampleRecord[] = [];
    const goodSamples: Record<string, SampleRecord> = {};
    originalSamples.forEach((sample) => {
      if (validateSample(sample)) {
        formValues[sample.name] = {
          bioSample: "",
          libraryName: sample.name,
          name: sample.name,
          id: sample.id,
          instrumentModel: { value: "", fromDefault: true },
          libraryConstructionProtocol: { value: "", fromDefault: true },
          librarySelection: { value: "", fromDefault: true },
          librarySource: { value: "", fromDefault: true },
          libraryStrategy: { value: "", fromDefault: true },
          singles: [],
          pairs: [],
        };
        // Only keep a reference to the good files.
        goodSamples[sample.name] = sample;
      } else {
        invalidSamples.push(sample);
      }
    });
    setFormSamples(formValues);
    setSamples(goodSamples);
    setInvalid(invalidSamples);
  }, [originalSamples]);

  /**
   * Update the default value for each sample in the form
   * @param field the field to update
   * @param value new value
   */
  const updateDefaultValue: UpdateDefaultValues = (field, value): void => {
    // Update all the samples that don't currently have a value set by the sample.
    const values: FormSamples = form.getFieldValue("samples");

    Object.values(values).forEach((sample) => {
      if (sample[field].fromDefault || sample[field].value.length === 0) {
        form.setFieldsValue({
          samples: { [sample.name]: { [field]: { value, fromDefault: true } } },
        });
      }
    });
  };

  const removeInvalidSamples = () => setInvalid([]);

  /*
    This prevents the release date to be in the past.
     */
  const disabledDate: RangePickerProps["disabledDate"] = (date): boolean => {
    // Can not select days before today for release
    return date && date < moment().startOf("day");
  };

  /**
   * When submitting the form, validate that all fields are valid, format the data
   * and submit to the server.
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
        samples: _samples,
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
          samples: Object.values(_samples).map(
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

  /**
   * Remove a sample from the export.
   * Note: this only removes from the export, but if the page is reloaded, they still
   * exist in the sessionStorage.
   * @param event
   * @param sample - sample to remove
   */
  const removeSample = (
    event: React.MouseEvent<HTMLElement>,
    sample: SampleRecord
  ): void => {
    event.stopPropagation();
    const updated = { ...samples };
    delete updated[sample.name];
    setSamples(updated);
  };

  const buttons =
    createStatus !== CreateStatus.RESOLVED ? (
      <Button
        type="primary"
        htmlType="submit"
        loading={createStatus === CreateStatus.PENDING}
        disabled={createStatus === CreateStatus.PENDING}
        className="t-submit-button"
      >
        {i18n("CreateNcbiExport.submit")}
      </Button>
    ) : (
      <Alert type="success" message={i18n("CreateNcbiExport.success")} />
    );

  return (
    <Layout.Content>
      <Row justify="center">
        <Col xxl={16} xl={20} sm={24}>
          <PageHeader title={i18n("CreateNcbiExport.title")}>
            {Object.keys(samples).length > 0 ? (
              <>
                <Form
                  layout="vertical"
                  initialValues={{
                    releaseDate: moment(new Date()),
                    samples: formSamples,
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
                      {invalid.length > 0 && (
                        <Alert
                          type="warning"
                          showIcon
                          message={"Some Samples are not valid"}
                          closable
                          onClose={removeInvalidSamples}
                        />
                      )}
                      <CreateNcbiDefaultOptions onChange={updateDefaultValue} />
                      <CreateNcbiExportSamples
                        form={form}
                        samples={samples}
                        removeSample={removeSample}
                      />
                    </Card>

                    {buttons}
                  </Space>
                </Form>
              </>
            ) : (
              <Alert
                type="error"
                showIcon
                message={"NO VALID SAMPLES"}
                description={
                  "None of the samples provided contain any sequencing file data."
                }
              />
            )}
          </PageHeader>
        </Col>
      </Row>
    </Layout.Content>
  );
}

export default CreateNcbiExport;
