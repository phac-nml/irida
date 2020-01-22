import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { blue6 } from "../../../styles/colors";

const Wrapper = styled.div`
  font-size: 30px;
  color: ${blue6};
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const CartNotification = ({ text, icon }) => (
  <Wrapper>
    <div>
      <FontAwesomeIcon icon={icon} style={{ fontSize: 120 }} />
    </div>
    <div>{text}</div>
  </Wrapper>
);

CartNotification.propTypes = {
  text: PropTypes.string.isRequired,
  icon: PropTypes.object.isRequired
};

export default CartNotification;
