import React, { useState, useContext } from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Alert, Popconfirm, message, Row } from "antd";
import { AnalysisContext } from '../../../state/AnalysisState';
import { showNotification } from "../../../modules/notifications";

import {
    deleteAnalysis
} from "../../../apis/analysis/analysis";

export default function AnalysisDelete() {
    const { state, dispatch } = useContext(AnalysisContext);
    const [deleteConfirm, setDeleteConfirm] = useState(false);
    const [submissionId, setSubmissionId] = useState(window.PAGE.analysis.identifier);

    function onChange(e) {
        if(e.target.checked == true){
            setDeleteConfirm(true);
        }
        else{
            setDeleteConfirm(false);
        }
    }

    function handleDeleteConfirm()
    {
        deleteAnalysis(state.analysis.identifier).then(res =>
            showNotification({ text: res.result})
        );

        window.setTimeout(function() {
          window.location.replace(window.TL.BASE_URL);
        }, 3500);
    }

    return (
        <>
            <h1>{deleteConfirm}</h1>
            <h2 style={{fontWeight: "bold"}}>Delete Analysis</h2>
            <strong className="spaced-top__sm"><Alert message="Warning! Deletion of an analysis is a permanent action!" type="warning" /></strong>
            <Row className="spaced-top__lg">
                <Checkbox onChange={onChange}>Confirm analysis deletion</Checkbox>
            </Row>
            <Row>
                <Popconfirm placement="top" title="Delete Analysis 1?" okText="Confirm" cancelText="Cancel" onConfirm={handleDeleteConfirm}>
                    <Button type="danger" className="spaced-top__lg" disabled={deleteConfirm ? false : true} >Delete</Button>
                </Popconfirm>
            </Row>
        </>
    );
}
