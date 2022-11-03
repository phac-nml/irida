import React from "react";
import { createRoot } from "react-dom/client";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { Breadcrumbs } from "./Breadcrumbs";
import { setBaseUrl } from "../../utilities/url-utilities";
import { MainNavigation } from "./MainNavigation";
import { AnnouncementProvider } from "./MainNavigation/components/announcements-context";
import { AnnouncementsModal } from "./MainNavigation/components/AnnouncementsModal";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

export function PageHeader() {
  const [inGalaxy, setInGalaxy] = React.useState(false);

  React.useEffect(() => {
    setInGalaxy(typeof window.GALAXY !== "undefined");
  }, []);

  return (
    <>
      <AnnouncementProvider>
        <MainNavigation />
        <AnnouncementsModal />
      </AnnouncementProvider>
      <Breadcrumbs crumbs={window.breadcrumbs} />
      <Session />
      <Notifications />
      {inGalaxy ? <GalaxyAlert /> : null}
    </>
  );
}

const ROOT_ELEMENT = document.querySelector(".js-page-header");
const root = createRoot(ROOT_ELEMENT);
root.render(<PageHeader />);
