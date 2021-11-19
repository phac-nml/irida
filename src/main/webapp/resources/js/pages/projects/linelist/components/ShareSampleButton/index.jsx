import { Button } from "antd";
import React from "react";
import { connect } from "react-redux";
import { actions as cartActions } from "../../../../../redux/reducers/cart";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

function ShareSamplesButtonComponent({ selected }) {
  function handleShare() {
    const projectId = selected[0]["irida-static-project-id"];
    const samples = selected.map((sample) => ({
      id: sample["irida-static-sample-id"],
      name: sample["irida-static-sample-name"],
      owner: sample.ownder,
    }));
    window.sessionStorage.setItem(
      "share",
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
    <Button onClick={handleShare} disabled={!selected.length}>
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
