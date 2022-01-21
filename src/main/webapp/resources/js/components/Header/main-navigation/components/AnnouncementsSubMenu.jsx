/**
 * @file AnnouncementsSubMenu is the announcements drop down in the main navigation bar.
 */

import { Badge, Dropdown, Menu, Space, Typography } from "antd";
import React from "react";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import { BORDERED_LIGHT } from "../../../../styles/borders";
import { fromNow } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { LinkButton } from "../../../Buttons/LinkButton";
import { IconBell } from "../../../icons/Icons";
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

  const aMenu = (
    <Menu className="t-announcements-submenu" theme={theme}>
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
            <LinkButton
              title={item.title}
              text={
                <Space size="large">
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
              }
              onClick={() => {
                showAnnouncementModal(index);
              }}
            />
          </Menu.Item>
        ))
      )}
      <Menu.Item key="view_all">
        <LinkButton
          className="t-announcements-view-all"
          text={i18n("AnnouncementsSubMenu.view-all")}
          href={setBaseUrl(`/announcements/user/list`)}
        />
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={aMenu}>
      <span className="announcements-dropdown">
        <Badge
          className="t-announcements-badge"
          count={announcements && announcements.filter((a) => !a.read).length}
        >
          <IconBell style={{ color: iconColor }} />
        </Badge>
      </span>
    </Dropdown>
  );
}
