import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { Col, Row } from "antd";
import { PageWrapper } from "../../../components/page/PageWrapper";
import SequencingRunNav from "./SequencingRunNav";
import { ContentLoading } from "../../../components/loader";

/**
 * React component that layouts the sequencing run page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunLayout() {
  const {runId} = useParams();

  return (
    <PageWrapper
      title={i18n("SequencingRunLayout.title", runId)}>
      <Row>
        <Col span={4}>
          <SequencingRunNav/>
        </Col>
        <Col offset={1} span={8}>
          <React.Suspense fallback={<ContentLoading/>}>
            <Outlet/>
          </React.Suspense>
        </Col>
      </Row>
    </PageWrapper>
  );
}