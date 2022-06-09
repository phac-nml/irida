import * as React from "react";
import {useParams} from "react-router-dom";
import {getNcbiSubmission} from "../../../../apis/export/ncbi";
import type {NcbiSubmission} from "../../../../types/irida";
import {BioSampleFileDetails, formatNcbiUploadDetails, formatNcbiUploadFiles,} from "./utils";
import {Card, List, Skeleton} from "antd";
import {BasicList} from "../../../../components/lists";
import {BasicListItem} from "../../../../components/lists/BasicList.types";
import NcbiBioSampleFile from "./NcbiBioSampleFile";

interface RouteParams {
  projectId: string;
  id: string;
}

function NcbiExportDetails(): JSX.Element {
  const { projectId, id } = useParams<keyof RouteParams>() as RouteParams;

  const [details, setDetails] = React.useState<BasicListItem[]>([]);
  const [bioSampleFiles, setBioSampleFiles] = React.useState<
    BioSampleFileDetails[]
  >([]);
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    getNcbiSubmission(parseInt(projectId), parseInt(id)).then(
      (submission: NcbiSubmission) => {
        const { bioSampleFiles, ...info } = submission;
        setDetails(formatNcbiUploadDetails(info));
        setBioSampleFiles(formatNcbiUploadFiles(bioSampleFiles));
        setLoading(false);
      }
    );
  }, [id, projectId]);

  return (
    <Skeleton active={true} loading={loading}>
      <Card title={i18n("project.export.sidebar.title")}>
        <BasicList dataSource={details} grid={{ gutter: 16, column: 2 }} />

        <List
          bordered
          itemLayout="vertical"
          dataSource={bioSampleFiles}
          renderItem={(sample) => (
            <List.Item>
              <List.Item.Meta title={sample.key} />
              <NcbiBioSampleFile key={sample.key} bioSample={sample} />
            </List.Item>
          )}
        />
      </Card>
    </Skeleton>
  );
}

export default NcbiExportDetails;
