import React from "react";
import { Badge } from "antd";
import { BellOutlined } from "@ant-design/icons";
import { ROUTE_ANNOUNCEMENTS } from "../../../data/routes";
import { useGetAnnouncementCountQuery } from "../../../redux/endpoints/announcements";

export default function AnnouncementLink() {
  const { data: count } = useGetAnnouncementCountQuery(undefined, {});

  return (
    <Badge count={count} offset={[-5, 0]}>
      <a className={"nav-icon"} href={ROUTE_ANNOUNCEMENTS}>
        <BellOutlined />
      </a>
    </Badge>
  );
}
