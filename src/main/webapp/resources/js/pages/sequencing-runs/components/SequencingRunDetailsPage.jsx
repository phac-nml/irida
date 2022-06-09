import React from "react";
import { useParams } from "react-router-dom";
import {
  useGetSequencingRunDetailsQuery,
  useGetSequencingRunFilesQuery,
} from "../../../apis/sequencing-runs/sequencing-runs";
import { Button, Col, Layout, Row, Table, Typography } from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconDownloadFile } from "../../../components/icons/Icons";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { BasicList } from "../../../components/lists";
import { NarrowPageWrapper } from "../../../components/page/NarrowPageWrapper";
import { SequencingRunStatusBadge } from "./SequencingRunStatusBadge";
import { grey1 } from "../../../styles/colors";
import { SPACE_LG } from "../../../styles/spacing";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";

const { Content } = Layout;

/**
 * React component to display the sequencing run details page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunDetailsPage() {
  const ADMIN_SEQUENCE_RUNS_URL = "admin/sequencing-runs";
  const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
  const showBack =
    isAdmin && document.referrer.includes(ADMIN_SEQUENCE_RUNS_URL);
  const { runId } = useParams();
  const goToAdminSequenceRunListPage = () =>
    (window.location.href = setBaseUrl(ADMIN_SEQUENCE_RUNS_URL));
  const { data: run = {}, isLoading: isRunLoading } =
    useGetSequencingRunDetailsQuery(runId);
  const { data: files = [], isLoading: isFilesLoading } =
    useGetSequencingRunFilesQuery(runId);
  const detailsList = isRunLoading
    ? []
    : [
        {
          title: i18n("SequencingRunDetailsPage.list.sequencerType"),
          desc: run.sequencerType,
        },
        {
          title: i18n("SequencingRunDetailsPage.list.uploadStatus"),
          desc: <SequencingRunStatusBadge status={run.uploadStatus} />,
        },
        {
          title: i18n("SequencingRunDetailsPage.list.uploadUser"),
          desc: isAdmin ? (
            <LinkButton
              text={run.userName}
              href={setBaseUrl(`/users/${run.userID}`)}
            />
          ) : (
            run.userName
          ),
        },
        {
          title: i18n("SequencingRunDetailsPage.list.createdDate"),
          desc: formatDate({ date: run.createdDate }),
        },
        {
          title: i18n("SequencingRunDetailsPage.list.description"),
          desc: run.description,
        },
      ];

  const optionalPropertiesList = [];
  run.optionalProperties &&
    Object.entries(run.optionalProperties).map(([key, value]) =>
      optionalPropertiesList.push({ title: key, desc: value })
    );

  const columns = [
    {
      title: i18n("SequencingRunDetailsPage.table.fileName"),
      dataIndex: "fileName",
      key: "fileName",
      render(text, item) {
        return (
          <LinkButton
            text={text}
            className="t-file-link"
            href={setBaseUrl(
              `/sequencing-runs/${runId}/sequenceFiles/${item.sequencingObjectId}/file/${item.id}/summary`
            )}
          />
        );
      },
    },
    {
      title: i18n("SequencingRunDetailsPage.table.size"),
      dataIndex: "fileSize",
      key: "fileSize",
      align: "right",
    },
    {
      title: i18n("SequencingRunDetailsPage.table.download"),
      dataIndex: "download",
      key: "download",
      render(text, item) {
        return (
          <Button
            shape="circle"
            onClick={() =>
              window.open(
                setBaseUrl(
                  `/sequenceFiles/download/${item.sequencingObjectId}/file/${item.id}`
                ),
                "_blank"
              )
            }
            icon={<IconDownloadFile />}
          />
        );
      },
      align: "center",
    },
  ];

  return (
    <NarrowPageWrapper
      title={i18n("SequencingRunDetailsPage.title", runId)}
      onBack={showBack ? goToAdminSequenceRunListPage : undefined}
      headerExtras={
        <AddNewButton
          href={setBaseUrl(`sequencing-runs/${runId}/samples`)}
          text={i18n("SequencingRunDetailsPage.button")}
        />
      }
    >
      <Layout>
        <Content
          style={{
            backgroundColor: grey1,
            padding: SPACE_LG,
            marginBottom: SPACE_LG,
          }}
        >
          <Row justify="center" gutter={[0, 16]}>
            <Col span={24}>
              <Typography.Title level={2}>
                {i18n("SequencingRunDetailsPage.title.details")}
              </Typography.Title>
            </Col>
            <Col span={24}>
              <BasicList
                dataSource={detailsList}
                grid={{ gutter: 16, column: 4 }}
              />
            </Col>
            <Col span={24}>
              <Typography.Title level={2}>
                {i18n("SequencingRunDetailsPage.title.optionalProperties")}
              </Typography.Title>
            </Col>
            <Col span={24}>
              <BasicList
                dataSource={optionalPropertiesList}
                grid={{ gutter: 16, column: 4 }}
              />
            </Col>
            <Col span={24}>
              <Typography.Title level={2}>
                {i18n("SequencingRunDetailsPage.title.files")}
              </Typography.Title>
            </Col>
            <Col span={24}>
              <Table
                loading={isFilesLoading}
                dataSource={files}
                columns={columns}
                scroll={{ x: "max-content", y: 600 }}
                pagination={false}
                rowKey={(record) => record.id}
                className="t-files-table"
              />
            </Col>
          </Row>
        </Content>
      </Layout>
    </NarrowPageWrapper>
  );
}
