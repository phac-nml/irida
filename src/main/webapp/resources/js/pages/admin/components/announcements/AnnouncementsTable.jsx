/**
 * @fileOverview React component to render the announcements table.
 */
import React, { forwardRef, useContext, useImperativeHandle } from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../../components/ant.design/table-renderers";
import { SPACE_XS } from "../../../../styles/spacing";
import { EditAnnouncement } from "./EditAnnouncement";
import { DeleteAnnouncement } from "./DeleteAnnouncement";
import { IconFlag } from "../../../../components/icons/Icons";
import { blue6, grey2 } from "../../../../styles/colors";
import {
  PagedTable,
  PagedTableContext
} from "../../../../components/ant.design/PagedTable";
import { MarkdownViewer } from "../../../../components/markdown/MarkdownViewer";

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
      sorter: true
    },
    {
      title: i18n("announcement.control.title"),
      align: "left",
      fixed: "left",
      dataIndex: "title",
      className: "t-announcement",
      render(text, full) {
        return (
            <a href={setBaseUrl(`announcements/${full.id}/details`)}>
              <MarkdownViewer markdown={text}/>
            </a>
        );
      },
      sorter: true
    },
    {
      title: i18n("announcement.control.createdBy"),
      dataIndex: "user",
      render(text, item) {
        return <a href={item.user.id}>{item.user.username}</a>;
      },
      sorter: true
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

  useImperativeHandle(ref, () => ({
    updateTable() {
      updateTable();
    }
  }));

  return <PagedTable columns={columns} />;
});
