import React, { useContext, forwardRef, useImperativeHandle } from "react";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { setBaseUrl } from "../../utilities/url-utilities";
import ReactMarkdown from "react-markdown";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { SPACE_SM, SPACE_XS } from "../../styles/spacing";
import { Button, Input, Table } from "antd";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";
import { EditAnnouncement } from "./EditAnnouncement";
import { DeleteAnnouncement } from "./DeleteAnnouncement";

export const AnnouncementsTable = forwardRef((props, ref) => {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    onSearch,
    handleTableChange,
    updateTable
  } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("iridaThing.id"),
      width: 80,
      dataIndex: "id",
      sorter: true
    },
    {
      title: i18n("AnnouncementTable.title"),
      dataIndex: "name",
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
    },
    {
      key: "actions",
      align: "right",
      fixed: "right",
      width: 120,
      render(text, record) {
        return (
          <span>
            <span style={{ marginRight: SPACE_XS }}>
              <EditAnnouncement
                announcement={record}
                updateAnnouncement={props.updateAnnouncement}
              />
            </span>
            <DeleteAnnouncement
              id={record.id}
              deleteAnnouncement={props.deleteAnnouncement}
            />
          </span>
        );
      }
    }
  ];

  /**
   * Handle searching through the external filter.
   * @param event
   */
  function tableSearch(event) {
    onSearch(event.target.value);
  }

  useImperativeHandle(ref, () => ({
    updateTable() {
      updateTable();
    }
  }));

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
});
