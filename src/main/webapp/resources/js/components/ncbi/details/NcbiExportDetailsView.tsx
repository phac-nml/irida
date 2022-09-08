import { SwapOutlined, SwapRightOutlined } from "@ant-design/icons";
import { Avatar, Card, List, Space, Tag, Typography } from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { BasicList } from "../../lists";
import type {
  SequencingObject,
  SingleEndSequenceFile,
} from "../../../types/irida";
import { NcbiSubmission, PairedEndSequenceFile } from "../../../types/irida";
import {
  BioSampleDetails,
  formatNcbiSubmissionDetails,
  formatNcbiBioSampleFiles,
} from "./utils";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { getNcbiSubmission } from "../../../apis/export/ncbi";
import { DataFunctionArgs } from "@remix-run/router/utils";
import { SPACE_LG } from "../../../styles/spacing";
import { BasicListItem } from "../../lists/BasicList";

type LoaderType = [BasicListItem[], BioSampleDetails[]];

/**
 * React router data loader (https://beta.reactrouter.com/en/dev/route/loader)
 * Fetches the submission details and formats them to be used by the component.
 * @param params
 */
export async function loader({
  params,
}: DataFunctionArgs): Promise<LoaderType> {
  const { id, projectId } = params;
  if (projectId && id) {
    return getNcbiSubmission(parseInt(projectId), parseInt(id)).then(
      (submission: NcbiSubmission): LoaderType => {
        const { bioSamples, ...info } = submission;
        const details = formatNcbiSubmissionDetails(info);
        const bioSampleDetails = formatNcbiBioSampleFiles(bioSamples);
        return [details, bioSampleDetails];
      }
    );
  } else {
    return Promise.reject(i18n("NcbiExportDetailsView.loader-error"));
  }
}

/**
 * Render the details of a NCBI SRA Submission
 * @constructor
 */
function NcbiExportDetailsView(): JSX.Element {
  const [details, bioSamplesDetails]: LoaderType = useLoaderData();

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <Typography.Title level={4} style={{ color: `var(--grey-7)` }}>
        {i18n("NcbiExportDetailsView.title")}
      </Typography.Title>
      <Card>
        <BasicList dataSource={details} grid={{ gutter: 16, column: 2 }} />
      </Card>
      <Typography.Title
        level={5}
        style={{ color: `var(--grey-7)`, marginTop: SPACE_LG }}
      >
        {i18n("NcbiSubmission.bioSamples")}
      </Typography.Title>
      {bioSamplesDetails.map((bioSample: BioSampleDetails) => {
        return (
          <Card key={bioSample.key}>
            <Space
              key={bioSample.key}
              direction="vertical"
              style={{ width: `100%` }}
            >
              <BasicList
                grid={{ gutter: 16, column: 2 }}
                dataSource={bioSample.details}
              />

              {bioSample.files.pairs.length > 0 && (
                <List
                  size="small"
                  dataSource={bioSample.files.pairs}
                  renderItem={(pair: PairedEndSequenceFile) => (
                    <List.Item className={"t-pair"}>
                      <div
                        style={{
                          width: `100%`,
                          display: "flex",
                          alignItems: "center",
                        }}
                      >
                        <Avatar
                          size="small"
                          style={{ backgroundColor: `var(--blue-6)` }}
                          icon={<SwapOutlined />}
                        />
                        <List
                          style={{ flexGrow: 1 }}
                          key={pair.key}
                          dataSource={pair.files}
                          renderItem={(file: SequencingObject) => (
                            <List.Item
                              actions={[
                                <Tag key={file.key}>{file.fileSize}</Tag>,
                              ]}
                            >
                              <List.Item.Meta
                                title={file.name}
                                description={formatInternationalizedDateTime(
                                  file.createdDate
                                )}
                              />
                            </List.Item>
                          )}
                        />
                      </div>
                    </List.Item>
                  )}
                />
              )}

              {bioSample.files.singles.length > 0 && (
                <List
                  size="small"
                  dataSource={bioSample.files.singles}
                  renderItem={(file: SingleEndSequenceFile) => (
                    <List.Item className="t-single">
                      <div
                        style={{
                          width: `100%`,
                          display: "flex",
                          alignItems: "center",
                        }}
                      >
                        <Avatar
                          size="small"
                          style={{ backgroundColor: `var(--blue-6)` }}
                          icon={<SwapRightOutlined />}
                        />
                        <List style={{ flexGrow: 1 }}>
                          <List.Item
                            actions={[
                              <Tag key={file.key}>{file.file.fileSize}</Tag>,
                            ]}
                          >
                            <List.Item.Meta
                              title={file.file.name}
                              description={formatInternationalizedDateTime(
                                file.file.createdDate
                              )}
                            />
                          </List.Item>
                        </List>
                      </div>
                    </List.Item>
                  )}
                />
              )}
            </Space>
          </Card>
        );
      })}
    </Space>
  );
}

export default NcbiExportDetailsView;
