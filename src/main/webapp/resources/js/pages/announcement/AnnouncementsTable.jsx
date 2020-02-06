import React, { forwardRef, useContext, useImperativeHandle } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { SPACE_XS } from "../../styles/spacing";
import { EditAnnouncement } from "./EditAnnouncement";
import { DeleteAnnouncement } from "./DeleteAnnouncement";
import {
  PagedTable,
  PagedTableContext
} from "../../components/ant.design/PagedTable";
import RichTextEditor from "react-rte/lib/RichTextEditor";

export const AnnouncementsTable = forwardRef((props, ref) => {
  const { updateTable } = useContext(PagedTableContext);

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
            <RichTextEditor
              value={RichTextEditor.createValueFromString(text, "markdown")}
              readOnly
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
