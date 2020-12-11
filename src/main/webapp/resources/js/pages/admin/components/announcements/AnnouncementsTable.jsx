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
import { IconFlag } from "../../../../components/icons/Icons";
import { blue6, grey2 } from "../../../../styles/colors";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";

/**
 * React component to render the announcements table.
 * Requires a `ref` to be passed in so parent can update the table.
 * @type {React.ForwardRefExoticComponent<React.PropsWithoutRef<{}> & React.RefAttributes<unknown>>}
 */
export const AnnouncementsTable = forwardRef((props, ref) => {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("announcement.control.priority"),
      align: "center",
      fixed: "left",
      dataIndex: "priority",
      render(hasPriority) {
        return <IconFlag style={{ color: hasPriority ? blue6 : grey2 }} />;
      },
      sorter: true,
      width: 50,
    },
    {
      title: i18n("announcement.control.title"),
      align: "left",
      fixed: "left",
      dataIndex: "title",
      className: "t-announcement",
      sorter: true,
    },
    {
      title: i18n("announcement.control.createdBy"),
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
      className: "t-delete-announcement",
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
