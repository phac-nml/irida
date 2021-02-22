import React, { useEffect, useState } from "react";
import {
  Badge,
  Button,
  Menu,
  notification,
  Space,
  Tag,
  Typography,
} from "antd";
import { IconBell } from "../../../icons/Icons";
import {
  getAnnouncement,
  getUnreadAnnouncements,
  markAnnouncementRead,
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
  const [readCount, setReadCount] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [badgeCount, setBadgeCount] = useState(0);
  const [menuList, setMenuList] = useState([]);
  const [menuListIndex, setMenuListIndex] = useState(0);
  const [cachedAnnouncements, setCachedAnnouncements] = useState([]);
  const [announcement, setAnnouncement] = useState({});
  const [visibility, setVisibility] = useState(false);

  useEffect(() => {
    getUnreadAnnouncements({})
      .then(({ data }) => {
        setMenuList(data);
        setBadgeCount(data.length);
        setTotalCount(data.length);
      })
      .catch(({ message }) => {
        notification.error({ message });
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
      readAnnouncement();
      if (!cachedAnnouncement) {
        getAnnouncement({ aID })
          .then((data) => {
            setAnnouncement(data);
            setCachedAnnouncements([...cachedAnnouncements, data]);
          })
          .catch(({ message }) => {
            notification.error({ message });
          });
      } else {
        setAnnouncement(cachedAnnouncement);
        setMenuListIndex(menuListIndex + 1);
      }
    }
  }

  function previousAnnouncement() {
    if (menuListIndex > 0) {
      let aID = menuList[menuListIndex - 1].identifier;
      let cachedAnnouncement = cachedAnnouncements.find(
        (item) => item.identifier === aID
      );
      readAnnouncement();
      if (!cachedAnnouncement) {
        getAnnouncement({ aID })
          .then((data) => {
            setAnnouncement(data);
            setCachedAnnouncements([data, ...cachedAnnouncements]);
            setMenuListIndex(menuListIndex - 1);
          })
          .catch(({ message }) => {
            notification.error({ message });
          });
      } else {
        setAnnouncement(cachedAnnouncement);
      }
    }
  }

  function readAnnouncement() {
    let aID = menuList[menuListIndex].identifier;
    markAnnouncementRead({ aID })
      .then(() => {
        let newMenuList = menuList.filter((item) => item.identifier !== aID);
        setMenuList(newMenuList);
        setBadgeCount(badgeCount - 1);
        setReadCount(readCount + 1);
        if (newMenuList.length === 0) {
          setVisibility(false);
        }
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  let footerButtons;
  // only one in list
  if (menuList.length === 1) {
    footerButtons = [
      <Button key="read" onClick={() => readAnnouncement()}>
        Read & Close
      </Button>,
    ];
  } else if (menuListIndex === 0) {
    // first in list
    footerButtons = [
      <Button key="next" onClick={() => nextAnnouncement()}>
        Next
      </Button>,
    ];
  } else if (menuListIndex + 1 === menuList.length) {
    // last in list
    footerButtons = [
      <Button key="previous" onClick={() => previousAnnouncement()}>
        Previous
      </Button>,
      <Button key="next" onClick={() => nextAnnouncement()}>
        Read & Close
      </Button>,
    ];
  } else {
    footerButtons = [
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
    ];
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
        {menuList.length > 0 && <Menu.Divider />}
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
            <Space direction="vertical" style={{ width: "100%" }}>
              <Tag>
                {readCount} / {totalCount}
              </Tag>
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
            </Space>
          }
          visible={visibility}
          width="90ch"
          onCancel={() => setVisibility(false)}
          footer={footerButtons}
        >
          <div style={{ marginLeft: "25px" }}>
            <Markdown source={announcement.message} />
          </div>
        </ScrollableModal>
      )}
    </>
  );
}
