import React from "react";
import { Badge, Menu } from "antd";
import { IconBell } from "../../../icons/Icons";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import "./announcements.css";
import { LinkButton } from "../../../Buttons/LinkButton";
import { TYPES, useAnnouncements } from "./announcements-context";

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
              text={item.title}
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
