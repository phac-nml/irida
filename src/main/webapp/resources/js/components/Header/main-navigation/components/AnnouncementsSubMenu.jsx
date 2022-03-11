/**
 * @file AnnouncementsSubMenu is the announcements drop down in the main navigation bar.
 */

import { Badge, Button, Menu, Space, Typography } from "antd";
import React from "react";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import { BORDERED_LIGHT } from "../../../../styles/borders";
import { fromNow } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconBell, IconShoppingCart } from "../../../icons/Icons";
import { TYPES, useAnnouncements } from "./announcements-context";
import { theme } from "../../../../utilities/theme-utilities";
import { grey6 } from "../../../../styles/colors";
import styled from "styled-components";

const { Text } = Typography;

const textColor = theme === "dark" ? `${grey6}` : "#222";
const hoverColor = theme === "dark" ? "#fff" : "#222";
const iconColor = theme === "dark" ? "#fff" : "#222";

const TextStyle = styled(Text)`
  color: ${textColor} !important;

  :hover {
    color: ${hoverColor} !important;
  }
`;

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu() {
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
    <Menu theme="dark" mode="horizontal">
      <Menu.SubMenu
        key="announcements"
        title={
          <Badge
            className="t-announcements-badge"
            count={announcements && announcements.filter((a) => !a.read).length}
          >
            <Button
              type="link"
              href="#"
              className="t-announcements-button"
              icon={<IconBell />}
            />
          </Badge>
        }
      >
        {announcements.length == 0 ? (
          <Menu.Item
            key="announcement_none"
            style={{ width: 400, borderBottom: BORDERED_LIGHT }}
            disabled={true}
          >
            {i18n("AnnouncementsSubMenu.emptyList")}
          </Menu.Item>
        ) : (
          announcements.map((item, index) => (
            <Menu.Item
              key={"announcement_" + index}
              style={{ width: 400, borderBottom: BORDERED_LIGHT }}
            >
              <Button
                type="link"
                onClick={() => {
                  showAnnouncementModal(index);
                }}
              >
                <Space size="small">
                  <PriorityFlag hasPriority={item.priority} />
                  <span>
                    <TextStyle strong ellipsis style={{ width: 310 }}>
                      {item.title}
                    </TextStyle>
                    <br />
                    <TextStyle type="secondary" style={{ fontSize: `.8em` }}>
                      {fromNow({ date: item.createdDate })}
                    </TextStyle>
                  </span>
                </Space>
              </Button>
            </Menu.Item>
          ))
        )}
        <Menu.Item key="view_all">
          <a
            type="link"
            className="t-announcements-view-all"
            href={setBaseUrl(`/announcements/user/list`)}
          >
            {i18n("AnnouncementsSubMenu.view-all")}
          </a>
        </Menu.Item>
      </Menu.SubMenu>
    </Menu>
  );
}
