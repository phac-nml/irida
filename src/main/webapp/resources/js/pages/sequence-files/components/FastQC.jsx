/*
 * This file is responsible for displaying the
 * tabs required and default renders the
 * FastQCCharts component.
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { Link } from "@reach/router";
import { Badge, Menu, Space } from "antd";
import { ContentLoading } from "../../../components/loader";

import { SPACE_XS } from "../../../styles/spacing";
import { InfoAlert } from "../../../components/alerts";
import { blue6 } from "../../../styles/colors";
import { FastQCProvider, useFastQCState } from "../fastqc-context";

function FastQCMenu({ route, uri }) {
  const { loading, fastQC } = useFastQCState();

  return (
    <Menu mode="horizontal" selectedKeys={[route]} className="t-fastQC-nav">
      <Menu.Item key="charts">
        <Link to={`${uri}/charts`}>{i18n("FastQC.charts")}</Link>
      </Menu.Item>
      <Menu.Item key="overrepresented">
        <Link to={`${uri}/overrepresented`}>
          {i18n("FastQC.overrepresentedSequences")}
          <Badge
            count={loading ? "-" : fastQC.overrepresentedSequences.length}
            showZero
            style={{ backgroundColor: blue6, marginLeft: SPACE_XS }}
            className="t-overrepresented-sequences-count"
          />
        </Link>
      </Menu.Item>
      <Menu.Item key="details">
        <Link to={`${uri}/details`}>{i18n("FastQC.details")}</Link>
      </Menu.Item>
    </Menu>
  );
}

function FastQCContent({ children, route, uri }) {
  const { loading, fastQC, file } = useFastQCState();

  return loading ? (
    <ContentLoading />
  ) : (
    <PageWrapper title={file.fileName}>
      {fastQC ? (
        <Space direction="vertical" style={{ width: `100%` }}>
          <FastQCMenu route={route} uri={uri} />
          {children}
        </Space>
      ) : (
        <div>
          <InfoAlert
            message={i18n("FastQC.noResults")}
            style={{ marginBottom: SPACE_XS }}
            className="t-fastQC-no-run"
          />
        </div>
      )}
    </PageWrapper>
  );
}

export default function FastQC({
  sequenceObjectId,
  fileId,
  children,
  route,
  uri,
}) {
  return (
    <FastQCProvider sequenceObjectId={sequenceObjectId} fileId={fileId}>
      <FastQCContent route={route} uri={uri}>
        {children}
      </FastQCContent>
    </FastQCProvider>
  );
}
