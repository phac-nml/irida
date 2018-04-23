import { connect } from "react-redux";
import { Loader } from "./Loader";

const mapStateToProps = state => ({});
const mapDispatchToProps = dispatch => ({});

export const LoaderContainer = connect(mapStateToProps, mapDispatchToProps)(
  Loader
);
