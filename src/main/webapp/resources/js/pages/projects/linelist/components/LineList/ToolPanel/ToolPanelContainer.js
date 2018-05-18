import { connect } from "react-redux";
import { ToolPanel } from "./ToolPanel";
import { actions } from "../../../reducers/templates";

const getTemplate = (templates, current) => {
  return templates.get(current);
};

const mapStateToProps = state => ({
  template: getTemplate(
    state.templates.get("templates"),
    state.templates.get("current")
  ),
  fields: state.fields.get("fields")
});
const mapDispatchToProps = dispatch => ({
  templateModified: fields => dispatch(actions.modified(fields))
});

export const ToolPanelContainer = connect(mapStateToProps, mapDispatchToProps)(
  ToolPanel
);
