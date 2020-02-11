import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { Icon } from "antd";
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
      <Icon type={icon} style={{ fontSize: 120 }} />
    </div>
    <div>{text}</div>
  </Wrapper>
);

CartNotification.propTypes = {
  text: PropTypes.string.isRequired,
  icon: PropTypes.string.isRequired
};

export default CartNotification;
