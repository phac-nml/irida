import React from "react";
import { IconBell } from "../../../icons/Icons";
import { Badge } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { getUnreadAnnouncementsCount } from "../../../../apis/announcements/announcements";

/**
 * React component to display the bell icon and announcement count
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsLink() {
  const [count, setCount] = React.useState(0);

  React.useEffect(() => {
    getUnreadAnnouncementsCount().then((data) => {
      setCount(data.data);
    });
  }, []);

  return (
    <a
      className="t-bell-notification"
      href={setBaseUrl(`/announcements/user/read`)}
    >
      <Badge count={count}>
        <IconBell />
      </Badge>
    </a>
  );
}
