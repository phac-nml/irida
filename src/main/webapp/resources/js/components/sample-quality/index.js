import React from "react";
import { Avatar, List, Popover } from "antd";
import {
  CheckCircleTwoTone,
  CloseCircleTwoTone,
  ExclamationOutlined,
} from "@ant-design/icons";
import { green6, red6 } from "../../styles/colors";

/**
 * React component to render the quality data for a sample.
 *
 * @param {array} qualities - list of qc issues associated with the sample
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleQuality({ qualities }) {
  if (qualities.length === 0) {
    return <CheckCircleTwoTone twoToneColor={green6} />;
  }

  return (
    <Popover
      placement="right"
      content={
        <List
          style={{ width: 350 }}
          size="small"
          dataSource={qualities}
          renderItem={(quality) => (
            <List.Item>
              <List.Item.Meta
                title={quality}
                avatar={
                  <Avatar
                    size={18}
                    style={{ backgroundColor: red6 }}
                    icon={<ExclamationOutlined />}
                  />
                }
              />
            </List.Item>
          )}
        />
      }
    >
      <CloseCircleTwoTone twoToneColor={red6} />
    </Popover>
  );
}
