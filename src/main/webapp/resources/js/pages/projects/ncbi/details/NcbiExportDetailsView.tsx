import { SwapOutlined } from "@ant-design/icons";
import { Avatar, Card, List, Space, Tag, Typography } from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { BasicList } from "../../../../components/lists";
import type { SequenceFile } from "../../../../types/irida";
import { NcbiSubmission, PairedEndSequenceFile } from "../../../../types/irida";
import {
  BioSampleFileDetails,
  formatNcbiUploadDetails,
  formatNcbiUploadFiles,
} from "./utils";
import { BasicListItem } from "../../../../components/lists/BasicList.types";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { getNcbiSubmission } from "../../../../apis/export/ncbi";
import { DataFunctionArgs } from "@remix-run/router/utils";

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
        const details = formatNcbiUploadDetails(info);
        const bioSamples = formatNcbiUploadFiles(bioSampleFiles);
        return [details, bioSamples];
      }
    );
  } else {
    return Promise.reject("No project id or export id");
  }
}

function NcbiExportDetailsView(): JSX.Element {
  const [details, bioSampleFiles]  = useLoaderData();

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <Card title={i18n("project.export.sidebar.title")}>
        <BasicList dataSource={details} grid={{ gutter: 16, column: 2 }} />
      </Card>
      <Typography.Title level={5} style={{ color: `var(--blue-6)` }}>
        __BioSample Files
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
            </Space>
          </Card>
        );
      })}
    </Space>
  );
}

export default NcbiExportDetailsView;
