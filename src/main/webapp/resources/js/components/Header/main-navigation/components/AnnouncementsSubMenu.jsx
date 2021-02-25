import React from "react";
import { Badge, Menu, Space, Typography } from "antd";
import { IconBell } from "../../../icons/Icons";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import "./announcements.css";
import { LinkButton } from "../../../Buttons/LinkButton";
import { TYPES, useAnnouncements } from "./announcements-context";
import { fromNow } from "../../../../utilities/date-utilities";

const { Text } = Typography;

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu(props) {
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

  return (
    <>
      <Menu.SubMenu
        popupClassName="announcement-dd"
        title={
          <Badge
            count={announcements && announcements.filter((a) => !a.read).length}
          >
            <IconBell />
          </Badge>
        }
        {...props}
      >
        {announcements.map((item, index) => (
          <Menu.Item key={"announcement_" + index}>
            <LinkButton
              title={item.title}
              text={
                <Space direction="vertical">
                  <Text>{item.title.substring(0, 100)}</Text>
                  <Text type="secondary" style={{ fontSize: `.8em` }}>
                    {fromNow({ date: item.createdDate })}
                  </Text>
                </Space>
              }
              onClick={() => {
                showAnnouncementModal(index);
              }}
            />
          </Menu.Item>
        ))}
        {announcements.length > 0 && <Menu.Divider />}
        <Menu.Item key="view_all">
          <LinkButton
            text={i18n("AnnouncementsSubMenu.view-all")}
            href={setBaseUrl(`/announcements/user/list`)}
          />
        </Menu.Item>
      </Menu.SubMenu>
    </>
  );
}
