import { connect } from "react-redux";
import { ToolPanel } from "./ToolPanel";
import { actions } from "../../../reducers/templates";

const mapStateToProps = state => ({
  fields: state.fields.get("fields")
});
const mapDispatchToProps = dispatch => ({
  templateModified: fields => dispatch(actions.modified(fields))
});

export const ToolPanelContainer = connect(mapStateToProps, mapDispatchToProps)(
  ToolPanel
);
