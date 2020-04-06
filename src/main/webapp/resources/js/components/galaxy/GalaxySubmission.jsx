import React from "react";

import { useStateValue } from "./GalaxyState";
import { Button } from "antd";
import { actions } from "./reducer";
import {
  getGalaxySamples,
  removeGalaxySession,
  validateOauthClient
} from "../../apis/galaxy/galaxy";
import { exportToGalaxy } from "../../apis/galaxy/submission";

/**
 * Component to actually send the samples to a Galaxy Client
 * @returns {*}
 */
export function GalaxySubmission() {
  const [
    { submittable, submitted, email, validEmail, makepairedcollection, includeAssemblies },
    dispatch
  ] = useStateValue();

  function submitToGalaxy() {
    validateOauthClient().then(result => {
      switch (result) {
        case "ERROR":
          dispatch(actions.submitError());
          break;
        case "CLOSED":
          dispatch(actions.oauthWindowClosed());
          break;
        default:
          getGalaxySamples().then(samples => {
            // Update the UI
            dispatch(actions.submit());

            // Tell the server that we are done with the galaxy session.
            removeGalaxySession().then(() => {
              // Post to Galaxy
              exportToGalaxy({
                email,
                makepairedcollection,
                includeAssemblies,
                oauthCode: result,
                oauthRedirect: `${window.PAGE.galaxyRedirect}`,
                samples
              });
            });
          });
      }
    });
  }

  return (
    <Button
      type="primary"
      disabled={!validEmail}
      loading={submitted}
      onClick={submitToGalaxy}
    >
      {i18n("GalaxyFinalSubmission.submit")}
    </Button>
  );
}
