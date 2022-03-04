import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";

import { RecentActivity } from "./components/RecentActivity";

/**
 * Component to display dashboard
 * @returns {JSX.Element}
 * @constructor
 */
function Dashboard() {
  return (
    <PageWrapper>
      <RecentActivity />
    </PageWrapper>
  );
}

render(<Dashboard />, document.querySelector("#root"));
