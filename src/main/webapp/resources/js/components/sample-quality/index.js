import React from "react";
import { Avatar, List, Popover, Tag } from "antd";
import { ExclamationOutlined } from "@ant-design/icons";
import { red6 } from "../../styles/colors";
import { IconCheck, IconWarning } from "../icons/Icons";

/**
 * React component to render the quality data for a sample.
 *
 * @param {array} qualities - list of qc issues associated with the sample
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleQuality({ qualities }) {
  return qualities.length === 0 ? (
    <Tag color="success" icon={<IconCheck />}>
      {i18n("SampleQuality.pass").toUpperCase()}
    </Tag>
  ) : (
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
      <Tag color="error" icon={<IconWarning />}>
        {i18n("SampleQuality.fail").toUpperCase()}
      </Tag>
    </Popover>
  );
}
