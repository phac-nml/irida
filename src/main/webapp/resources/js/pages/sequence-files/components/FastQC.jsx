/*
 * This file is responsible for displaying the
 * tabs required and default renders the
 * FastQCCharts component.
 */

import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { Link } from "@reach/router";
import { Badge, Menu, Space } from "antd";
import { ContentLoading } from "../../../components/loader";

import { SPACE_XS } from "../../../styles/spacing";
import { getFastQCDetails } from "../../../apis/files/sequence-files";
import { InfoAlert } from "../../../components/alerts";
import { blue6 } from "../../../styles/colors";

export default function FastQC({
  sequenceFileId,
  fileId,
  children,
  route,
  uri,
}) {
  const [loading, setLoading] = useState(true);
  const [fastQC, setFastQC] = useState({});
  const [file, setFile] = useState({});

  useEffect(() => {
    getFastQCDetails(sequenceFileId, fileId).then(
      ({ analysisFastQC, sequenceFile }) => {
        setFile(sequenceFile);
        setFastQC(analysisFastQC);
        setLoading(false);
      }
    );
  }, []);

  return (
    <PageWrapper title={file.fileName}>
      {loading ? (
        <ContentLoading message={i18n("FastQC.fetchingData")} />
      ) : fastQC ? (
        <Space direction="vertical" style={{ width: `100%` }}>
          <Menu
            mode="horizontal"
            selectedKeys={[route]}
            className="t-fastQC-nav"
          >
            <Menu.Item key="charts">
              <Link to={`${uri}/charts`}>{i18n("FastQC.charts")}</Link>
            </Menu.Item>
            <Menu.Item key="overrepresented">
              <Link to={`${uri}/overrepresented`}>
                {i18n("FastQC.overrepresentedSequences")}
                <Badge
                  count={fastQC.overrepresentedSequences.length}
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
