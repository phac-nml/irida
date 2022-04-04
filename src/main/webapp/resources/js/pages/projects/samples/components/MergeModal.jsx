import React from "react";
import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  Modal,
  Radio,
  Row,
  Space,
  Typography
} from "antd";

export default function MergeModal({ samples, visible, onOk }) {
                                                                 const copy = Object.entries(
                                                                   samples
                                                                 ).map(
                                                                   ([
                                                                     ,
                                                                     sample
                                                                   ]) => sample
                                                                 );
                                                                 const [
                                                                   value,
                                                                   setValue
                                                                 ] = React.useState(
                                                                   copy[0].id
                                                                 );
                                                                 const [
                                                                   renameSample,
                                                                   setRenameSample
                                                                 ] = React.useState(
                                                                   false
                                                                 );

                                                                 return (
                                                                   <Modal
                                                                     title="Merge Samples"
                                                                     visible={
                                                                       visible
                                                                     }
                                                                     onOk={onOk}
                                                                     onCancel={
                                                                       onOk
                                                                     }
                                                                   >
                                                                     <Row
                                                                       gutter={[
                                                                         16,
                                                                         16
                                                                       ]}
                                                                     >
                                                                       <Col
                                                                         span={
                                                                           24
                                                                         }
                                                                       >
                                                                         <Alert
                                                                           type="warning"
                                                                           showIcon
                                                                           message={
                                                                             "All samples will be merged into a single sample."
                                                                           }
                                                                           description={
                                                                             "Currently only the metadata from the sample selected will retain its metadata."
                                                                           }
                                                                         />
                                                                       </Col>
                                                                       <Col
                                                                         span={
                                                                           24
                                                                         }
                                                                       >
                                                                         <Typography.Text>
                                                                           The
                                                                           following
                                                                           2
                                                                           samples
                                                                           will
                                                                           be
                                                                           merged
                                                                           into
                                                                           the
                                                                           selected
                                                                           sample.
                                                                         </Typography.Text>
                                                                       </Col>
                                                                       <Col
                                                                         span={
                                                                           24
                                                                         }
                                                                       >
                                                                         <Form layout="vertical">
                                                                           <Form.Item
                                                                             label={
                                                                               "Select primary sample"
                                                                             }
                                                                             validateStatus="warning"
                                                                             help={
                                                                               "Other samples details will be overwritten by the primary sample"
                                                                             }
                                                                             required
                                                                           >
                                                                             <Radio.Group
                                                                               value={
                                                                                 value
                                                                               }
                                                                             >
                                                                               <Space
                                                                                 direction="vertical"
                                                                                 onChange={e =>
                                                                                   setValue(
                                                                                     e
                                                                                       .target
                                                                                       .value
                                                                                   )
                                                                                 }
                                                                                 value={
                                                                                   value
                                                                                 }
                                                                               >
                                                                                 {copy.map(
                                                                                   sample => {
                                                                                     return (
                                                                                       <Radio
                                                                                         value={
                                                                                           sample.id
                                                                                         }
                                                                                         key={`sample-${sample.id}`}
                                                                                       >
                                                                                         {
                                                                                           sample.sampleName
                                                                                         }
                                                                                       </Radio>
                                                                                     );
                                                                                   }
                                                                                 )}
                                                                               </Space>
                                                                             </Radio.Group>
                                                                           </Form.Item>
                                                                           <Form.Item>
                                                                             <Checkbox
                                                                               checked={
                                                                                 renameSample
                                                                               }
                                                                               onChange={e =>
                                                                                 setRenameSample(
                                                                                   e
                                                                                     .target
                                                                                     .checked
                                                                                 )
                                                                               }
                                                                             >
                                                                               Rename
                                                                               Sample
                                                                             </Checkbox>
                                                                           </Form.Item>
                                                                           <Form.Item
                                                                             label={
                                                                               "New sample name"
                                                                             }
                                                                             help={
                                                                               "(Only letters, numbers and - ! @ # $ % ~ ', No spaces or tabs)"
                                                                             }
                                                                           >
                                                                             <Input
                                                                               disabled={
                                                                                 !renameSample
                                                                               }
                                                                             />
                                                                           </Form.Item>
                                                                         </Form>
                                                                       </Col>
                                                                     </Row>
                                                                   </Modal>
                                                                 );
                                                               }
