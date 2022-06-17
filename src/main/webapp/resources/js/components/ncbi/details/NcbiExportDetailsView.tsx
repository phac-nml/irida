import { SwapOutlined, SwapRightOutlined } from "@ant-design/icons";
import { Avatar, Card, List, Space, Tag, Typography } from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { BasicList } from "../../lists";
import type { SequenceFile, SingleEndSequenceFile } from "../../../types/irida";
import { NcbiSubmission, PairedEndSequenceFile } from "../../../types/irida";
import {
  BioSampleFileDetails,
  formatNcbiSubmissionDetails,
  formatNcbiBioSampleFiles,
} from "./utils";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { getNcbiSubmission } from "../../../apis/export/ncbi";
import { DataFunctionArgs } from "@remix-run/router/utils";
import { SPACE_LG } from "../../../styles/spacing";
import { BasicListItem } from "../../lists/BasicList";

/**
 * React router data loader (https://beta.reactrouter.com/en/dev/route/loader)
 * Fetches the submission details and formats them to be used by the component.
 * @param params
 */
export async function loader({
  params,
}: DataFunctionArgs): Promise<[BasicListItem[], BioSampleFileDetails[]]> {
  const { id, projectId } = params;
  if (projectId && id) {
    return getNcbiSubmission(parseInt(projectId), parseInt(id)).then(
      (
        submission: NcbiSubmission
      ): [BasicListItem[], BioSampleFileDetails[]] => {
        const { bioSampleFiles, ...info } = submission;
        const details = formatNcbiSubmissionDetails(info);
        const bioSamples = formatNcbiBioSampleFiles(bioSampleFiles);
        return [details, bioSamples];
      }
    );
  } else {
    return Promise.reject("No project id or export id");
  }
}

/**
 * Render the details of a NCBI SRA Submission
 * @constructor
 */
function NcbiExportDetailsView(): JSX.Element {
  const [details, bioSampleFiles] = useLoaderData();

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
        {i18n("NcbiExportDetailsView.files")}
      </Typography.Title>
      {bioSampleFiles.map((bioSampleFile: BioSampleFileDetails) => {
        return (
          <Card key={bioSampleFile.key}>
            <Space
              key={bioSampleFile.key}
              direction="vertical"
              style={{ width: `100%` }}
            >
              <BasicList
                grid={{ gutter: 16, column: 2 }}
                dataSource={bioSampleFile.details}
              />

              {bioSampleFile.files.pairs.length > 0 && (
                <List
                  size="small"
                  dataSource={bioSampleFile.files.pairs}
                  renderItem={(pair: PairedEndSequenceFile) => (
                    <List.Item>
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
                          renderItem={(file: SequenceFile) => (
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

              {bioSampleFile.files.singles.length > 0 && (
                <List
                  size="small"
                  dataSource={bioSampleFile.files.singles}
                  renderItem={(file: SingleEndSequenceFile) => (
                    <List.Item>
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
