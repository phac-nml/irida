import React, { useContext } from "react";
import { render } from "react-dom";
import { Input, Table } from "antd";
import {
  PagedTableContext,
  PagedTableProvider
} from "../../contexts/PagedTableContext";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PageWrapper } from "../../components/page/PageWrapper";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import ReactMarkdown from "react-markdown";
import { SPACE_SM } from "../../styles/spacing";
import { CreateNewAnnouncement } from "./CreateNewAnnouncement";

function AnnouncementsTable() {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    onSearch,
    handleTableChange
  } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("iridaThing.id"),
      width: 80,
      dataIndex: "id",
      sorter: true
    },
    {
      title: i18n("announcement.control.message"),
      dataIndex: "message",
      className: "t-announcement",
      render(text, full) {
        return (
          <a href={setBaseUrl(`announcements/${full.id}/details`)}>
            <ReactMarkdown
              source={text}
              disallowedTypes={["paragraph"]}
              unwrapDisallowed
            />
          </a>
        );
      }
    },
    {
      title: i18n("announcement.control.createdBy"),
      dataIndex: "user",
      render(text, item) {
        return <a href={item.user.id}>{item.user.username}</a>;
      }
    },
    {
      ...dateColumnFormat(),
      className: "t-created-date",
      title: i18n("iridaThing.timestamp"),
      dataIndex: "createdDate"
    }
  ];

  /**
   * Handle searching through the external filter.
   * @param event
   */
  function tableSearch(event) {
    onSearch(event.target.value);
  }

  return (
    <>
      <div
        style={{
          display: "flex",
          flexDirection: "row-reverse",
          marginBottom: SPACE_SM
        }}
      >
        <Input.Search style={{ width: 250 }} onChange={tableSearch} />
      </div>
      <Table
        dataSource={dataSource}
        columns={columns}
        loading={loading}
        onChange={handleTableChange}
        pagination={{ total, pageSize, hideOnSinglePage: true }}
      />
    </>
  );
}
render(
  <PageWrapper
    title={i18n("announcement.admin-menu")}
    headerExtras={<CreateNewAnnouncement />}
  >
    <PagedTableProvider url={setBaseUrl(`announcements/control/ajax/list`)}>
      <AnnouncementsTable />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#announcement-root")
);
