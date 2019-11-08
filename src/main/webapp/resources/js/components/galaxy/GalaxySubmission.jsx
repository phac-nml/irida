import React from "react";
import { useStateValue } from "./GalaxyState";
import { Button } from "antd";
import { getI18N } from "../../utilities/i18n-utilities";
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
    { submittable, submitted, email, validEmail, makepairedcollection },
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
                oauthCode: result,
                oauthRedirect: `${window.TL.BASE_URL}galaxy/auth_code`,
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
      {getI18N("GalaxyFinalSubmission.submit")}
    </Button>
  );
}
