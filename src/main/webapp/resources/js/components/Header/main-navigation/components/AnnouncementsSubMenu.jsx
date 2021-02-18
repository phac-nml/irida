import React, { useEffect, useState } from "react";
import { Badge, Button, Menu, notification, Space, Typography } from "antd";
import { IconBell } from "../../../icons/Icons";
import {
  getAnnouncement,
  getUnreadAnnouncements,
} from "../../../../apis/announcements/announcements";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import "./announcements.css";
import { LinkButton } from "../../../Buttons/LinkButton";
import { ScrollableModal } from "../../../ant.design/ScrollableModal";
import { formatDate } from "../../../../utilities/date-utilities";
import Markdown from "react-markdown";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";

const { Text } = Typography;

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu({ ...props }) {
  const [badgeCount, setBadgeCount] = useState(0);
  const [menuList, setMenuList] = useState([]);
  const [menuListIndex, setMenuListIndex] = useState(0);
  const [cachedAnnouncements, setCachedAnnouncements] = useState([]);
  const [announcement, setAnnouncement] = useState({});
  const [visibility, setVisibility] = useState(false);

  useEffect(() => {
    getUnreadAnnouncements({}).then(({ data }) => {
      setMenuList(data);
      setBadgeCount(data.length);
    });
  }, []);

  function showAnnouncementModal(index) {
    let aID = menuList[index].identifier;
    getAnnouncement({ aID })
      .then((data) => {
        setAnnouncement(data);
        setCachedAnnouncements([data]);
        setMenuListIndex(index);
        setVisibility(true);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  function nextAnnouncement() {
    if (menuListIndex + 1 < menuList.length) {
      let aID = menuList[menuListIndex + 1].identifier;
      let cachedAnnouncement = cachedAnnouncements.find(
        (item) => item.identifier === aID
      );
      if (!cachedAnnouncement) {
        getAnnouncement({ aID })
          .then((data) => {
            setAnnouncement(data);
            setCachedAnnouncements([...cachedAnnouncements, data]);
            setMenuListIndex(menuListIndex + 1);
          })
          .catch(({ message }) => {
            notification.error({ message });
          });
      } else {
        setAnnouncement(cachedAnnouncement);
      }
      setMenuListIndex(menuListIndex + 1);
    }
  }

  function previousAnnouncement() {
    if (menuListIndex > 0) {
      let aID = menuList[menuListIndex - 1].identifier;
      let cachedAnnouncement = cachedAnnouncements.find(
        (item) => item.identifier === aID
      );
      if (!cachedAnnouncement) {
        getAnnouncement({ aID })
          .then((data) => {
            setAnnouncement(data);
            setCachedAnnouncements([data, ...cachedAnnouncements]);
          })
          .catch(({ message }) => {
            notification.error({ message });
          });
      } else {
        setAnnouncement(cachedAnnouncement);
      }
      setMenuListIndex(menuListIndex - 1);
    }
  }

  return (
    <>
      <Menu.SubMenu
        popupClassName="announcement-dd"
        title={
          <Badge count={badgeCount}>
            <IconBell />
          </Badge>
        }
        {...props}
      >
        {menuList.map((item, index) => (
          <Menu.Item key={"announcement_" + index}>
            <LinkButton
              text={item.title}
              onClick={() => {
                showAnnouncementModal(index);
              }}
            />
          </Menu.Item>
        ))}
        <Menu.Divider />
        <Menu.Item key="view_all">
          <LinkButton
            text="View All"
            href={setBaseUrl(`/announcements/user/list`)}
          />
        </Menu.Item>
      </Menu.SubMenu>

      {announcement && announcement.user && (
        <ScrollableModal
          className="t-modal"
          maskClosable={false}
          title={
            <Space align="start">
              <PriorityFlag hasPriority={announcement.priority} />
              <Space direction="vertical">
                <Text strong>{announcement.title}</Text>
                <Text type="secondary" style={{ fontSize: `.8em` }}>
                  {i18n(
                    "Announcements.create.details",
                    announcement.user.username,
                    formatDate({ date: announcement.createdDate })
                  )}
                </Text>
              </Space>
            </Space>
          }
          visible={visibility}
          width="60%"
          onCancel={() => setVisibility(false)}
          footer={[
            <Button
              key="previous"
              disabled={!(menuListIndex > 0)}
              onClick={() => previousAnnouncement()}
            >
              Previous
            </Button>,
            <Button
              key="next"
              disabled={!(menuListIndex + 1 < menuList.length)}
              onClick={() => nextAnnouncement()}
            >
              Next
            </Button>,
          ]}
        >
          <Markdown source={announcement.message} />
        </ScrollableModal>
      )}
    </>
  );
}
