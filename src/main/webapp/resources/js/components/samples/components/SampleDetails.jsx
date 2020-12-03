import React from "react";
import { SampleMetadata } from "./SampleMetadata";
import { Tabs, Typography } from "antd";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { IconCalendarTwoTone } from "../../icons/Icons";
import { SPACE_XS } from "../../../styles/spacing";
import { SampleFiles } from "./SampleFiles";

const { Text } = Typography;

export function SampleDetails({ details }) {
  return (
    <>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <span>
          <IconCalendarTwoTone style={{ marginRight: SPACE_XS }} />
          <Text type="secondary">
            {formatInternationalizedDateTime(details.sample.createdDate)}
          </Text>
        </span>
      </div>
      <Tabs defaultActiveKey="metadata">
        <Tabs.TabPane tab={"METADATA"} key="metadata">
          <SampleMetadata metadata={details.metadata} />
        </Tabs.TabPane>
        <Tabs.TabPane tab={"FILES"} key="files">
          <SampleFiles
            id={details.sample.identifier}
            projectId={details.projectId}
          />
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}
