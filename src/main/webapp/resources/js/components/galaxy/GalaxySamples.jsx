import React from "react";
import PropTypes from "prop-types";
import { getGalaxySamples } from "../../apis/galaxy/galaxy";
import { green6 } from "../../styles/colors";
import { getI18N } from "../../utilities/i18n-utilties";
import { SubmitIcon, SubmitIconProcessing, SubmitStep } from "./components";

export class GalaxySamples extends React.Component {
  propTypes = {
    setSamples: PropTypes.func.isRequired
  };

  state = {
    finished: false
  };

  componentDidMount() {
    getGalaxySamples().then(samples => {
      this.setState({ finished: true }, () => {
        this.props.setSamples(samples);
      });
    });
  }

  render() {
    return this.state.finished ? (
      <SubmitStep>
        <SubmitIcon type="check-circle" theme="twoTone" twoToneColor={green6} />
        {getI18N("GalaxySamples.ready")}
      </SubmitStep>
    ) : (
      <SubmitStep>
        <SubmitIconProcessing type="loading" />
        {getI18N("GalaxySamples.processing")}
      </SubmitStep>
    );
  }
}
