/*
 * This file is responsible for displaying the
 * tabs required and default renders the
 * FastQCCharts component.
 */

import { Badge, Layout, Menu, Skeleton, Space } from "antd";
import React from "react";
import { Link, Outlet, useLocation, useParams } from "react-router-dom";
import { InfoAlert } from "../../../components/alerts";
import { blue6, grey1 } from "../../../styles/colors";

import { SPACE_LG, SPACE_XS } from "../../../styles/spacing";
import { FastQCProvider, useFastQCState } from "../fastqc-context";
import { NarrowPageWrapper } from "../../../components/page/NarrowPageWrapper";

const { Content } = Layout;

function FastQCMenu({ current }) {
  const location = useLocation();
  const [key, setKey] = React.useState(current);
  const { loading, fastQC } = useFastQCState();

  let uri = location.pathname;
  if (uri.endsWith(current)) {
    // Strip of the current location as it will mess up the links
    uri = uri.replace(`/${current}`, "");
  }

  return (
    <Menu
      mode="horizontal"
      selectedKeys={[key]}
      className="t-fastQC-nav"
      onClick={(e) => setKey(e.key)}
    >
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

function FastQCContent({ children, current, uri }) {
  const { loading, fastQC, file = {}, processingState } = useFastQCState();

  const processingStateTranslations = {
    UNPROCESSED: i18n("FastQC.sequencingobject.unprocessed"),
    QUEUED: i18n("FastQC.sequencingobject.queued"),
    PROCESSING: i18n("FastQC.sequencingobject.processing"),
    ERROR: i18n("FastQC.sequencingobject.error"),
    FINISHED: i18n("FastQC.sequencingobject.finished"),
  };

  return (
    <Skeleton loading={loading} active>
      <NarrowPageWrapper title={file.fileName}>
        <Layout>
          <Content
            style={{
              backgroundColor: grey1,
              padding: SPACE_LG,
              marginBottom: SPACE_LG,
            }}
          >
            {fastQC ? (
              <Space direction="vertical" style={{ width: `100%` }}>
                <FastQCMenu current={current} uri={uri} />
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
          </Content>
        </Layout>
      </NarrowPageWrapper>
    </Skeleton>
  );
}

export default function FastQC({ current }) {
  const { sequenceObjectId, fileId } = useParams();
  return (
    <FastQCProvider sequenceObjectId={sequenceObjectId} fileId={fileId}>
      <FastQCContent current={current}>
        <Outlet />
      </FastQCContent>
    </FastQCProvider>
  );
}
