import React from "react";
import { render } from "react-dom";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { Breadcrumbs } from "./Breadcrumbs";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnnouncementProvider } from "./MainNavigation/components/announcements-context";
import { AnnouncementsModal } from "./MainNavigation/components/AnnouncementsModal";
import { Provider } from "react-redux";
import { store } from "../../redux/store";
import MainNavigation from "../main-navigation";
import { Layout } from "antd";

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
    <Layout>
      <Provider store={store}>
        <Layout.Header>
          <AnnouncementProvider>
            <MainNavigation />
            <AnnouncementsModal />
          </AnnouncementProvider>
        </Layout.Header>
        <Breadcrumbs crumbs={window.breadcrumbs} />
        <Session />
        <Notifications />
        {inGalaxy ? <GalaxyAlert /> : null}
      </Provider>
    </Layout>
  );
}

render(<PageHeader />, document.querySelector(".js-page-header"));
