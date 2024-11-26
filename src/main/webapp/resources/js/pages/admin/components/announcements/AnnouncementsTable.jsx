/**
 * @fileOverview React component to render the announcements table.
 */
import React, { forwardRef, useContext, useImperativeHandle } from "react";
import { Space } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../../components/ant.design/table-renderers";
import { DeleteAnnouncement } from "./DeleteAnnouncement";
import EditAnnouncement from "./EditAnnouncement";
import ViewAnnouncement from "./ViewAnnouncement";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";
import { PriorityFlag } from "../../../announcement/components/PriorityFlag";

/**
 * React component to render the announcements table.
 * Requires a `ref` to be passed in so parent can update the table.
 * @type {React.ForwardRefExoticComponent<React.PropsWithoutRef<{}> & React.RefAttributes<unknown>>}
 */
export const AnnouncementsTable = forwardRef((props, ref) => {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("AnnouncementsTable.priority"),
      align: "center",
      dataIndex: "priority",
      render(hasPriority) {
        return <PriorityFlag hasPriority={hasPriority} />;
      },
      sorter: true,
      width: 50,
    },
    {
      title: i18n("AnnouncementsTable.title"),
      className: "t-announcement",
      align: "left",
      dataIndex: "title",
      sorter: true,
    },
    {
      title: i18n("AnnouncementsTable.createdBy"),
      dataIndex: "user",
      render(text, item) {
        return (
          <a href={setBaseUrl(`/users/${item.user.identifier}`)}>
            {item.user.username}
          </a>
        );
      },
      sorter: true,
    },
    {
      ...dateColumnFormat(),
      className: "t-created-date",
      title: i18n("iridaThing.timestamp"),
      dataIndex: "createdDate",
    },
    {
      key: "actions",
      align: "right",
      fixed: "right",
      width: 110,
      render(text, record) {
        return (
          <Space>
            <ViewAnnouncement announcement={record} />
            <EditAnnouncement
              announcement={record}
              updateAnnouncement={props.updateAnnouncement}
            />
            <DeleteAnnouncement
              id={record.id}
              deleteAnnouncement={props.deleteAnnouncement}
            />
          </Space>
        );
      },
    },
  ];

  useImperativeHandle(ref, () => ({
    updateTable() {
      updateTable();
    },
  }));

  return <PagedTable columns={columns} />;
});

AnnouncementsTable.displayName = "AnnouncementsTable";
