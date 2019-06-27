import React, { useContext, useEffect, useState } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Card, Row, Alert } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState'
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";

import {
    getSharedProjects,
    updateSharedProjects,
    saveToRelatedSamples
} from "../../../apis/analysis/analysis";

export default function AnalysisShare() {
    const { state } = useContext(AnalysisContext);
    const [sharedProjects, setSharedProjects] = useState(null);

    function createSharedProjectsList()
    {
        const projectList = [];

        for(let i = 0; i < sharedProjects.length; i++) {
            projectList.push(
                <Row className="spaced-bottom" key={`sharedprojrow${i}`}>
                    <Checkbox
                        key={`sharedproj${i}`}
                        value={sharedProjects[i].project.identifier}
                        onChange={onChange}
                    >
                        {sharedProjects[i].project.name}
                    </Checkbox>
                </Row>
            )
        }
        return projectList;
    }

    function onChange(e)
    {
        updateSharedProjects(
            state.analysis.identifier, e.target.value, e.target.checked
        ).then(res =>
            showNotification({ text: res.message})
        );
    }

    function handleSaveResults()
    {
        saveToRelatedSamples(state.analysis.identifier)
    }

    useEffect(() => {
        getSharedProjects(state.analysis.identifier).then(res =>
            setSharedProjects(res.data)
        );
    }, []);

    return (
      <>
        <h2 style={{fontWeight: "bold"}}>Results</h2>
        <br />
        <Card
          title="Share Results with Projects"
        >
            { sharedProjects !== null ?
                createSharedProjectsList() : null
            }
        </Card>

        <br /><br />

        { state.canShareToSamples ?
            <Card
              title={getI18N("analysis.details.save.samples.title")}
            >
                <p className="spaced_bottom">{getI18N(`workflow.label.share-analysis-samples.${state.analysisType.type}`)}</p>

                    { state.analysis.updateSamples ?
                        <Alert message={getI18N("analysis.details.save.complete")} type="info" />
                     : null }

                <Button
                    type="primary"
                    className="spaced-top"
                    disabled={state.analysis.updateSamples ? true : false}
                    onClick={handleSaveResults}
                >
                    {getI18N("analysis.details.save.samples.button")}
                </Button>
            </Card>
            : null

        }
      </>
    );
}
