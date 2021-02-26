import React from "react";
import { Badge, Dropdown, Menu, Space, Typography } from "antd";
import { IconBell } from "../../../icons/Icons";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { LinkButton } from "../../../Buttons/LinkButton";
import { TYPES, useAnnouncements } from "./announcements-context";
import { fromNow } from "../../../../utilities/date-utilities";
import { BORDERED_LIGHT } from "../../../../styles/borders";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import "./announcements.css";

const { Text } = Typography;

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
    <Menu>
      {announcements.map((item, index) => (
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
                  <Text strong ellipsis style={{ width: 310 }}>
                    {item.title}
                  </Text>
                  <br />
                  <Text type="secondary" style={{ fontSize: `.8em` }}>
                    {fromNow({ date: item.createdDate })}
                  </Text>
                </span>
              </Space>
            }
            onClick={() => {
              showAnnouncementModal(index);
            }}
          />
        </Menu.Item>
      ))}
      <Menu.Item key="view_all">
        <LinkButton
          text={i18n("AnnouncementsSubMenu.view-all")}
          href={setBaseUrl(`/announcements/user/list`)}
        />
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={aMenu}>
      <span style={{ padding: 20 }}>
        <Badge
          count={announcements && announcements.filter((a) => !a.read).length}
        >
          <IconBell />
        </Badge>
      </span>
    </Dropdown>
  );
}
