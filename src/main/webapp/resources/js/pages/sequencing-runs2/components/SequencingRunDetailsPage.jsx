import React from "react";
import { useParams } from "react-router-dom";
import {
  useGetSequencingRunDetailsQuery,
  useGetSequencingRunFilesQuery
} from "../../../apis/sequencing-runs/sequencing-runs";
import { Button, Card, Col, Row, Table } from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconDownloadFile } from "../../../../js/components/icons/Icons";
import { LinkButton } from "../../../components/Buttons/LinkButton";

/**
 * React component to display the sequencing run details page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunDetailsPage() {
  const {runId} = useParams();
  const {data: run = {}} = useGetSequencingRunDetailsQuery(runId);
  const {data: files = []} = useGetSequencingRunFilesQuery(runId);
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
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
    },
    {
      dataIndex: 'download',
      key: 'download',
      render(text, item) {
        return <Button type="primary"
                       onClick={() => window.open(setBaseUrl(`/sequenceFiles/download/${item.sequencingObjectId}/file/${item.id}`), "_blank")}
                       icon={<IconDownloadFile/>}/>;
      },
    }
  ]

  console.log("runID = " + runId);
  console.log("run = " + JSON.stringify(run));
  console.log("files = " + JSON.stringify(files));

  return (
    <PageWrapper title={i18n("SequenceRunDetailsPage.title", runId)}>
      <Card title="Sequencer">
        <Row justify="center">
          <Col span={6}>ID</Col>
          <Col span={6}>{runId}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Sequencer Type</Col>
          <Col span={6}>{run.sequencerType}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Upload Status</Col>
          <Col span={6}>{run.uploadStatus}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Upload User</Col>
          <Col span={6}>{run.identifier}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Created</Col>
          <Col span={6}>{formatDate({date: run.createdDate})}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Description</Col>
          <Col span={6}>{run.description}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Workflow</Col>
          <Col span={6}>{run.workflow}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Assay</Col>
          <Col span={6}>{run.assay}</Col>
        </Row>
        <Row justify="center">
          <Col span={6}>Read Lengths</Col>
          <Col span={6}>{run.read_lengths}</Col>
        </Row>
      </Card>

      <Table dataSource={files} columns={columns}/>

    </PageWrapper>
  );
}