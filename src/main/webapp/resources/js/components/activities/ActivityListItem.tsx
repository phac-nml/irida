import { Avatar, List, Typography } from "antd";
import isNumeric from "antd/es/_util/isNumeric";
import React from "react";
import { blue6, blue8, grey6, red6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import styled from "styled-components";

import {
  IconCalendarTwoTone,
  IconExperiment,
  IconFile,
  IconUser,
  IconUserDelete,
  IconUsergroupAdd,
  IconUsergroupDelete,
} from "../icons/Icons";
import { Activity } from "../../apis/activities/activities";

const CustomListItem = styled(List.Item)`
  .ant-list-item-meta-title > a {
    color: ${blue6};
  }
  .ant-list-item-meta-title > a:hover {
    color: ${blue8};
    text-decoration: underline;
  }
`;

export interface ActivityListItemProps {
  activity: Activity;
}

/**
 * Component for rendering an activity (event) within an Ant Design List.
 * @param {Object} activity - the activity to render
 * @returns {JSX.Element}
 * @constructor
 */
export const ActivityListItem = ({ activity } : ActivityListItemProps): JSX.Element => {
  const [title] = React.useState(() => {
    /*
    The description (title) is sent from the server with placeholders, e.g.
    "{0} has been removed from this project" - where {0} is the placed holder
    for the user's name that has been removed.

    items - is an array of the words to put into the description.  The come in
    the order that they go into the description. An item has a label and if required
    a href to link to it.  If it has an href the link will be created, if not
    just the label is displayed.
     */
    const fragments = activity.description.split(/{([0-9])}/);
    const content = [];
    for (let i = 0; i < fragments.length; i++) {
      const key = `activity-${activity.id}-${i}`;
      if (isNumeric(fragments[i])) {
        // If it is numeric, it is one of the placeholder values.
        // get the item and decide how to add it.
        const item = activity.items[parseInt(fragments[i])];

        if (item.href) {
          // If there is a href create a link to the item
          content.push(
            <Typography.Link key={key} href={setBaseUrl(item.href)}>
              {item.label}
            </Typography.Link>
          );
        } else {
          // No href, just add the label
          content.push(
            <Typography.Text key={key}>{item.label}</Typography.Text>
          );
        }
      } else if (fragments[i].length) {
        // If its not numeric, it is just part of the description so just add it.
        content.push(
          <Typography.Text key={key}>{fragments[i]}</Typography.Text>
        );
      }
    }
    return content;
  });

  type ttypeAvatar = {
    [key: string]: JSX.Element;
  }

  /**
   * Different icons for each type of activity
   * @constant
   * @type {{project_user_group_added: JSX.Element, project_user_group_removed: JSX.Element, project_sample_data_added: JSX.Element, project_sample_added: JSX.Element, project_user_role_updated: JSX.Element, project_user_removed: JSX.Element}}
   */
  const typeAvatar : ttypeAvatar = {
    project_user_role_updated: (
      <Avatar
        data-activity={"project_user_role_updated"}
        style={{ backgroundColor: blue6 }}
        icon={<IconUser />}
      />
    ),
    project_sample_added: (
      <Avatar
        data-activity={"project_sample_added"}
        style={{ backgroundColor: blue6 }}
        icon={<IconExperiment />}
      />
    ),
    project_sample_data_added: (
      <Avatar
        data-activity={"project_sample_data_added"}
        style={{ backgroundColor: blue6 }}
        icon={<IconFile />}
      />
    ),
    project_sample_removed: (
      <Avatar
        data-activity={"project_sample_removed"}
        style={{ backgroundColor: red6 }}
        icon={<IconExperiment />}
      />
    ),
    project_user_removed: (
      <Avatar
        data-activity={"project_user_removed"}
        style={{ backgroundColor: red6 }}
        icon={<IconUserDelete />}
      />
    ),
    project_user_group_added: (
      <Avatar
        data-activity={"project_user_group_added"}
        style={{ backgroundColor: blue6 }}
        icon={<IconUsergroupAdd />}
      />
    ),
    project_user_group_removed: (
      <Avatar
        data-activity={"project_user_group_removed"}
        style={{ backgroundColor: red6 }}
        icon={<IconUsergroupDelete />}
      />
    ),
  };

  return (
    <CustomListItem className={"t-activity"}>
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
    </CustomListItem>
  );
}
