import React from "react";
import { useParams } from "react-router-dom";

import { useGetUserProjectDetailsQuery } from "../../../apis/users/users";

/**
 * React component to display the user projects page.
 * @returns {*}
 * @constructor
 */
export default function UserProjectsPage() {
  const { userId } = useParams();
  const { data, isSuccess } = useGetUserProjectDetailsQuery(userId);

  console.log(userId);
  console.log(data);

  return (
    <div>User Projects Page</div>
  );
}