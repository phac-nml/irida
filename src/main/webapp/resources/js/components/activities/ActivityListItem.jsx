import { Avatar, List, Typography } from "antd";
import isNumeric from "antd/es/_util/isNumeric";
import React from "react";
import { blue6, grey6, red6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  IconCalendarTwoTone,
  IconExperiment,
  IconFile,
  IconUser,
  IconUserDelete,
  IconUsergroupAdd,
  IconUsergroupDelete,
} from "../icons/Icons";

export function ActivityListItem({ activity }) {
  const [title] = React.useState(() => {
    const texts = activity.sentence.split(/{([0-9])}/);
    const content = [];
    for (let i = 0; i < texts.length; i++) {
      const key = `activity-${activity.id}-${i}`;
      if (isNumeric(texts[i])) {
        const item = activity.items[parseInt(texts[i])];
        if (item.href) {
          content.push(
            <Typography.Link key={key} type="link" href={setBaseUrl(item.href)}>
              {item.label}
            </Typography.Link>
          );
        } else {
          content.push(
            <Typography.Text key={key}>{item.label}</Typography.Text>
          );
        }
      } else if (texts[i].length) {
        content.push(<Typography.Text key={key}>{texts[i]}</Typography.Text>);
      }
    }
    return content;
  });

  const typeAvatar = {
    project_user_role_updated: (
      <Avatar style={{ backgroundColor: blue6 }} icon={<IconUser />} />
    ),
    project_sample_added: (
      <Avatar style={{ backgroundColor: blue6 }} icon={<IconExperiment />} />
    ),
    project_sample_data_added: (
      <Avatar style={{ backgroundColor: blue6 }} icon={<IconFile />} />
    ),
    project_user_removed: (
      <Avatar style={{ backgroundColor: red6 }} icon={<IconUserDelete />} />
    ),
    project_user_group_added: (
      <Avatar style={{ backgroundColor: blue6 }} icon={<IconUsergroupAdd />} />
    ),
    project_user_group_removed: (
      <Avatar
        style={{ backgroundColor: red6 }}
        icon={<IconUsergroupDelete />}
      />
    ),
  };

  return (
    <List.Item>
      <List.Item.Meta
        avatar={typeAvatar[activity.type]}
        title={title}
        description={
          <div>
            <IconCalendarTwoTone
              twoToneColor={grey6}
              style={{ marginRight: SPACE_XS }}
            />
            {formatInternationalizedDateTime(activity.date)}
          </div>
        }
      />
    </List.Item>
  );
}
