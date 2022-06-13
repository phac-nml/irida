import { SwapOutlined } from "@ant-design/icons";
import { Avatar, Card, Divider, List, Space, Tag, Typography } from "antd";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { BasicList } from "../../../../components/lists";
import { blue6 } from "../../../../styles/colors";
import type { SequenceFile } from "../../../../types/irida";
import { PairedEndSequenceFile } from "../../../../types/irida";
import { BioSampleFileDetails } from "./utils";
import { BasicListItem } from "../../../../components/lists/BasicList.types";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";

type LoaderParams = [BasicListItem[], BioSampleFileDetails[]];

function NcbiExportDetails(): JSX.Element {
  const [details, bioSampleFiles]: LoaderParams = useLoaderData();

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <Card title={i18n("project.export.sidebar.title")}>
        <BasicList dataSource={details} grid={{ gutter: 16, column: 2 }} />
      </Card>
      <Typography.Title level={5} style={{ color: blue6 }}>
        __BioSample Files
      </Typography.Title>
      {bioSampleFiles.map((bioSampleFile: BioSampleFileDetails) => {
        console.log(bioSampleFile);
        return (
          <Card>
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
                        style={{ backgroundColor: blue6 }}
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

export default NcbiExportDetails;
