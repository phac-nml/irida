import { Button, Col, List, Row, Space, Typography } from "antd";
import React from "react";
import { Activity } from "../../../apis/activities/activities";
import { ActivityListItem } from "../../../components/activities/ActivityListItem";
import { BORDERED_LIGHT } from "../../../styles/borders";

export interface RecentActivityListProps {
  activities: Activity[],
  total: number,
  page: number,
  setPage: (value: number) => void,
  loading: boolean,
}

/**
 * Component to display list of recent activity
 * @param activities List of recent activities
 * @param total The total number of recent activities
 * @param page The last loaded page of activities
 * @param setPage Function to set the page number
 * @param loading Whether the data has finished loading or not
 * @constructor
 */
export function RecentActivityList({
  activities,
  total,
  page,
  setPage,
  loading,
}: RecentActivityListProps): JSX.Element {
  return (
    <Row gutter={[16, 16]}>
      <Col span={24}>
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
      </Col>
      <Col span={24}>
        <Space>
          <Button
            className={"t-load-more-button"}
            onClick={() => setPage(page + 1)}
            disabled={total === activities.length}
          >
            {i18n("RecentActivityList.button.loadMore")}
          </Button>
          <Typography.Text className="t-loaded-total">
            {i18n("RecentActivityList.loaded", activities.length, total)}
          </Typography.Text>
        </Space>
      </Col>
    </Row>
  );
}
