/**
 * @file AnnouncementsSubMenu is the announcements drop down in the main navigation bar.
 */
import React from "react";
import { Avatar, Badge, Button, List, Popover } from "antd";
import { blue6, grey6 } from "../../../../styles/colors";
import { fromNow } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconFlag } from "../../../icons/Icons";
import { TYPES, useAnnouncements } from "./announcements-context";
import { BellOutlined } from "@ant-design/icons";

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu(): JSX.Element {
  const [{ announcements }, dispatch] = useAnnouncements();

  function showAnnouncementModal(index) {
    dispatch({
      type: TYPES.SHOW_ANNOUNCEMENT,
      payload: {
        index,
        isPriority: false,
      },
    });
  }

  const Announcements = () => (
    <List
      className="t-announcements-submenu"
      size="small"
      itemLayout="horizontal"
    >
      {announcements.map((announcement, index) => (
        <List.Item key={`announcement_${announcement.identifier}`}>
          <List.Item.Meta
            avatar={
              <Avatar
                size={25}
                icon={<IconFlag />}
                style={{
                  backgroundColor: announcement.priority ? blue6 : grey6,
                }}
              />
            }
            title={
              <a
                style={{ padding: 0 }}
                onClick={() => showAnnouncementModal(index)}
              >
                {announcement.title}
              </a>
            }
            description={fromNow({ date: announcement.createdDate })}
          />
        </List.Item>
      ))}
      {announcements.length === 0 && (
        <List.Item>{i18n("AnnouncementsSubMenu.emptyList")}</List.Item>
      )}
      <List.Item>
        <a
          className="t-announcements-view-all"
          href={setBaseUrl(`/announcements/user/list`)}
        >
          {i18n("AnnouncementsSubMenu.view-all")}
        </a>
      </List.Item>
    </List>
  );

  return (
    <Popover
      placement="bottomRight"
      content={
        <div style={{ width: 300 }}>
          <Announcements />
        </div>
      }
      trigger="click"
    >
      <Badge
        className="t-announcements-badge"
        count={announcements && announcements.filter((a) => !a.read).length}
        offset={[5, -3]}
      >
        <BellOutlined className="t-announcements-button" />
      </Badge>
    </Popover>
  );
}
