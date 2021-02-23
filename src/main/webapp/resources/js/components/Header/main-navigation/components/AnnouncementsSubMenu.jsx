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
  const [priorityAnnouncements, setPriorityAnnouncements] = useState([]);
  const [priorityAnnouncementsIndex, setPriorityAnnouncementsIndex] = useState(
    0
  );
  const [announcement, setAnnouncement] = useState({});
  const [visibility, setVisibility] = useState(false);

  useEffect(() => {
    getUnreadAnnouncements({})
      .then(({ data }) => {
        setMenuList(data);
        setBadgeCount(data.length);
        let priorityList = data.filter((item) => item.priority);
        setPriorityAnnouncements(priorityList);
        setTotalCount(data.length - priorityList.length);
        if (priorityList) {
          setAnnouncement(priorityList[0]);
          setVisibility(true);
        }
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

  function nextPriorityAnnouncement() {
    if (priorityAnnouncementsIndex + 1 < priorityAnnouncements.length) {
      let aID =
        priorityAnnouncements[priorityAnnouncementsIndex + 1].identifier;
      readPriorityAnnouncement();
      getAnnouncement({ aID })
        .then((data) => {
          setAnnouncement(data);
        })
        .catch(({ message }) => {
          notification.error({ message });
        });
    }
  }

  function readPriorityAnnouncement() {
    let aID = priorityAnnouncements[priorityAnnouncementsIndex].identifier;
    markAnnouncementRead({ aID })
      .then(() => {
        if (priorityAnnouncementsIndex + 1 === priorityAnnouncements.length) {
          setPriorityAnnouncements([]);
          setVisibility(false);
        } else {
          setPriorityAnnouncementsIndex(priorityAnnouncementsIndex + 1);
        }
        let newMenuList = menuList.filter((item) => item.identifier !== aID);
        setMenuList(newMenuList);
        setBadgeCount(badgeCount - 1);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  let footerButtons;
  if (
    priorityAnnouncements.length === 1 ||
    priorityAnnouncementsIndex + 1 === priorityAnnouncements.length
  ) {
    // only one in priority list or last in priority list
    footerButtons = [
      <Button key="close" onClick={() => readPriorityAnnouncement()}>
        {i18n("AnnouncementsSubMenu.close")}
      </Button>,
    ];
  } else if (priorityAnnouncements.length > 0) {
    // first in priority list
    footerButtons = [
      <Button key="next" onClick={() => nextPriorityAnnouncement()}>
        {i18n("AnnouncementsSubMenu.next")}
      </Button>,
    ];
  } else if (menuList.length === 1) {
    // only one in list
    footerButtons = [
      <Button key="close" onClick={() => readAnnouncement()}>
        {i18n("AnnouncementsSubMenu.close")}
      </Button>,
    ];
  } else if (menuListIndex === 0) {
    // first in list
    footerButtons = [
      <Button key="next" onClick={() => nextAnnouncement()}>
        {i18n("AnnouncementsSubMenu.next")}
      </Button>,
    ];
  } else if (menuListIndex + 1 === menuList.length) {
    // last in list
    footerButtons = [
      <Button key="previous" onClick={() => previousAnnouncement()}>
        {i18n("AnnouncementsSubMenu.previous")}
      </Button>,
      <Button key="close" onClick={() => readAnnouncement()}>
        {i18n("AnnouncementsSubMenu.close")}
      </Button>,
    ];
  } else {
    footerButtons = [
      <Button
        key="previous"
        disabled={!(menuListIndex > 0)}
        onClick={() => previousAnnouncement()}
      >
        {i18n("AnnouncementsSubMenu.previous")}
      </Button>,
      <Button
        key="next"
        disabled={!(menuListIndex + 1 < menuList.length)}
        onClick={() => nextAnnouncement()}
      >
        {i18n("AnnouncementsSubMenu.next")}
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
            text={i18n("AnnouncementsSubMenu.view-all")}
            href={setBaseUrl(`/announcements/user/list`)}
          />
        </Menu.Item>
      </Menu.SubMenu>
      {announcement && announcement.user && (
        <ScrollableModal
          className="t-modal"
          closable={priorityAnnouncements.length > 0 ? false : true}
          maskClosable={priorityAnnouncements.length > 0 ? false : true}
          title={
            <Space direction="vertical" style={{ width: "100%" }}>
              {priorityAnnouncements.length > 0 ? (
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  {priorityAnnouncements.length > 1
                    ? i18n(
                        "AnnouncementsSubMenu.title.multiple",
                        priorityAnnouncements.length
                      )
                    : i18n("AnnouncementsSubMenu.title.single")}
                  <Tag className="t-read-over-unread-ratio" color="red">
                    {priorityAnnouncementsIndex + 1} /{" "}
                    {priorityAnnouncements.length}
                  </Tag>
                </div>
              ) : (
                <Tag className="t-read-over-unread-ratio">
                  {readCount} / {totalCount}
                </Tag>
              )}
              <Space align="start">
                <PriorityFlag hasPriority={announcement.priority} />
                <Space direction="vertical">
                  <Text strong>{announcement.title}</Text>
                  <Text type="secondary" style={{ fontSize: `.8em` }}>
                    {i18n(
                      "AnnouncementsSubMenu.create.details",
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
