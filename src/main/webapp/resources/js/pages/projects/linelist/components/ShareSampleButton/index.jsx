import { Button } from "antd";
import React from "react";
import { connect } from "react-redux";
import { IconShare } from "../../../../../components/icons/Icons";
import { actions as cartActions } from "../../../../../redux/reducers/cart";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

/**
 * React component for the share sample button.  Formats the selected
 * samples into a list that can be consumed by the share samples page,
 * and store them to session storage.
 *
 * @param {array} selected - selected samples
 * @returns {JSX.Element}
 * @constructor
 */
function ShareSamplesButtonComponent({ selected }) {
  function handleShare() {
    const projectId = selected[0]["irida-static-project-id"];
    const samples = selected.map((sample) => ({
      id: sample["irida-static-sample-id"],
      name: sample["irida-static-sample-name"],
      owner: sample.owner,
      projectId: sample["irida-static-project-id"],
      projectName: sample["irida-static-project-name"],
    }));
    window.sessionStorage.setItem(
      "samples",
      JSON.stringify({
        samples,
        projectId,
        timestamp: Date.now(),
      })
    );

    // Redirect user to share page
    window.location.href = setBaseUrl(`/projects/${projectId}/share`);
  }

  return (
    <Button
      className="t-share-button"
      icon={<IconShare />}
      onClick={handleShare}
      disabled={!selected.length}
    >
      {i18n("ShareSampleButton.label")}
    </Button>
  );
}

const mapStateToProps = (state) => ({
  selected: state.entries.selected,
});

const mapDispatchToProps = (dispatch) => ({
  addSamplesToCart: (samples) => dispatch(cartActions.add(samples)),
});

export const ShareSampleButton = connect(
  mapStateToProps,
  mapDispatchToProps
)(ShareSamplesButtonComponent);
