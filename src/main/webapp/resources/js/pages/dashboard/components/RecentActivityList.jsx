import { Button, List, Space, Typography } from "antd";
import React from "react";
import { ActivityListItem } from "../../../components/activities/ActivityListItem";
import { BORDERED_LIGHT } from "../../../styles/borders";

/**
 * Component to display list of recent activity
 * @param activities List of recent activities
 * @param total The total number of recent activities
 * @param page The last loaded page of activities
 * @param {function} setPage Function to set the page number
 * @param loading Whether the data has finished loading or not
 * @returns {JSX.Element}
 * @constructor
 */
export function RecentActivityList({
  activities,
  total,
  page,
  setPage,
  loading,
}) {
  return (
    <Space direction={"vertical"} style={{ width: `100%` }}>
      <div
        style={{
          maxHeight: 600,
          overflow: "auto",
          border: BORDERED_LIGHT,
          borderLeft: "none",
          borderRight: "none",
        }}
      >
        <List
          bordered
          dataSource={activities}
          loading={loading}
          renderItem={(activity) => <ActivityListItem activity={activity} />}
        />
      </div>
      <Space>
        <Button
          className={"t-load-more"}
          onClick={() => setPage(page + 1)}
          disabled={total === activities.length}
        >
          {i18n("RecentActivityList.button.loadMore")}
        </Button>
        <Typography.Text>
          {i18n("RecentActivityList.loaded", activities.length, total)}
        </Typography.Text>
      </Space>
    </Space>
  );
}
