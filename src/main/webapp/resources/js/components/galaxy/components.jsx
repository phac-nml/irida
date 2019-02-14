import styled from "styled-components";
import { Icon } from "antd";
import { SPACE_MD, SPACE_SM } from "../../styles/spacing";
import { blue6 } from "../../styles/colors";

export const SubmitStep = styled.div`
  height: 32px;
  display: flex;
  align-items: center;
  margin-bottom: ${SPACE_MD};
`;

export const SubmitIcon = styled(Icon)`
  padding-right: ${SPACE_SM};
  padding-left: 2px;
  font-size: 30px;
`;

export const SubmitIconProcessing = styled(SubmitIcon)`
  color: ${blue6};
`;
