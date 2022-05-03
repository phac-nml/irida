import React from "react";
import { useParams } from "react-router-dom";
import {
  useGetSequencingRunDetailsQuery,
  useGetSequencingRunFilesQuery
} from "../../../apis/sequencing-runs/sequencing-runs";
import { Card, Col, Row, Table } from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import { PageWrapper } from "../../../components/page/PageWrapper";

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
      dataIndex: 'identifier',
      key: 'identifier',
    },
    {
      title: 'Filename',
      dataIndex: 'fileName',
      key: 'fileName',
    },
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