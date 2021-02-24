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
import { TYPES, useAnnouncements } from "./announcements-context";

const { Text } = Typography;

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu(props) {
  const [{ announcements }, dispatch] = useAnnouncements();

  const [readCount, setReadCount] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [badgeCount, setBadgeCount] = useState(0);
  const [menuListIndex, setMenuListIndex] = useState(0);
  const [cachedAnnouncements, setCachedAnnouncements] = useState([]);
  const [priorityAnnouncements, setPriorityAnnouncements] = useState([]);
  const [priorityAnnouncementsIndex, setPriorityAnnouncementsIndex] = useState(
    0
  );
  const [announcement, setAnnouncement] = useState({});
  const [visibility, setVisibility] = useState(false);

  // useEffect(() => {
  //   getUnreadAnnouncements({})
  //     .then(({ data }) => {
  //       setAnnouncements(data);
  //
  //       setBadgeCount(data.length);
  //       let priorityList = data.filter((item) => item.priority);
  //       setPriorityAnnouncements(priorityList);
  //       setTotalCount(data.length - priorityList.length);
  //       if (priorityList) {
  //         setAnnouncement(priorityList[0]);
  //         setVisibility(true);
  //       }
  //     })
  //     .catch(({ message }) => {
  //       notification.error({ message });
  //     });
  // }, []);

  function showAnnouncementModal(index) {
    dispatch({
      type: TYPES.SHOW_ANNOUNCEMENT,
      payload: {
        index,
      },
    });
    // let aID = announcements[index].identifier;
    // getAnnouncement({ aID })
    //   .then((data) => {
    //     setAnnouncement(data);
    //     setCachedAnnouncements([data]);
    //     setMenuListIndex(index);
    //     setVisibility(true);
    //   })
    //   .catch(({ message }) => {
    //     notification.error({ message });
    //   });
  }

  function nextAnnouncement() {
    if (menuListIndex + 1 < announcements.length) {
      let aID = announcements[menuListIndex + 1].identifier;
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
      let aID = announcements[menuListIndex - 1].identifier;
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
    let aID = announcements[menuListIndex].identifier;
    markAnnouncementRead({ aID })
      .then(() => {
        let newMenuList = announcements.filter(
          (item) => item.identifier !== aID
        );
        setAnnouncements(newMenuList);
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
        let newMenuList = announcements.filter(
          (item) => item.identifier !== aID
        );
        setAnnouncements(newMenuList);
        setBadgeCount(badgeCount - 1);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  // let footerButtons;
  // if (
  //   priorityAnnouncements.length === 1 ||
  //   priorityAnnouncementsIndex + 1 === priorityAnnouncements.length
  // ) {
  //   // only one in priority list or last in priority list
  //   footerButtons = [
  //     <Button key="close" onClick={() => readPriorityAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.close")}
  //     </Button>,
  //   ];
  // } else if (priorityAnnouncements.length > 0) {
  //   // first in priority list
  //   footerButtons = [
  //     <Button key="next" onClick={() => nextPriorityAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.next")}
  //     </Button>,
  //   ];
  // } else if (announcements.length === 1) {
  //   // only one in list
  //   footerButtons = [
  //     <Button key="close" onClick={() => readAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.close")}
  //     </Button>,
  //   ];
  // } else if (menuListIndex === 0) {
  //   // first in list
  //   footerButtons = [
  //     <Button key="next" onClick={() => nextAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.next")}
  //     </Button>,
  //   ];
  // } else if (menuListIndex + 1 === announcements.length) {
  //   // last in list
  //   footerButtons = [
  //     <Button key="previous" onClick={() => previousAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.previous")}
  //     </Button>,
  //     <Button key="close" onClick={() => readAnnouncement()}>
  //       {i18n("AnnouncementsSubMenu.close")}
  //     </Button>,
  //   ];
  // } else {
  //   footerButtons = [
  //     <Button
  //       key="previous"
  //       disabled={!(menuListIndex > 0)}
  //       onClick={() => previousAnnouncement()}
  //     >
  //       {i18n("AnnouncementsSubMenu.previous")}
  //     </Button>,
  //     <Button
  //       key="next"
  //       disabled={!(menuListIndex + 1 < announcements.length)}
  //       onClick={() => nextAnnouncement()}
  //     >
  //       {i18n("AnnouncementsSubMenu.next")}
  //     </Button>,
  //   ];
  // }

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
