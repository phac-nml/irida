import React from "react";
import { useParams } from "react-router-dom";
import {
  useGetSequencingRunDetailsQuery,
  useGetSequencingRunFilesQuery
} from "../../../apis/sequencing-runs/sequencing-runs";
import { Badge, Button, Col, Row, Table, Typography } from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconDownloadFile } from "../../../components/icons/Icons";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { BasicList } from "../../../components/lists";
import { NarrowPageWrapper } from "../../../components/page/NarrowPageWrapper";

/**
 * React component to display the sequencing run details page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunDetailsPage() {
  const ADMIN_SEQUENCE_RUNS_URL = "admin/sequencing_runs";
  const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
  const showBack = isAdmin && document.referrer.includes(ADMIN_SEQUENCE_RUNS_URL);
  const {runId} = useParams();
  const goToAdminSequenceRunListPage = () =>
    (window.location.href = setBaseUrl(ADMIN_SEQUENCE_RUNS_URL));
  const {
    data: run = {},
    isLoading: isRunLoading
  } = useGetSequencingRunDetailsQuery(runId);
  const {
    data: files = [],
    isLoading: isFilesLoading
  } = useGetSequencingRunFilesQuery(runId);
  const detailsList = isRunLoading
    ? []
    : [
      {
        title: "Sequencer Type",
        desc: run.sequencerType
      },
      {
        title: "Upload Status",
        desc: <UploadStatusBadge status={run.uploadStatus}/>
      },
      {
        title: "Upload User",
        desc: isAdmin ? (<LinkButton
            text={run.userName}
            href={setBaseUrl(`/users/${run.userId}`)}/>
        ) : run.userName
      },
      {
        title: "Created",
        desc: formatDate({date: run.createdDate})
      },
      {
        title: "Description",
        desc: run.description
      },
    ];
  const optionalPropertiesList = [];
  run.optionalProperties && Object.entries(run.optionalProperties).map(([key, value]) => (
    optionalPropertiesList.push({title: key, desc: value})))

  const columns = [
    {
      title: 'Filename',
      dataIndex: 'fileName',
      key: 'fileName',
      render(text, item) {
        return <LinkButton
          text={text}
          href={setBaseUrl(`/sequencingRuns/${runId}/sequenceFiles/${item.sequencingObjectId}/file/${item.id}/summary`)}
        />
      },
    },
    {
      title: 'Size',
      dataIndex: 'fileSize',
      key: 'fileSize',
      align: 'right'
    },
    {
      title: 'Download',
      dataIndex: 'download',
      key: 'download',
      render(text, item) {
        return <Button shape="circle"
                       onClick={() => window.open(setBaseUrl(`/sequenceFiles/download/${item.sequencingObjectId}/file/${item.id}`), "_blank")}
                       icon={<IconDownloadFile/>}/>;
      },
      align: 'center'
    }
  ]

  return (
    <NarrowPageWrapper title={i18n("SequenceRunDetailsPage.title", runId)}
                       onBack={showBack ? goToAdminSequenceRunListPage : undefined}>
      <Row justify="center" gutter={[0, 16]}>
        <Col span={18}>
          <Typography.Title level={2}>Details</Typography.Title>
        </Col>
        <Col span={18}>
          <BasicList dataSource={detailsList} grid={{gutter: 16, column: 4}}/>
        </Col>
        <Col span={18}>
          <Typography.Title level={2}>Additional Properties</Typography.Title>
        </Col>
        <Col span={18}>
          <BasicList dataSource={optionalPropertiesList}
                     grid={{gutter: 16, column: 4}}/>
        </Col>
        <Col span={18}>
          <Typography.Title level={2}>Files</Typography.Title>
        </Col>
        <Col span={18}>
          <Table loading={isFilesLoading}
                 dataSource={files}
                 columns={columns}
                 scroll={{x: "max-content", y: 600}}
                 pagination={false}/>
        </Col>
      </Row>
    </NarrowPageWrapper>
  );
}

function UploadStatusBadge({status}) {
  switch (status) {
    case "ERROR":
      return <Badge status="error" text={status}/>;
    case "UPLOADING":
      return <Badge status="warning" text={status}/>;
    case "COMPLETE":
      return <Badge status="success" text={status}/>;
    default:
      return <Badge text={status}/>;
  }
}