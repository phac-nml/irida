import React, { useContext } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Card } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState'

export default function AnalysisShare() {
    const { state } = useContext(AnalysisContext);
    return (
      <>
        <h2 style={{fontWeight: "bold"}}>Results</h2>
        <br />
        <Card
          title="Share Results with Projects"
        >
        </Card>

        <br /><br />

        {state.canShareToSamples ?
            <Card
              title="Save Results"
            >

            </Card>
            :""
        }
      </>
    );
}
