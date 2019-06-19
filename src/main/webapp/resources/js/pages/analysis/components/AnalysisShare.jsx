import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Card } from "antd";

export function AnalysisShare() {
  const [canShareToSamples, setCanShareToSamples] = useState(true);
  return (
      <>
        <h2 style={{fontWeight: "bold"}}>Results</h2>
        <br />
        <Card
          title="Share Results with Projects"
        >
        </Card>

        <br /><br />

        {canShareToSamples ?
            <Card
              title="Save Results"
            >

            </Card>
            :""
        }
      </>
  );
}
