import React from "react";
import { Avatar, List, Popover, Tag } from "antd";
import { IconCheck, IconWarning } from "../../../icons/Icons";
import { red6 } from "../../../../styles/colors";
import { ExclamationOutlined } from "@ant-design/icons";

export type SampleQualityParams = {
  qcStatus: string;
  qualities: string[];
};

/**
 * React component to render the quality data for a sample.
 *
 * @param qcStatus
 * @param qualities - list of qc issues associated with the sample
 * @constructor
 */
export default function SampleQuality({
  qcStatus,
  qualities,
}: SampleQualityParams): JSX.Element {
  return qualities.length === 0 ? (
    <>
      {qcStatus === "pass" ? (
        <Tag color="success" icon={<IconCheck />}>
          {i18n("SampleQuality.pass").toUpperCase()}
        </Tag>
      ) : (
        <Tag>{i18n("SampleQuality.na").toUpperCase()}</Tag>
      )}
    </>
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