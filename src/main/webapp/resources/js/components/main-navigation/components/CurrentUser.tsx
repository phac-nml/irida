import React, { useMemo } from "react";
import { useGetCurrentUserQuery } from "../../../redux/endpoints/user";
import { Avatar } from "antd";
import { generateColourForItem } from "../../../utilities/colour-utilities";

/**
 * React component to render an avatar with the users initials.
 * The colour of the avatar is generated from the user id and username
 * @constructor
 */
export default function CurrentUser(): JSX.Element {
  const { data: user, isSuccess } = useGetCurrentUserQuery(undefined, {});

  const colour = useMemo(
    () =>
      isSuccess
        ? generateColourForItem({ id: user.identifier, label: user.username })
        : { text: "transparent", background: "transparent" },
    [isSuccess, user?.identifier, user?.username]
  );

  return (
    <Avatar
      style={{
        backgroundColor: colour.background,
        color: colour.text,
        verticalAlign: "middle",
      }}
    >
      {`${user?.firstName.charAt(0)}${user?.lastName.charAt(0)}`}
    </Avatar>
  );
}
