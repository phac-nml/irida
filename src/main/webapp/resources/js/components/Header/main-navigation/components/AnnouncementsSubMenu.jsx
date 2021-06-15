/**
 * @file AnnouncementsSubMenu is the announcements drop down in the main navigation bar.
 */

import { Badge, Menu, Row, Typography } from "antd";
import React from "react";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import { BORDERED_LIGHT } from "../../../../styles/borders";
import { grey2, grey4 } from "../../../../styles/colors";
import { SPACE_MD } from "../../../../styles/spacing";
import { fromNow } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { LinkButton } from "../../../Buttons/LinkButton";
import { IconBell } from "../../../icons/Icons";
import { TYPES, useAnnouncements } from "./announcements-context";

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
    <>
      {announcements.length === 0 ? (
        <Menu.Item
          key={"announcement_none"}
          style={{ width: 400, borderBottom: BORDERED_LIGHT }}
          disabled={true}
        >
          {i18n("AnnouncementsSubMenu.emptyList")}
        </Menu.Item>
      ) : (
        announcements.map((item, index) => (
          <Menu.Item
            key={"announcement_" + index}
            style={{
              width: 400,
            }}
            icon={<PriorityFlag hasPriority={item.priority} />}
          >
            <LinkButton
              title={item.title}
              text={
                <Row justify="space-between" align="middle">
                  <Text ellipsis style={{ maxWidth: 260, color: grey2 }}>
                    {item.title}
                  </Text>
                  {/*<br />*/}
                  <Text
                    type="secondary"
                    style={{
                      fontSize: `.8em`,
                      color: grey4,
                      paddingRight: SPACE_MD,
                    }}
                  >
                    {fromNow({ date: item.createdDate })}
                  </Text>
                </Row>
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
    </>
  );

  return (
    <Menu.SubMenu
      key="announcements"
      title={
        <span className="announcements-dropdown">
          <Badge
            className="t-announcements-badge"
            count={announcements && announcements.filter((a) => !a.read).length}
          >
            <IconBell style={{ color: grey2 }} />
          </Badge>
        </span>
      }
    >
      {aMenu}
    </Menu.SubMenu>
  );
}
