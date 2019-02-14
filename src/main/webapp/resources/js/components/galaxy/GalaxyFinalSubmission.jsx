import React, { Component } from "react";
import { SubmitIconProcessing, SubmitStep } from "./components";
import { getI18N } from "../../utilities/i18n-utilties";

export class GalaxyFinalSubmission extends Component {
  componentDidMount() {
    const form = document.forms["js-galaxy-form"];
    const input = form.elements["js-query"];
    input.value = JSON.stringify(this.props.query);
    form.submit();
  }

  render() {
    return (
      <SubmitStep>
        <SubmitIconProcessing type="loading" />
        {getI18N("GalaxyFinalSubmission.submitting")}
      </SubmitStep>
    );
  }
}
